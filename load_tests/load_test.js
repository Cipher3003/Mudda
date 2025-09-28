import http from 'k6/http';
import { sleep, check } from 'k6';

// 1. Test Configuration (Options)
export const options = {
    vus: 10,       // 10 virtual users
    duration: '30s', // Test runs for 30 seconds
};

// 2. Main Test Logic
export default function () {
    // *** CRITICAL: Use the Docker Compose service name 'backend' ***
    const res = http.get('http://backend:8080/actuator/health');

    // 3. Check for successful response
    check(res, {
        'is status 200': (r) => r.status === 200,
    });

    sleep(1); // Wait 1 second before next request
}