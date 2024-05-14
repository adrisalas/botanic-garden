package com.salastroya.bgandroid.services

import com.salastroya.bgandroid.BuildConfig
import com.salastroya.bgandroid.model.Item
import com.salastroya.bgandroid.model.MapPath
import com.salastroya.bgandroid.model.MapRoute
import com.salastroya.bgandroid.services.auth.JWTService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

private interface UserRouteClient {
    @GET("api/map/my-route")
    suspend fun findMyRoute(@Header("authorization") token: String): MapRoute

    @POST("api/map/my-route")
    suspend fun requestMyRoute(
        @Header("authorization") token: String,
        @Body items: List<Item>
    ): MapRoute

    @GET("api/map/routes")
    suspend fun findAllRoutes(): List<MapRoute>

    @GET("api/map/paths")
    suspend fun findAllPaths(): List<MapPath>
}

object UserRouteService {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://${BuildConfig.API_ADDRESS}/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val client: UserRouteClient = retrofit.create(UserRouteClient::class.java)

    suspend fun findMyRoute(): MapRoute {
        val myRoute: MapRoute
        try {
            myRoute = client.findMyRoute("Bearer ${JWTService.jwtStore}")
        } catch (ex: Exception) {
            return MapRoute()
        }
        return myRoute
    }

    suspend fun findAllRoutes(): List<MapRoute> {
        val routes: List<MapRoute>;
        try {
            routes = client.findAllRoutes()
        } catch (ex: Exception) {
            return emptyList()
        }

        return routes;
    }

    suspend fun requestMyRoute(items: List<Item>): MapRoute {
        val myRoute: MapRoute
        try {
            myRoute = client.requestMyRoute(
                "Bearer ${JWTService.jwtStore}",
                items
            )
        } catch (ex: Exception) {
            return MapRoute()
        }
        return myRoute
    }

    suspend fun findAllPaths(): List<MapPath> {
        val paths: List<MapPath>;
        try {
            paths = client.findAllPaths()
        } catch (ex: Exception) {
            return emptyList()
        }

        return paths;
    }

}