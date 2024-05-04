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
