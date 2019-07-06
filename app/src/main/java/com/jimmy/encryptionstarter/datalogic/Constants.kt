package com.jimmy.encryptionstarter.datalogic

object FileConstants {
    const val DATA_SOURCE_FILE_NAME = "pets.xml"
    const val PREFRENCE_NAME = "MyPrefs"
    const val KEY_ALIAS = "MyKeyAlias"

    /* encryption related constants */
    const val ENCRYPTION_ALGORITHM = "PBKDF2WithHmacSHA1"
    const val AES = "AES"
    const val TRANSFORMATION = "AES/CBC/PKCS7Padding"
    const val TRANSFORMATION_KEYSTORE = "AES/GCM/NoPadding"
    const val ANDROID_KEY_STORE = "AndroidKeyStore"
}