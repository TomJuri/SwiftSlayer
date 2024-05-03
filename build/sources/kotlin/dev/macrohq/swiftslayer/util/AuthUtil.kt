package dev.macrohq.swiftslayer.util

import java.security.KeyFactory
import java.security.MessageDigest
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher

object AuthUtil {
    fun getHWID(): String {
        val toEncrypt =
            (System.getenv("COMPUTERNAME") + System.getProperty("user.name") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv(
                "PROCESSOR_LEVEL"
            ))
        val md = MessageDigest.getInstance("MD5")
        md.update(toEncrypt.toByteArray())
        val byteData = md.digest()
        val hexString = StringBuilder()
        for (aByteData in byteData) {
            val hex = Integer.toHexString(0xff and aByteData.toInt())
            if (hex.length == 1) {
                hexString.append('0')
            }
            hexString.append(hex)
        }
        return hexString.toString()
    }

    fun generateRandomString(length: Int): String {
        val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val random = Random()
        val sb = StringBuilder(length)
        for (i in 0 until length) {
            sb.append(characters[random.nextInt(characters.length)])
        }
        return sb.toString()
    }

    fun rsaEncrypt(message: ByteArray, publicKey: RSAPublicKey): String {
        val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedData = cipher.doFinal(message)
        return Base64.getEncoder().encodeToString(Base64.getEncoder().encodeToString(encryptedData).toByteArray())
    }

    fun rsaDecrypt(encryptedText: String, privateKey: RSAPrivateKey): String {
        val encryptedData = Base64.getDecoder().decode(Base64.getDecoder().decode(encryptedText))
        val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedData = cipher.doFinal(encryptedData)
        return String(decryptedData)
    }

    fun getPublicKey(key: String): RSAPublicKey {
        val keyBytes = Base64.getDecoder().decode(key)
        val spec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(spec) as RSAPublicKey
    }

    fun getPrivateKey(key: String): RSAPrivateKey {
        val keyBytes = Base64.getDecoder().decode(key)
        val spec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(spec) as RSAPrivateKey
    }
}