package com.salastroya.bgserver.core.auth

import com.salastroya.bgserver.core.auth.command.ChangePasswordCommand
import com.salastroya.bgserver.core.auth.command.CreateUserCommand
import com.salastroya.bgserver.core.auth.command.UserLoginQuery
import com.salastroya.bgserver.core.auth.exception.UnauthorizedException
import com.salastroya.bgserver.core.auth.model.User
import com.salastroya.bgserver.core.auth.port.UserRepository
import com.salastroya.bgserver.core.auth.service.JWTService
import com.salastroya.bgserver.core.auth.service.PasswordSecurityService
import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class AuthUseCasesTest {

    companion object {
        const val USERNAME = "adri"
        const val PASSWORD = "Elephant2024_"
        const val SALT = "S41tS41t"
        const val IS_ADMIN = true
        const val JWT = "token.token.token"
        val USER = User(USERNAME, PASSWORD, SALT, IS_ADMIN)
    }

    @MockK
    lateinit var jwtService: JWTService

    @MockK
    lateinit var passwordService: PasswordSecurityService

    @MockK
    lateinit var userRepository: UserRepository

    @InjectMockKs
    lateinit var useCases: AuthUseCases

    @Test
    fun findAll(): Unit = runBlocking {
        every { userRepository.findAll() }.returns(flowOf(USER))

        val users = useCases.findAll().toList()

        assertThat(users).hasSize(1)
        assertThat(users[0]).isEqualTo(USER)
    }

    @Test
    fun findByUsername(): Unit = runBlocking {
        coEvery { userRepository.findByUsername(USERNAME) }.returns(USER)

        val user = useCases.findByUsername(USERNAME)

        assertThat(user).isNotNull()
            .isEqualTo(USER)
    }

    @Test
    fun getLoginToken(): Unit = runBlocking {
        coEvery { userRepository.findByUsername(USERNAME) }.returns(USER)
        coEvery { jwtService.createToken(USERNAME, IS_ADMIN) }.returns(JWT)
        coEvery { passwordService.hashPassword(PASSWORD, SALT) }.returns(PASSWORD)

        val token = useCases.getLoginToken(UserLoginQuery(USERNAME, PASSWORD))

        assertThat(token).isNotNull()
            .isEqualTo(JWT)
    }

    @Test
    fun getLoginTokenInvalidPassword(): Unit = runBlocking {
        coEvery { userRepository.findByUsername(USERNAME) }.returns(USER)
        coEvery { jwtService.createToken(USERNAME, IS_ADMIN) }.returns(JWT)
        coEvery { passwordService.hashPassword(PASSWORD, SALT) }.returns(PASSWORD + "1234_")

        assertThrows<UnauthorizedException> { useCases.getLoginToken(UserLoginQuery(USERNAME, PASSWORD)) }
    }

    @Test
    fun getLoginTokenUserDoesNotExist(): Unit = runBlocking {
        coEvery { userRepository.findByUsername(USERNAME) }.returns(null)

        assertThrows<UnauthorizedException> { useCases.getLoginToken(UserLoginQuery(USERNAME, PASSWORD)) }
    }

    @Test
    fun createUser(): Unit = runBlocking {
        val userCaptor = slot<User>()
        coEvery { userRepository.existsByUsername(USERNAME) }.returns(false)
        coEvery { passwordService.isValidPassword(PASSWORD) }.returns(true)
        coEvery { passwordService.generateRandomSalt() }.returns(SALT)
        coEvery { passwordService.hashPassword(PASSWORD, SALT) }.returns("1234")
        coEvery { userRepository.insert(capture(userCaptor)) }.returns(USER)

        val user = useCases.createUser(CreateUserCommand(USERNAME, PASSWORD))

        assertThat(user).isNotNull()
            .isEqualTo(USER)
        assertThat(userCaptor.captured.username).isEqualTo(USERNAME)
        assertThat(userCaptor.captured.password).isEqualTo("1234")
        assertThat(userCaptor.captured.salt).isEqualTo(SALT)
        assertThat(userCaptor.captured.isAdmin).isFalse()
    }

    @Test
    fun createUserInvalidPassword(): Unit = runBlocking {
        coEvery { userRepository.existsByUsername(USERNAME) }.returns(false)
        coEvery { passwordService.isValidPassword(PASSWORD) }.returns(false)
        coEvery { passwordService.generateRandomSalt() }.returns(SALT)
        coEvery { passwordService.hashPassword(PASSWORD, SALT) }.returns(PASSWORD)

        assertThrows<InvalidUseCaseException> { useCases.createUser(CreateUserCommand(USERNAME, PASSWORD)) }
    }

    @Test
    fun createUserAlreadyExists(): Unit = runBlocking {
        coEvery { userRepository.existsByUsername(USERNAME) }.returns(true)

        assertThrows<InvalidUseCaseException> { useCases.createUser(CreateUserCommand(USERNAME, PASSWORD)) }
    }

    @Test
    fun updatePassword(): Unit = runBlocking {
        val newPassword = "1234"
        val userExpectedToBeSaved = USER.copy(password = newPassword)
        coEvery { userRepository.findByUsername(USERNAME) }.returns(USER)
        coEvery { userRepository.update(userExpectedToBeSaved) }.returns(USER)
        coEvery { passwordService.hashPassword(PASSWORD, SALT) }.returns(PASSWORD)
        coEvery { passwordService.hashPassword(newPassword, SALT) }.returns(newPassword)
        coEvery { passwordService.isValidPassword(newPassword) }.returns(true)

        useCases.updatePassword(ChangePasswordCommand(USERNAME, PASSWORD, newPassword))

        coVerify { userRepository.update(userExpectedToBeSaved) }
    }

    @Test
    fun updatePasswordNoExistingUser(): Unit = runBlocking {
        val newPassword = "1234"
        coEvery { userRepository.findByUsername(USERNAME) }.returns(null)

        assertThrows<InvalidUseCaseException> {
            useCases.updatePassword(
                ChangePasswordCommand(
                    USERNAME,
                    PASSWORD,
                    newPassword
                )
            )
        }
    }

    @Test
    fun updatePasswordWrongOldPassword(): Unit = runBlocking {
        val newPassword = "1234"
        coEvery { userRepository.findByUsername(USERNAME) }.returns(USER)
        coEvery { passwordService.hashPassword(PASSWORD, SALT) }.returns("notTheSame")
        coEvery { passwordService.isValidPassword(newPassword) }.returns(true)

        assertThrows<InvalidUseCaseException> {
            useCases.updatePassword(
                ChangePasswordCommand(
                    USERNAME,
                    PASSWORD,
                    newPassword
                )
            )
        }
    }

    @Test
    fun updatePasswordNotValidPassword(): Unit = runBlocking {
        val newPassword = "1234"
        coEvery { userRepository.findByUsername(USERNAME) }.returns(USER)
        coEvery { passwordService.hashPassword(PASSWORD, SALT) }.returns(PASSWORD)
        coEvery { passwordService.isValidPassword(newPassword) }.returns(false)

        assertThrows<InvalidUseCaseException> {
            useCases.updatePassword(
                ChangePasswordCommand(
                    USERNAME,
                    PASSWORD,
                    newPassword
                )
            )
        }
    }

    @Test
    fun delete(): Unit = runBlocking {
        coEvery { userRepository.delete(USERNAME) }.returns(Unit)

        useCases.delete(USERNAME)

        coVerify { userRepository.delete(USERNAME) }
    }
}