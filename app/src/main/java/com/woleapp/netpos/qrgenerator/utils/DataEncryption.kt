package com.woleapp.netpos.qrgenerator.utils

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest

class DataEncryption {
    private val secret: String = "YourSecretKey"
        private val iv: String = "YourSecretKey"
    fun getEncryptionKey(): String {
        val key = MessageDigest.getInstance("SHA-512")
            .digest(secret.toByteArray())
            .take(32)
            .joinToString("") { "%02x".format(it) }
        return key
    }

    fun getEncryptionIV(): String {
        val iv = MessageDigest.getInstance("SHA-512")
            .digest(iv.toByteArray())
            .take(8)
            .joinToString("") { "%02x".format(it) }
        return iv
    }

    fun encryptData(data: String): String? {
        try {
            // Ensure that the secret key is 256 bits (32 bytes)
            val key = SecretKeySpec(getEncryptionKey().toByteArray(), 0, 32, "AES")

            val iv = IvParameterSpec(getEncryptionIV().toByteArray())
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, key, iv)
            val encryptedBytes = cipher.doFinal(data.toByteArray())

            return encryptedBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun decryptData(encryptedData: String): String? {
        try {
            // Ensure that the secret key is 256 bits (32 bytes)
            val key = SecretKeySpec(getEncryptionKey().toByteArray(), 0, 32, "AES")

            val iv = IvParameterSpec(getEncryptionIV().toByteArray())
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, key, iv)

            // Convert the hexadecimal string back to bytes
            val encryptedBytes = ByteArray(encryptedData.length / 2)
            for (i in 0 until encryptedData.length step 2) {
                encryptedBytes[i / 2] = encryptedData.substring(i, i + 2).toInt(16).toByte()
            }

            val decryptedBytes = cipher.doFinal(encryptedBytes)
            return String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}
