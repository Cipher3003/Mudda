import {commonOptions, loadTestOptions, smokeTestOptions, stressTestOptions} from './config.js';
import issueBrowsing from './scenarios/issue_browsing.js';
import userFlow from './scenarios/user_flow.js';

/**
 * Main entry point for Mudda Load Tests.
 * Orchestrates multiple scenarios to simulate realistic traffic.
 */
export const options = {
    ...commonOptions,
    // ...smokeTestOptions
    // ...loadTestOptions
    ...stressTestOptions
};

// Scenario wrappers
export function browsingScenario() {
    issueBrowsing();
}

export function userFlowScenario() {
    userFlow();
}

// Default export if run directly (runs browsing)
export default function () {
    userFlow();
}
