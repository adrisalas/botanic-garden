package com.salastroya.bgandroid.services.auth

import com.salastroya.bgandroid.BotanicGardenApplication
import com.salastroya.bgandroid.BuildConfig
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

private interface AuthClient {
    @POST("api/auth/login")
    suspend fun login(@Body userLogin: UserLoginDto): TokenDto

    @POST("api/auth/users")
    suspend fun singUp(@Body createUser: User): User
}

object AuthService {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://${BuildConfig.API_ADDRESS}/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val client: AuthClient = retrofit.create(AuthClient::class.java)

    @Throws(HttpException::class)
    suspend fun login(username: String, password: String) {
        JWTService.storeJwt(
            BotanicGardenApplication.instance,
            client.login(UserLoginDto(username, password)).token
        )
    }

    @Throws(HttpException::class)
    suspend fun singUp(username: String, password: String) {
        client.singUp(User(username, password))
    }

}