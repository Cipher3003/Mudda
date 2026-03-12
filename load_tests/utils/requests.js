/**
 * Get common headers with Bearer token
 * @param {string} token 
 * @returns {object}
 */
export function getAuthHeaders(token) {
    return {
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
            'X-Client-Type': 'mobile',
        },
    };
}

/**
 * Common GET params
 */
export const commonParams = {
    headers: {
        'X-Client-Type': 'mobile',
    }
};
