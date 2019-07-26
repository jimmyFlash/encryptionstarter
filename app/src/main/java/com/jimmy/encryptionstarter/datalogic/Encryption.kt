/*
 * Copyright (c) 2019 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.jimmy.encryptionstarter.datalogic

import android.annotation.TargetApi
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import com.jimmy.encryptionstarter.datalogic.FileConstants.AES
import com.jimmy.encryptionstarter.datalogic.FileConstants.ANDROID_KEY_STORE
import com.jimmy.encryptionstarter.datalogic.FileConstants.ENCRYPTION_ALGORITHM
import com.jimmy.encryptionstarter.datalogic.FileConstants.KEY_ALIAS
import com.jimmy.encryptionstarter.datalogic.FileConstants.TRANSFORMATION
import com.jimmy.encryptionstarter.datalogic.FileConstants.TRANSFORMATION_KEYSTORE
import java.security.KeyStore
import java.security.SecureRandom
import java.util.HashMap
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

internal class Encryption {

  fun encrypt(dataToEncrypt: ByteArray,  password: CharArray): HashMap<String, ByteArray> {

    val map = HashMap<String, ByteArray>()

    /*
    use the SecureRandom class, which makes sure that the output is difficult to predict.
    That’s called a cryptographically strong random number generator.
     */
    val random = SecureRandom()
    val salt = ByteArray(256)
    random.nextBytes(salt)

    /*
    1- Put the salt and password into PBEKeySpec, a password-based encryption object. The constructor takes
    an iteration count (1324). The higher the number, the longer it
    would take to operate on a set of keys during a brute force attack.
     */
    val pbKeySpec = PBEKeySpec(password, salt, 1324, 256)
//    2- Passed PBEKeySpec into the SecretKeyFactory.
    val secretKeyFactory = SecretKeyFactory.getInstance(ENCRYPTION_ALGORITHM)
//    3- Generated the key as a ByteArray
    val keyBytes = secretKeyFactory.generateSecret(pbKeySpec).encoded

//    4- Wrapped the raw ByteArray into a SecretKeySpec object.
    val keySpec = SecretKeySpec(keyBytes, AES)

    val ivRandom = SecureRandom() //not caching previous seeded instance of SecureRandom
    //  Created 16 bytes of random data.
    val iv = ByteArray(16)
    ivRandom.nextBytes(iv)
    //  Packaged it into an IvParameterSpec object.
    val ivSpec = IvParameterSpec(iv)

    /*

     passed in the specification string “AES/CBC/PKCS7Padding”.
     It chooses AES with cipher block chaining mode.
     PKCS7Padding is a well-known standard for padding. Since you’re working with blocks,
      not all data will fit perfectly into the block size, so you need to pad the remaining space.
       By the way, blocks are 128 bits long and AES adds padding before encryption.
     */
    val cipher = Cipher.getInstance(TRANSFORMATION)
    cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
    // doFinal does the actual encryption.
    val encrypted = cipher.doFinal(dataToEncrypt)


    map["salt"] = salt
    map["iv"] = iv
    map["encrypted"] = encrypted



    return map
  }

  fun decrypt(map: HashMap<String, ByteArray>, password: CharArray): ByteArray? {

    var decrypted: ByteArray? = null

    /*
    Used the HashMap that contains the encrypted data, salt and IV necessary for decryption.
     */
    val salt = map["salt"]
    val iv = map["iv"]
    val encrypted = map["encrypted"]

    /*
    Regenerated the key given that information plus the user’s password.
     */
    //regenerate key from password
    val pbKeySpec = PBEKeySpec(password, salt, 1324, 256)
    val secretKeyFactory = SecretKeyFactory.getInstance(ENCRYPTION_ALGORITHM)
    val keyBytes = secretKeyFactory.generateSecret(pbKeySpec).encoded
    val keySpec = SecretKeySpec(keyBytes, AES)

    /*
    Decrypted the data and returned it as a ByteArray.
     */
    //Decrypt
    val cipher = Cipher.getInstance(TRANSFORMATION)
    val ivSpec = IvParameterSpec(iv)
    try {
      cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
      decrypted = cipher.doFinal(encrypted)
    } catch (e: BadPaddingException) {
      Log.e("BadPaddingException", e.localizedMessage)
    }


    return decrypted
  }

  fun keystoreEncrypt(dataToEncrypt: ByteArray): HashMap<String, ByteArray> {

    val map = HashMap<String, ByteArray>()

      // 1
      //Get the key , retrieve the key from the KeyStore.
      val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
      keyStore.load(null)

      val secretKeyEntry =
        keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
      val secretKey = secretKeyEntry.secretKey

      // 2
      //Encrypt data, encrypted the data using the Cipher object, given the SecretKey.
      val cipher = Cipher.getInstance(TRANSFORMATION_KEYSTORE)
      cipher.init(Cipher.ENCRYPT_MODE, secretKey)
      val ivBytes = cipher.iv
      val encryptedBytes = cipher.doFinal(dataToEncrypt)

      // 3  return a HashMap containing the encrypted data and IV needed to decrypt the data.
      map["iv"] = ivBytes
      map["encrypted"] = encryptedBytes


    return map
  }

  fun keystoreDecrypt(map: HashMap<String, ByteArray>): ByteArray? {

    var decrypted: ByteArray? = null

    // 1
    //Get the key, Obtained the key again from the KeyStore.
    val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
    keyStore.load(null)

    val secretKeyEntry =
      keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
    val secretKey = secretKeyEntry.secretKey

    // 2
    //Extract info from map
    val encryptedBytes = map["encrypted"]
    val ivBytes = map["iv"]

    // 3
    //Decrypt data, Initialize the Cipher object using the DECRYPT_MODE constant and decrypted the data to a ByteArray.
    val cipher = Cipher.getInstance(TRANSFORMATION_KEYSTORE)
    val spec = GCMParameterSpec(128, ivBytes)
    cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
    decrypted = cipher.doFinal(encryptedBytes)


    return decrypted
  }

  @TargetApi(23)
  fun keystoreTest() {

    //1 created a KeyGenerator instance and set it to the “AndroidKeyStore” provider.
    val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
    val keyGenParameterSpec = KeyGenParameterSpec.Builder(KEY_ALIAS,
      KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
      .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
      .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
      // 2 Optionally, you added .setUserAuthenticationRequired(true) requiring a lock screen to be set up.
      //  requires lock screen, invalidated if lock screen is disabled
      //.setUserAuthenticationRequired(true)

      // 3 key only available x seconds from password authentication. -1 requires finger print - every time
      //.setUserAuthenticationValidityDurationSeconds(120)

      /*
        requires API 24
        makes the key unavailable once the device has detected it is no longer on the person
       */
      //.setUserAuthenticationValidWhileOnBody(true)

      // 4 different ciphertext for same plaintext on each call
      /*
      use a new IV each time. As you learned earlier, that means that if you encrypt identical data a second time,
      the encrypted output will not be identical. It prevents attackers from obtaining clues about the encrypted
      data based on feeding in the same inputs.
       */
      .setRandomizedEncryptionRequired(true)
      .build()
    keyGenerator.init(keyGenParameterSpec)
    keyGenerator.generateKey()

    // Testing keystore
    // 1
    val map = keystoreEncrypt("My very sensitive string!".toByteArray(Charsets.UTF_8))
    // 2 Called the decrypt method on the encrypted output to test that everything worked.
    val decryptedBytes = keystoreDecrypt(map)
    decryptedBytes?.let {
      val decryptedString = String(it, Charsets.UTF_8)
      Log.e("MyApp", "The decrypted string is: $decryptedString")
    }


  }
}

