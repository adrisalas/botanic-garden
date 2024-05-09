package com.salastroya.bgserver.core.map

import com.salastroya.bgserver.core.map.command.RequestRouteCommand
import com.salastroya.bgserver.core.map.model.Route
import com.salastroya.bgserver.core.map.service.RouteGenerator
import kotlinx.coroutines.TimeoutCancellationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class UserRouteUseCases(
    private val routeUseCases: RouteUseCases,
    private val routeGenerator: RouteGenerator
) {

    suspend fun findRouteOfUser(username: String): Route? {
        return routeUseCases.findByAssociatedUser(username)
    }

    @Transactional
    @Throws(TimeoutCancellationException::class)
    suspend fun requestRoute(command: RequestRouteCommand): Route {
        return routeUseCases.insertUsersRoute(
            Route(
                id = null,
                name = "Private route for user ${command.username}",
                points = routeGenerator.generateBestRouteWith(command.items),
                associatedUser = command.username
            )
        )
    }
}
