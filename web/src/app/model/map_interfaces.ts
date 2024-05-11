/*................POINTS.............................. */
export interface Map_Point {
    id?: number,
    lat: number,
    lon: number,
    items?: Map_Point_Item[]
}

export interface Map_Point_Item {
    type: string,
    id: string
}

export interface Point_Coordinates {
    lat: number,
    lon: number
}

export function initializeMapPoint(): Map_Point {
    return {
        id: 0,
        lat: 0,
        lon: 0
    };
}

/*...................PATHS .............................. */
export interface Map_Path {
    meters: number,
    pointA: Map_Point,
    pointB: Map_Point
}

export function initializeMapPath(): Map_Path {
    return {
        meters: 0,
        pointA: initializeMapPoint(),
        pointB: initializeMapPoint()
    };
}


/*.....................ROUTES...........................*/
export interface Map_Route {
    id?: number,
    name: string,
    points: Map_Point[]
}

export interface Map_Route_Create {
    id?: number,
    name: string,
    points: number[]
}

export function initializeMapRoute(): Map_Route {
    return {
        id: 0,
        name: '',
        points: []
    };
}


