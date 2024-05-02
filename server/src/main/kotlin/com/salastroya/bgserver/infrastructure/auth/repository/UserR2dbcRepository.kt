package com.salastroya.bgserver.infrastructure.auth.repository

import com.salastroya.bgserver.infrastructure.auth.dto.UserDto
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserR2dbcRepository : CoroutineCrudRepository<UserDto, String> {
    override fun findAll(): Flow<UserDto>
    suspend fun existsByUsername(username: String): Boolean
    suspend fun findByUsername(username: String): UserDto?
    suspend fun insert(user: UserDto): UserDto
    suspend fun update(user: UserDto): UserDto
    suspend fun deleteByUsername(username: String)

}