package com.salastroya.bgserver.core.auth

import com.salastroya.bgserver.core.auth.command.ChangePasswordCommand
import com.salastroya.bgserver.core.auth.command.CreateUserCommand
import com.salastroya.bgserver.core.auth.command.UserLoginQuery
import com.salastroya.bgserver.core.auth.exception.UnauthorizedException
import com.salastroya.bgserver.core.auth.model.User
import com.salastroya.bgserver.core.auth.port.UserRepository
import com.salastroya.bgserver.core.auth.service.JWTService
import com.salastroya.bgserver.core.auth.service.PasswordSecurityService
import com.salastroya.bgserver.core.auth.service.PasswordSecurityService.Companion.PASSWORD_VALIDATION_MESSAGE
import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthUseCases(
    private val jwtService: JWTService,
    private val passwordService: PasswordSecurityService,
    private val userRepository: UserRepository
) {
    fun findAll(): Flow<User> {
        return userRepository.findAll()
    }

    suspend fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    @Throws(InvalidUseCaseException::class)
    suspend fun getLoginToken(userLoginQuery: UserLoginQuery): String {
        val (username, password) = userLoginQuery
        val user = findByUsername(username)
            ?: throw UnauthorizedException("Username or password are incorrect")

        val expectedHashedPassword = passwordService.hashPassword(password, user.salt)
        if (expectedHashedPassword != user.password) {
            throw UnauthorizedException("Username or password are incorrect")
        }

        return jwtService.createToken(user.username, user.isAdmin)
    }

    @Transactional
    @Throws(InvalidUseCaseException::class)
    suspend fun createUser(createUserCommand: CreateUserCommand): User {
        val (username, password) = createUserCommand
        if (userRepository.existsByUsername(username)) {
            throw InvalidUseCaseException("User with username $username already exists")
        }

        if (!passwordService.isValidPassword(password)) {
            throw InvalidUseCaseException(PASSWORD_VALIDATION_MESSAGE)
        }

        val salt = passwordService.generateRandomSalt()
        val hashedPassword = passwordService.hashPassword(password, salt)

        return userRepository.insert(
            User(username, hashedPassword, salt)
        )
    }

    @Transactional
    @Throws(InvalidUseCaseException::class)
    suspend fun updatePassword(updatePasswordCommand: ChangePasswordCommand): User {
        val (username, oldPassword, newPassword) = updatePasswordCommand
        val user = userRepository.findByUsername(username)
            ?: throw InvalidUseCaseException("User with username $username does not exist")

        val expectedHashedPassword = passwordService.hashPassword(oldPassword, user.salt)
        if (expectedHashedPassword != user.password) {
            throw InvalidUseCaseException("Old password is not correct")
        }

        if (!passwordService.isValidPassword(newPassword)) {
            throw InvalidUseCaseException(PASSWORD_VALIDATION_MESSAGE)
        }

        val newHashedPassword = passwordService.hashPassword(newPassword, user.salt)

        return userRepository.update(user.copy(password = newHashedPassword))
    }

    @Transactional
    suspend fun delete(username: String) {
        userRepository.delete(username)
    }
}