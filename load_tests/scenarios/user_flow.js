import http from 'k6/http';
import {check, sleep} from 'k6';
import {BASE_URL} from '../config.js';
import {login, logout, refresh} from '../utils/auth.js';
import {getAuthHeaders} from '../utils/requests.js';
import {SharedArray} from 'k6/data';
import papaparse from 'https://jslib.k6.io/papaparse/5.1.1/index.js';

// Load users from CSV for authenticating unique virtual users
const users = new SharedArray('users', function () {
    return papaparse.parse(open('../users.csv'), {header: true}).data;
});

/**
 * Scenario: Complete user interaction flow.
 * Targets: AuthController, AccountController, IssueController, CommentController
 */
export default function () {
    // Pick a user based on VU ID to avoid collisions
    const userIndex = (__VU - 1) % users.length;
    const testUser = users[userIndex];

    // 1. Auth: Login
    const authData = login(testUser.username, testUser.password);
    if (!authData.accessToken) {
        console.error(`Login failed for user ${testUser.username}`);
        return;
    }

    const headers = getAuthHeaders(authData.accessToken);

    // 2. Account: Get current user info
    const meRes = http.get(`${BASE_URL}/api/v1/account/me`, headers);
    check(meRes, {
        'get account info is 200': (r) => r.status === 200,
        'username matches': (r) => r.json().username === testUser.username,
    });

    // 3. Issue: Create a new issue
    const issuePayload = JSON.stringify({
        title: `Vulnerability Report ${Date.now()}_${__VU}`,
        description: 'Detailed description for load testing purpose.',
        location_id: 1, // Fallback ID, should ideally be dynamic
        category_id: 1,
        media_urls: [],
    });

    const createIssueRes = http.post(`${BASE_URL}/api/v1/issues`, issuePayload, headers);
    check(createIssueRes, {
        'create issue status is 201': (r) => r.status === 201,
    });

    let issueId;

    if (createIssueRes.status === 201) {
        issueId = createIssueRes.json().id;

        // 4. Comment: Post a comment on the new issue
        const commentPayload = JSON.stringify({
            text: `Automated comment from VU ${__VU}`,
        });
        const commentRes = http.post(`${BASE_URL}/api/v1/issues/${issueId}/comments`, commentPayload, headers);
        check(commentRes, {
            'post comment status is 201': (r) => r.status === 201,
        });

        if (commentRes.status === 201) {
            const commentId = commentRes.json().comment_id;

            // 5. Comment: Like the comment
            const likeRes = http.post(`${BASE_URL}/api/v1/comments/${commentId}/like`, null, headers);
            check(likeRes, {
                'like comment status is 200': (r) => r.status === 200,
            });

            // 6. Comment: Reply to the comment
            const replyPayload = JSON.stringify({text: 'This is a reply'});
            const replyRes = http.post(`${BASE_URL}/api/v1/comments/${commentId}/replies`, replyPayload, headers);
            check(replyRes, {
                'post reply status is 201': (r) => r.status === 201,
            });
        }
    }

    // 7. Account: View my issues
    const myIssuesRes = http.get(`${BASE_URL}/api/v1/account/me/issues?page=0&size=10`, headers);
    check(myIssuesRes, {
        'get user issues is 200': (r) => r.status === 200,
    });

    // 8. Auth: Refresh Token (Simulate session maintenance)
    refresh(authData.refreshToken)

    // 9. Cleanup: Remove the issue posted
    if (issueId) {
        const deleteIssueRes = http.del(`${BASE_URL}/api/v1/issues/${issueId}`, null, headers);
        check(deleteIssueRes, {
            'delete issue status is 204': (r) => r.status === 204,
        })
    }

    // 10. Cleanup (Optional/Simulated): Logout
    // Normally logout might not be called in every iteration of a load test unless testing auth churn
    sleep(1);
    logout(authData.refreshToken)

    sleep(2);
}
