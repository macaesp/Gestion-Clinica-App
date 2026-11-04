package com.example.gestionclinica.data

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Hash de contraseñas con PBKDF2 (disponible desde API 24) y sal por usuario.
 */
object PasswordHasher {
    private const val ITERACIONES = 120_000
    private const val BITS_CLAVE = 256

    fun nuevaSal(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    fun hash(password: String, sal: String): String {
        val spec = PBEKeySpec(
            password.toCharArray(),
            Base64.decode(sal, Base64.NO_WRAP),
            ITERACIONES,
            BITS_CLAVE
        )
        val clave = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).encoded
        return Base64.encodeToString(clave, Base64.NO_WRAP)
    }

    fun verificar(password: String, sal: String, hashEsperado: String): Boolean =
        MessageDigest.isEqual(
            hash(password, sal).toByteArray(),
            hashEsperado.toByteArray()
        )
}