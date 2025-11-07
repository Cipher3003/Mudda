export const devStages = [
    { duration: '10s', target: 5 },  // Ramp up to 5 VUs over 10s
    { duration: '30s', target: 10 }, // Maintain 10 VUs for 30s
    { duration: '10s', target: 0 },  // Ramp down to 0 VUs over 10s
];

export const smokeStages = [
    { duration: '5s', target: 1 },
    { duration: '10s', target: 2 },
    { duration: '5s', target: 0 },
];

export const loadStages = [
    { duration: '20s', target: 10 },
    { duration: '1m', target: 10 },
    { duration: '10s', target: 20 },
    { duration: '1m', target: 20 },
    { duration: '20s', target: 0 },
];

export const stressStages = [
    { duration: '30s', target: 20 },
    { duration: '30s', target: 50 },
    { duration: '30s', target: 80 },
    { duration: '30s', target: 120 },
    { duration: '20s', target: 0 },
];

export const spikeStages = [
    { duration: '2s', target: 50 },
    { duration: '20s', target: 50 },
    { duration: '10s', target: 0 },
];
