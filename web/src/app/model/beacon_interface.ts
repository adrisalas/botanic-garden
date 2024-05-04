import { Plant } from "./plant_interface";
import { POI } from "./poi_interface";

export interface Beacon {
    id: string;
    item: {
        type: string;
        id: string;
    }
}

export function initializeBeacon(): Beacon {
    return {
        id: '',
        item: {
            type: '',
            id: ''
        }
    };
}

export function initializeBeaconFromAnother(beacon: Beacon): Beacon {
    return {
        id: beacon.id,
        item: {
            type: beacon.item ? beacon.item.type : "",
            id: beacon.item ? beacon.item.id : ""
        }
    }
}

export function returnBeaconWithNullItem(beacon: Beacon) {
    return {
        id: beacon.id,
        item: null
    }
}
