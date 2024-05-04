export interface News {
    id?: number;
    title: string;
    subtitle: string;
    description: string;
    image: string;
    date: string;
}

export function initializeNews(): News {
    return {
        title: '',
        subtitle: '',
        description: '',
        image: '',
        date: ''
    };
}
