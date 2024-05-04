export interface POI {
    id?: number;
    name: string;
    description: string;
    image: string;
}

export function initializePOI(): POI {
    return {
        name: '',
        description: '',
        image: ''
    };
}
