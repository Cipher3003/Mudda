export const BASE_URL = 'http://localhost:8080';

export const commonOptions = {
    thresholds: {
        http_req_failed: ['rate<0.01'], // http errors should be less than 1%
        http_req_duration: ['p(95)<500'], // 95% of requests should be below 500ms
    },
};

export const smokeTestOptions = {
    vus: 1,
    duration: '10s',
};

export const loadTestOptions = {
    stages: [
        {duration: '1m', target: 5},
        {duration: '2m', target: 10},
        {duration: '2m', target: 15},
        {duration: '2m', target: 20},
        {duration: '1m', target: 0},
    ],
};

export const stressTestOptions = {
    stages: [
        {duration: '2m', target: 30},
        {duration: '2m', target: 50},
        {duration: '1m', target: 0},
    ],
};
