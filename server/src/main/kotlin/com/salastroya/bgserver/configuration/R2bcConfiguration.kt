package com.salastroya.bgserver.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository
import org.springframework.data.relational.repository.query.RelationalEntityInformation
import reactor.core.publisher.Mono

@EnableR2dbcRepositories(
    value = ["com.salastroya.bgserver"],
    repositoryBaseClass = CustomR2bcRepository::class
)
@Configuration
class R2bcConfiguration

class CustomR2bcRepository<T : Any, ID>(
    entity: RelationalEntityInformation<T, ID>,
    val entityOperations: R2dbcEntityOperations,
    converter: R2dbcConverter
) : SimpleR2dbcRepository<T, ID>
    (entity, entityOperations, converter) {

    fun insert(entity: T): Mono<T> {
        return entityOperations.insert(entity)
    }

    fun update(entity: T): Mono<T> {
        return entityOperations.update(entity)
    }

}

