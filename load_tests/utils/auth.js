import http from 'k6/http';
import {check} from 'k6';
import {BASE_URL} from '../config.js';

/**
 * Login a user and return the AuthResponse
 * @param {string} username
 * @param {string} password
 */
export function login(username, password) {
    const url = `${BASE_URL}/auth/login`;
    const payload = JSON.stringify({
        username: username,
        password: password,
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'X-Client-Type': 'mobile',
        },
    };

    const res = http.post(url, payload, params);
    if (res.status !== 200) {
        console.warn(`Authentication failed: status: ${res.status} (${res.statusText}) ${res.body} `);
    }

    check(res, {
        'login successful': (r) => r.status === 200,
        'has access token': (r) => r.json().accessToken !== undefined,
    });

    return res.json();
}

/**
 * Register a new user
 * @param {object} userData
 */
export function register(userData) {
    const url = `${BASE_URL}/auth/register`;
    const payload = JSON.stringify(userData);

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'X-Client-Type': 'mobile',
        },
    };

    const res = http.post(url, payload, params);

    check(res, {
        'registration successful': (r) => r.status === 201,
    });

    return res;
}

/**
 * Refresh access token
 * @param {string} refreshToken
 */
export function refresh(refreshToken) {
    const url = `${BASE_URL}/auth/refresh`;
    const payload = JSON.stringify({refreshToken});

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'X-Client-Type': 'mobile',
        },
    };

    const res = http.post(url, payload, params);

    check(res, {
        'refresh successful': (r) => r.status === 200,
    });

    return res.json();
}

/**
 * Logout user
 * @param {string} refreshToken
 */
export function logout(refreshToken) {
    const url = `${BASE_URL}/auth/logout`;
    const payload = JSON.stringify({refreshToken});

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'X-Client-Type': 'mobile',
        },
    };

    return http.post(url, payload, params);
}
