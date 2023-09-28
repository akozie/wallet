package com.woleapp.netpos.qrgenerator.utils

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object EncryptionUtils {

    private const val AES_MODE = "AES/CBC/PKCS7Padding"
    private const val AES_KEY = "your_secret_key" // Replace with your own secret key
    private const val AES_IV = "your_initialization_vector" // Replace with your own initialization vector

    fun encrypt(text: String): String {
        val cipher = Cipher.getInstance(AES_MODE)
        val secretKey = SecretKeySpec(AES_KEY.toByteArray(), "AES")
        val iv = IvParameterSpec(AES_IV.toByteArray())
        
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv)
        
        val encryptedBytes = cipher.doFinal(text.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    fun decrypt(encryptedText: String): String {
        val cipher = Cipher.getInstance(AES_MODE)
        val secretKey = SecretKeySpec(AES_KEY.toByteArray(), "AES")
        val iv = IvParameterSpec(AES_IV.toByteArray())
        
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
        
        val encryptedBytes = Base64.decode(encryptedText, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }
}
