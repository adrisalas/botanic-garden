package com.salastroya.bgandroid.services.beacons

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import com.salastroya.bgandroid.BotanicGardenApplication
import com.salastroya.bgandroid.MainActivity
import com.salastroya.bgandroid.R
import com.salastroya.bgandroid.services.NearbyPlantService
import com.salastroya.bgandroid.services.Routes
import kotlinx.coroutines.runBlocking
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.MonitorNotifier
import org.altbeacon.beacon.Region

private const val I_BEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"
private const val NOTIFICATION_ID_FOREGROUND_SERVICE = -23
private const val NOTIFICATION_CHANNEL_ID = "botanic-ref-notification-id"
private val HARDWARE_MANUFACTURER_CODES = arrayOf(0x004c).toIntArray()
private val REGION = Region("all-beacons", null, null, null)


object BeaconRangerService {

    fun startBeaconScanning() {
        val beaconManager =
            BeaconManager.getInstanceForApplication(BotanicGardenApplication.instance)
        BeaconManager.setDebug(true)

        val parser = BeaconParser().setBeaconLayout(I_BEACON_LAYOUT)
        parser.setHardwareAssistManufacturerCodes(HARDWARE_MANUFACTURER_CODES)
        beaconManager.beaconParsers.add(parser)

        try {
            setupForegroundService()

            //beaconManager.setEnableScheduledScanJobs(false)
            //beaconManager.backgroundBetweenScanPeriod = 0
            //beaconManager.backgroundScanPeriod = 1100

            // beaconManager.setIntentScanningStrategyEnabled(true)

            beaconManager.startMonitoring(REGION)
            beaconManager.startRangingBeacons(REGION)
            // These two lines set up a Live Data observer so this Activity can get beacon data from the Application class
            val regionViewModel = BeaconManager
                .getInstanceForApplication(BotanicGardenApplication.instance)
                .getRegionViewModel(REGION)
            // observer will be called each time the monitored regionState changes (inside vs. outside region)
            regionViewModel.regionState.observeForever(centralMonitoringObserver)
            // observer will be called each time a new list of beacons is ranged (typically ~1 second in the foreground)
            regionViewModel.rangedBeacons.observeForever(centralRangingObserver)
        } catch (e: SecurityException) {
            Log.d(
                BotanicGardenApplication.TAG,
                e.message ?: "Unexpected error starting beaconManager or ForegroundService"
            )
        }
    }

    private fun setupForegroundService() {

        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            Routes.nearbyPlants.toUri(),
            BotanicGardenApplication.instance,
            MainActivity::class.java
        )
        val pendingIntent = PendingIntent.getActivity(
            BotanicGardenApplication.instance,
            0,
            deepLinkIntent,
            PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE
        )

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Plant scanner",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply { description = "Get notifications when the scanner is active." }
        val notificationManager = BotanicGardenApplication.instance.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val notification = Notification
            .Builder(BotanicGardenApplication.instance, channel.id)
            .setContentTitle("Scanning for nearby Plants...")
            .setSmallIcon(R.drawable.icon_beacons)
            .setContentIntent(pendingIntent)
            .build()
        BeaconManager.getInstanceForApplication(BotanicGardenApplication.instance)
            .enableForegroundServiceScanning(notification, NOTIFICATION_ID_FOREGROUND_SERVICE)
    }

    private val centralMonitoringObserver = Observer<Int> { state ->
        when (state) {
            MonitorNotifier.INSIDE -> {
                Log.d(null, "inside beacon region: $REGION")
                sendNotification()
            }

            MonitorNotifier.OUTSIDE -> {
                Log.d(null, "outside beacon region: $REGION")
            }
        }
    }

    private val centralRangingObserver = Observer<Collection<Beacon>> { beacons ->
        Log.d(null, "Ranged: ${beacons.count()} beacons")
        for (beacon: Beacon in beacons) {
            NearbyBeaconService.add(beacon)
        }
    }

    private fun sendNotification() = runBlocking {
        val notificationMessage =
            when (val count = NearbyPlantService.getNearbyPlants().count()) {
                0 -> "No plants nearby."
                1 -> "There is a plant nearby."
                else -> "There are $count plants nearby."
            }
        val builder =
            NotificationCompat.Builder(
                BotanicGardenApplication.instance,
                NOTIFICATION_CHANNEL_ID
            )
                .setContentTitle("Botanic Garden Application")
                .setSmallIcon(R.drawable.icon_beacons)
                .setContentText(notificationMessage)
        val stackBuilder = TaskStackBuilder.create(BotanicGardenApplication.instance)
        stackBuilder.addNextIntent(
            Intent(
                BotanicGardenApplication.instance,
                MainActivity::class.java
            )
        )
        val resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE
        )
        builder.setContentIntent(resultPendingIntent)
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Plants nearby notification", NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "My Notification Channel Description"
        val notificationManager = BotanicGardenApplication.instance.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        builder.setChannelId(channel.id)
        notificationManager.notify(1, builder.build())
    }

    fun stopBeaconScanning() {
        val beaconManager =
            BeaconManager.getInstanceForApplication(BotanicGardenApplication.instance)

        beaconManager.stopMonitoring(REGION)
        beaconManager.stopRangingBeacons(REGION)
    }
}
