package com.salastroya.bgserver.core.auth.service

import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class PasswordSecurityServiceTest {
    companion object {
        private const val PEPPER = "pepper"
        private const val SALT = "salt"
        private const val NO_SECURE_PASSWORD = "1234"
    }

    private val service = PasswordSecurityService(PEPPER)

    @Test
    fun hashPasswordWithSaltAndPepper() {
        val expected =
            "8174175fcbdccf45bb4cb174a8b9ecadf36931529bacfb6134101e8ba69fabee26922a4ed48134f59d78447ac2a90c1fc3adb16e36cb934000f59d23c89a8d20"

        val output = service.hashPassword(NO_SECURE_PASSWORD, SALT)

        assertThat(output).isEqualTo(expected)
    }

    @Test
    fun generateRandomSalt() {
        val gen1 = service.generateRandomSalt()
        val gen2 = service.generateRandomSalt()
        val gen3 = service.generateRandomSalt()

        assertThat(gen1)
            .hasSize(10)
            .isNotEqualTo(gen2)
            .isNotEqualTo(gen3)
        assertThat(gen2)
            .hasSize(10)
            .isNotEqualTo(gen3)
        assertThat(gen3)
            .hasSize(10)
    }

    @Test
    fun invalidPasswords() {
        listOf(
            "admin",
            "1234",
            "admin1234",
            "ADMIN",
            "adminadmin",
            "Aa1-",
            "2024Aa-",
            "2024 NoWhiteSpaceAllowed-"
        ).forEach {
            assertThat(service.isValidPassword(it)).isFalse()
        }
    }
}