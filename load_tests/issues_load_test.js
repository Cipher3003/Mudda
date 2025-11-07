import http from 'k6/http';
import { sleep, check, group } from 'k6';

// --- Test Setup ---
const BASE_URL = 'http://backend:8080/api/v1/issues';
// NOTE: For 'backend', ensure your docker-compose or local setup is correct.

// 1. Test Configuration (Options)
export const options = {
    // A ramp-up/ramp-down scenario to simulate realistic load changes
    stages: [
        { duration: '10s', target: 5 },  // Ramp up to 5 VUs over 10s
        { duration: '30s', target: 10 }, // Maintain 10 VUs for 30s
        { duration: '10s', target: 0 },  // Ramp down to 0 VUs over 10s
    ],

    // 2. Real Performance Metrics (Thresholds)
    // These define the pass/fail criteria for the whole test.
    thresholds: {
        // Global Check: 95% of all requests must complete within 300ms
        'http_req_duration': ['p(95) < 300'],

        // Global Check: Less than 1% of all requests can fail
        'http_req_failed': ['rate < 0.01'],

        // Endpoint Specific Checks: Organize by 'name' tag
        // 'List Issues' endpoint P95 should be under 500ms (as it involves DB queries and pagination)
        'http_req_duration{name:list_issues}': ['p(95) < 500'],

        // 'Create Issue' endpoint P95 should be under 150ms
        'http_req_duration{name:create_issue}': ['p(95) < 150'],

        // The overall success rate of all checks must be 100%
        'checks': ['rate == 1.0'],
    },
};

// Data to hold the ID of a created issue for subsequent requests
let createdIssueId = 0;

// 3. Main Test Logic - The User Flow
export default function () {
    // Use a 'group' to logically segment the full flow.
    group('Issue Controller API Performance Test', function () {

        // =================================================================
        // 1. GET: List Issues (Simulating a user viewing the main feed)
        // =================================================================
        group('1. List All Issues /api/v1/issues (GET)', function () {
            const listRes = http.get(`${BASE_URL}?page=0&size=10&sortOrder=desc`, {
                // Use the 'tags' property to give the request a unique name for metrics
                tags: { name: 'list_issues' },
            });

            check(listRes, {
                'List Status is 200': (r) => r.status === 200,
            });
        });

        sleep(1); // Wait 1 second

        // =================================================================
        // 2. POST: Create Issue (Simulating a user submitting a new issue)
        // =================================================================
        group('2. Create New Issue /api/v1/issues (POST)', function () {
            const payload = JSON.stringify({
                title: `Test Issue ${__VU}-${__ITER}`,
                description: 'Performance test creation.',
                reporter_id: 1,
                location_id: 1,
                category_id: 1
                // ... include other required fields like location, status, etc.
            });

            const params = {
                headers: { 'Content-Type': 'application/json' },
                tags: { name: 'create_issue' },
            };

            const createRes = http.post(BASE_URL, payload, params);

            check(createRes, {
                'Create Status is 200': (r) => r.status === 200,
                'Response has ID': (r) => r.json() && r.json().id !== undefined,
            });

            // Store the ID for the next steps in the flow
            if (createRes.json() && createRes.json().id) {
                createdIssueId = createRes.json().id;
            }
        });

        sleep(0.5);

        // =================================================================
        // 3. GET: Get Issue By ID (Simulating a user viewing a specific issue)
        // =================================================================
        if (createdIssueId > 0) {
            group('3. Get Issue By ID /api/v1/issues/{id} (GET)', function () {
                const getRes = http.get(`${BASE_URL}/${createdIssueId}`, {
                    tags: { name: 'get_issue_by_id' },
                });

                check(getRes, {
                    'Get by ID Status is 200': (r) => r.status === 200,
                });
            });
            sleep(0.5);

            // =================================================================
            // 4. PUT: Update Issue (Simulating a user editing an issue)
            // =================================================================
            group('4. Update Issue /api/v1/issues/{id} (PUT)', function () {
                const updatePayload = JSON.stringify({
                    // Only send fields required for update
                    title: `Updated Issue ${__VU}-${__ITER}`,
                    // ... other update fields
                });

                const params = {
                    headers: { 'Content-Type': 'application/json' },
                    tags: { name: 'update_issue' },
                };

                const updateRes = http.put(`${BASE_URL}/${createdIssueId}`, updatePayload, params);

                check(updateRes, {
                    'Update Status is 200': (r) => r.status === 200,
                });
            });
            sleep(0.5);

            // =================================================================
            // 5. DELETE: Delete Issue (Simulating a user deleting an issue)
            // =================================================================
            group('5. Delete Issue /api/v1/issues/{id} (DELETE)', function () {
                const deleteRes = http.del(`${BASE_URL}/${createdIssueId}`, null, {
                    tags: { name: 'delete_issue' },
                });

                check(deleteRes, {
                    'Delete Status is 204': (r) => r.status === 204, // 204 No Content
                });
            });
        }
    });

    sleep(1);
}