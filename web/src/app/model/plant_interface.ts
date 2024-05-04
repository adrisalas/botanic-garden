export interface Plant {
    id?: number;
    commonName: string;
    scientificName: string;
    description: string;
    image: string;
    type: string;
    details: PlantDetail
}

export interface PlantDetail {
    season: string;
    leafType: string;
    water: string;
    flowering: {
        first: string;
        second: string;
    }
}

export function initializePlant(): Plant {
    return {
        commonName: '',
        scientificName: '',
        description: '',
        image: '',
        type: '',
        details: {
            season: '',
            leafType: '',
            water: '',
            flowering: {
                first: '',
                second: ''
            }
        }
    };
}

export function checkOrSetNullValue(keyName: string, plant: any) {
    if (plant[keyName] == "") {
        plant[keyName] = null;
        return;
    }

    if (plant.details && plant.details[keyName] == "") {
        plant.details[keyName] = null;
        return;
    }

    if (plant.details && plant.details.flowering && plant.details.flowering[keyName] == "") {
        plant.details.flowering[keyName] = null;
        return;
    }
}

export interface User {
    username: string;
    fullName?: string;
    password: string;
}


export interface SelectMonth {
    name: string;
    value: string;
}


export const allMonths: SelectMonth[] = [
    {
        name: "January",
        value: "JANUARY"
    },
    {
        name: "February",
        value: "FEBRUARY"
    },
    {
        name: "March",
        value: "MARCH"
    },
    {
        name: "April",
        value: "APRIL"
    },
    {
        name: "May",
        value: "MAY"
    },
    {
        name: "June",
        value: "JUNE"
    },
    {
        name: "July",
        value: "JULY"
    },
    {
        name: "August",
        value: "AUGUST"
    },
    {
        name: "September",
        value: "SEPTEMBER"
    },
    {
        name: "October",
        value: "OCTOBER"
    },
    {
        name: "November",
        value: "NOVEMBER"
    },
    {
        name: "December",
        value: "DECEMBER"
    }
]
