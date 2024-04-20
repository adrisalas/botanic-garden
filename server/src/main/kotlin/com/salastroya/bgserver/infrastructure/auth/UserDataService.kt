package com.salastroya.bgserver.infrastructure.auth

import com.salastroya.bgserver.core.auth.model.User
import com.salastroya.bgserver.core.auth.port.UserRepository
import com.salastroya.bgserver.infrastructure.auth.mapper.toDto
import com.salastroya.bgserver.infrastructure.auth.mapper.toModel
import com.salastroya.bgserver.infrastructure.auth.repository.UserR2bcRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class UserDataService(
    private val repository: UserR2bcRepository
) : UserRepository {
    override fun findAll(): Flow<User> {
        return repository.findAll()
            .map { it.toModel() }
    }

    override suspend fun findByUsername(username: String): User? {
        return repository.findByUsername(username)?.toModel()
    }

    override suspend fun existsByUsername(username: String): Boolean {
        return repository.existsByUsername(username)
    }

    override suspend fun insert(user: User): User {
        return repository.insert(user.toDto()).toModel()
    }

    override suspend fun update(user: User): User {
        return repository.update(user.toDto()).toModel()
    }

    override suspend fun delete(username: String) {
        return repository.deleteByUsername(username)
    }
}