package com.salastroya.bgandroid.pages

import android.content.Context
import android.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.salastroya.bgandroid.R
import com.salastroya.bgandroid.components.LoadingSpinner
import com.salastroya.bgandroid.model.MapPath
import com.salastroya.bgandroid.model.MapPoint
import com.salastroya.bgandroid.model.MapRoute
import com.salastroya.bgandroid.services.UserRouteService
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM
import org.osmdroid.views.overlay.Marker.ANCHOR_CENTER
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


@Composable
fun CustomMap(navController: NavController) {
    val context = LocalContext.current
    val routeNav =
        navController.previousBackStackEntry?.savedStateHandle?.get<MapRoute>("ROUTE")
    var loading by remember { mutableStateOf(false) }
    var allPaths by remember { mutableStateOf(emptyList<MapPath>()) }

    LaunchedEffect(Unit) {
        loading = true
        allPaths = UserRouteService.findAllPaths()
        loading = false
    }

    if (!allPaths.isNullOrEmpty()) {
        // Initialize osmdroid configuration
        Configuration.getInstance()
            .load(context, context.getSharedPreferences("OSMDroid", Context.MODE_PRIVATE))

        // Create MapView
        val mapView = MapView(context)
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.controller.setCenter(GeoPoint(43.52087, -5.61553))
        mapView.controller.setZoom(20.0)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        mapView.setMultiTouchControls(true)


        //Markers for items in the points
        if (!routeNav?.points.isNullOrEmpty()) {
            routeNav?.points?.forEach { point ->
                if (!point.items.isNullOrEmpty()) {
                    val marker = Marker(mapView)
                    marker.position = pointToGeoPoint(point)
                    marker.icon = ContextCompat.getDrawable(context, R.drawable.point_pin)
                    marker.setAnchor(ANCHOR_CENTER, ANCHOR_BOTTOM)
                    mapView.overlays.add(marker)
                    marker.infoWindow = null //Disable popup
                }
            }
        }

        /*.....SELECTED ROUTE..... */
        val route = Polyline().apply {
            if (routeNav != null && !routeNav.points.isNullOrEmpty()) {
                pointsToGeoPoints(routeNav.points).forEach { addPoint(it) }
                outlinePaint.color = Color.rgb(3, 7, 252)
                outlinePaint.strokeWidth = 20f
                outlinePaint.alpha = 100
            }
        }
        mapView.overlayManager.add(route)

        /*.....PAINT ALL PATHS...... */
        allPaths.forEach { path ->
            val defaultPath = Polyline().apply {
                addPoint(pointToGeoPoint(path.pointA))
                addPoint(pointToGeoPoint(path.pointB))
                outlinePaint.color = Color.rgb(153, 73, 12)
                outlinePaint.strokeWidth = 10f
                outlinePaint.alpha = 80
            }
            mapView.overlayManager.add(defaultPath)
        }


        /*val route = Polyline().apply {
        addPoint(severalPoints[severalPoints.size - 3])
        addPoint(severalPoints[severalPoints.size - 2])
        addPoint(severalPoints[severalPoints.size - 1])
        outlinePaint.color = Color.Blue.toArgb()
        outlinePaint.strokeWidth = 15f
        outlinePaint.alpha = 180
    }
    mapView.overlayManager.add(route)*/
        val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
        myLocationOverlay.enableMyLocation()
        mapView.overlayManager.add(myLocationOverlay)


        // Compose MapView
        Column(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                { mapView }
            )
        }

        DisposableEffect(Unit) {
            onDispose {
                mapView.onDetach()
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            LoadingSpinner("Loading...")
        }
    }

}

fun pointsToGeoPoints(points: List<MapPoint>): List<GeoPoint> {
    var geoPointsList = mutableListOf<GeoPoint>()
    points.forEach {
        geoPointsList.add(GeoPoint(it.lat.toDouble(), it.lon.toDouble()))
    }
    return geoPointsList;
}

fun pointToGeoPoint(point: MapPoint): GeoPoint {
    return GeoPoint(point.lat.toDouble(), point.lon.toDouble())
}