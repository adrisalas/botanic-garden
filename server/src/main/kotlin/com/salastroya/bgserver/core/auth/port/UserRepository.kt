package com.salastroya.bgserver.core.auth.port

import com.salastroya.bgserver.core.auth.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun findAll(): Flow<User>
    suspend fun findByUsername(username: String): User?
    suspend fun existsByUsername(username: String): Boolean
    suspend fun insert(user: User): User
    suspend fun update(user: User): User
    suspend fun delete(username: String)
}