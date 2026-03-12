import http from 'k6/http';
import {check, sleep} from 'k6';
import {BASE_URL} from '../config.js';

/**
 * Scenario: Browse issues, search, clusters and dashboard.
 * Targets: IssueController (GET)
 */
export default function () {
    // 1. List issues with default filters
    const listRes = http.get(`${BASE_URL}/api/v1/issues?page=0&size=20&sortBy=CREATED_AT&sortOrder=desc`);
    check(listRes, {
        'list issues status is 200': (r) => r.status === 200,
        'has issues content': (r) => r.json().content !== undefined,
    });

    const issues = listRes.json().content;
    if (issues && issues.length > 0) {
        // Pick a random issue from the response
        const randomIndex = Math.floor(Math.random() * issues.length);
        const issueId = issues[randomIndex].id;

        // 2. Get issue detail
        const detailRes = http.get(`${BASE_URL}/api/v1/issues/${issueId}`);
        check(detailRes, {
            'issue detail status is 200': (r) => r.status === 200,
        });

        // 3. Get comments for this issue
        const commentsRes = http.get(`${BASE_URL}/api/v1/issues/${issueId}/comments`);
        check(commentsRes, {
            'comments list status is 200': (r) => r.status === 200,
        });
    }

    // 4. Test Clusters (heavy geo-query usually)
    const clusterRes = http.get(`${BASE_URL}/api/v1/issues/clusters?maxLatitude=30.0&maxLongitude=80.0&minLatitude=20.0&minLongitude=70.0&zoomLevel=5`);
    check(clusterRes, {
        'clusters status is 200': (r) => r.status === 200,
    });

    sleep(1);
}
