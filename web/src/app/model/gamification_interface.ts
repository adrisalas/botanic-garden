export interface User_Points {
    username: string;
    points: number;
}

export function initializeUserPoints(): User_Points {
    return {
        username: '',
        points: 0
    };
}