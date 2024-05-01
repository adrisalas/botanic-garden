package com.salastroya.bgserver.core.auth.service

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Hex
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.security.Security

private const val SHA_512 = "SHA-512"
private const val PROVIDER_BOUNCY_CASTLE = "BC"

@Service
class PasswordSecurityService(
    @Value("\${custom.security.pepper}") private val pepper: String,
) {

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    companion object {
        const val PASSWORD_VALIDATION_MESSAGE =
            "Password should contain a number, a lowercase, an uppercase, a symbol, no whitespaces and be at least 8 chars long"
        private val SALT_VALUES = mapOf(
            "lowercase" to "abcdefghijklmnopqrstuvwz",
            "uppercase" to "ABCDEFGHIJKLMNOPQRSTUVWZ",
            "symbol" to "~`!@#%^&*()_-+={[}]|:;'<,>.?/",
            "number" to "0123456789"
        )
    }

    fun isValidPassword(password: String): Boolean {
        val regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@\\-_#$%^&+=!])(?=\\S+$).{8,}$".toRegex()
        return regex.matches(password)
    }


    fun generateRandomSalt(): String {
        val sb = StringBuilder()
        repeat(10) {
            sb.append(SALT_VALUES.values.random().random())
        }
        return sb.toString()
    }

    fun hashPassword(password: String, salt: String): String {
        val saltAndPepperPassword = "$pepper$password$salt"

        val messageDigest = MessageDigest.getInstance(SHA_512, PROVIDER_BOUNCY_CASTLE)
        val digest = messageDigest.digest(saltAndPepperPassword.toByteArray())

        return Hex.toHexString(digest)
    }
}