package com.salastroya.bgandroid.services.beacons

import com.salastroya.bgandroid.services.TelemetryService
import com.salastroya.bgandroid.services.beacons.BeaconService.normalizeId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.altbeacon.beacon.Beacon

/**
 * Due to beacons emitting not continuous their data, during the scanning
 * we may not get all the beacons nearby, that is why this class uses
 * a time window of seen beacons
 */
object NearbyBeaconService {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private const val TIME_WINDOW_MILLIS = 10_000
    private var beaconSeen = emptyList<Beacon>()

    fun add(beacon: Beacon) {
        val currentBeacons = getNearbyBeacons()
        val isNewBeacon = !currentBeacons.contains(beacon)

        if (isNewBeacon) {
            serviceScope.launch {
                TelemetryService.sendTelemetry(beacon.normalizeId())
            }
        }

        beaconSeen = currentBeacons
            .filter { it != beacon }
            .plus(beacon)
    }

    fun getNearbyBeacons(): List<Beacon> {
        return beaconSeen.filter { it.wasSeenInTimeWindow() }
    }

    private fun Beacon.wasSeenInTimeWindow(): Boolean {
        return (System.currentTimeMillis() - this.lastCycleDetectionTimestamp) < TIME_WINDOW_MILLIS
    }
}