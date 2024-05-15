import { Plant } from "./plant_interface"
import { POI } from "./poi_interface"

export interface StatVisitorsDay {
    day: string,
    count: number
}


export interface StatVisitorsHour {
    hour: string,
    count: number
}

export interface MostVisitedPlants {
    plant: Plant,
    count: number
}

export interface MostVisitedPOIs {
    poi: POI,
    count: number
}
