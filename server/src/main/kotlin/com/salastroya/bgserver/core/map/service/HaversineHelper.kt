package com.salastroya.bgserver.core.map.service

import com.salastroya.bgserver.core.map.model.Point
import java.lang.Math.toRadians
import kotlin.math.*


const val EARTH_RADIUS_IN_METERS = 6_372_800

//https://rosettacode.org/wiki/Haversine_formula
fun distanceBetweenTwoGeoPointsInMeters(pointA: Point, pointB: Point): Double {
    val latA = toRadians(pointA.lat)
    val latB = toRadians(pointB.lat)
    val lonA = pointA.lon
    val lonB = pointB.lon
    val dLat = latB - latA
    val dLon = toRadians(lonB - lonA)

    val a = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(latA) * cos(latB)
    val c = 2 * asin(sqrt(a))
    return (EARTH_RADIUS_IN_METERS * c).roundToDecimal(2)
}

fun Double.roundToDecimal(decimals: Int): Double {
    val multiplier = 10.0.pow(decimals.toDouble())
    return (this * multiplier).roundToInt() / multiplier
}