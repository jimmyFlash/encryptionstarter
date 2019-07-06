package com.jimmy.encryptionstarter.views.main

import android.app.Application
import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jimmy.encryptionstarter.R
import com.jimmy.encryptionstarter.datalogic.Encryption
import com.jimmy.encryptionstarter.datalogic.FileConstants
import com.jimmy.encryptionstarter.datalogic.FileConstants.PREFRENCE_NAME
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.text.DateFormat
import java.util.Date


class MainActivityViewModel(application : Application) : AndroidViewModel(application ){


    private val TAG = MainActivityViewModel::class.java.simpleName

    private var workingFile: File? = null

    private val isSignedUp : MutableLiveData<Boolean> = MutableLiveData()

    val loginConfirmPass : MutableLiveData<Boolean> = MutableLiveData()

    val isSigned : LiveData<Boolean> = isSignedUp

    val buttonText : MutableLiveData<String> = MutableLiveData()

    val lastLogMsg : MutableLiveData<String> = MutableLiveData()

    val proceedToListActivity : MutableLiveData<Boolean> = MutableLiveData()



    init {

        workingFile = File(getApplication<Application>().filesDir.absolutePath +
                File.separator +
                FileConstants.DATA_SOURCE_FILE_NAME)
        updateLoggedInState()

        Encryption().keystoreTest()

    }

    private fun saveLastLoggedInTime(context : Context) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())

        //Save to shared prefs
        val editor = context.getSharedPreferences(PREFRENCE_NAME, Context.MODE_PRIVATE).edit()
        editor.putString("l", currentDateTimeString)
        editor.apply()
    }
    //This is just for demo data
    private fun createDataSource(context : Context, filename: String, outFile: File) {
        val fileDescriptor = context.assets.openFd(filename)
        fileDescriptor.createInputStream().use { inputStream ->
            FileOutputStream(outFile).use { outputStream ->
                inputStream.channel.transferTo(
                    fileDescriptor.startOffset,
                    fileDescriptor.length,
                    outputStream.channel)
            }
        }
    }

    private fun secureSaveLastLoggedInTime(context : Context, pwrdStr : String) {
        //Get password
        val password = CharArray(pwrdStr.length)
        pwrdStr.toCharArray(password,0, 0, pwrdStr.length)

        //Base64 the data
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        // 1
        /*
        Converted the String into a ByteArray with the UTF-8 encoding and encrypted it.
        In the previous code you opened a file as binary, but in the case of working with strings,
        you’ll need to take the character encoding into account.
         */
        val map = Encryption().encrypt(currentDateTimeString.toByteArray(Charsets.UTF_8),
            password)

        // 2
        /*
        Converted the raw data into a String representation.
        SharedPreferences can’t store a ByteArray directly but it can work with String.
        Base64 is a standard that converts raw data to a string representation
         */
        val valueBase64String = Base64.encodeToString(map["encrypted"], Base64.NO_WRAP)
        val saltBase64String = Base64.encodeToString(map["salt"], Base64.NO_WRAP)
        val ivBase64String = Base64.encodeToString(map["iv"], Base64.NO_WRAP)

        //Save to shared prefs
        val editor = context.getSharedPreferences(PREFRENCE_NAME, Context.MODE_PRIVATE).edit()

        // 3
        /*
        Saved the strings to the SharedPreferences. You can optionally encrypt both the preference key and the value.
        That way, an attacker can’t figure out what the value might be by looking at the key,
        and using keys like “password” won’t work for brute forcing, since that would be encrypted as well.
         */
        editor.putString("l", valueBase64String)
        editor.putString("lsalt", saltBase64String)
        editor.putString("liv", ivBase64String)
        editor.apply()

    }

    /**
     * open the data file as an input stream and fed the data into the encryption method.
     * You serialized the HashMap using the ObjectOutputStream class and then saved it to storage.
     */
    private fun createDataSource(context : Context, filename: String, outFile: File, pwdStr : String) {
        val inputStream = context.assets.open(filename)
        val bytes = inputStream.readBytes()
        inputStream.close()

        val password = CharArray(pwdStr.length)
        pwdStr.toCharArray(password,0, 0, pwdStr.length)
       // Log.e("jjjjjjjj", "$pwdStr  ${pwdStr.length}  $password")
        val map = Encryption().encrypt(bytes, password)
        ObjectOutputStream(FileOutputStream(outFile)).use {
            it.writeObject(map)
        }

    }

    private fun updateLoggedInState() {
        val fileExists = workingFile?.exists() ?: false
        if (fileExists) {
            isSignedUp.value = true
            buttonText.value = getApplication<Application>().getString(R.string.login)
//            login_confirm_password.visibility = View.INVISIBLE
            loginConfirmPass.value = false
        } else {
            isSignedUp.value = false
            buttonText.value = getApplication<Application>().getString(R.string.signup)
            loginConfirmPass.value = true
        }
    }

    private fun lastLoggedIn(pwdString : String): String? {
        //Get password
        val password = CharArray(pwdString.length)
        pwdString.toCharArray(password, 0, 0, pwdString.length)

        //Retrieve shared prefs data
        // 1 Retrieved the string representations for the encrypted data, IV and salt.
        val preferences = getApplication<Application>().getSharedPreferences(PREFRENCE_NAME,
            Context.MODE_PRIVATE)
        val base64Encrypted = preferences.getString("l", "")
        val base64Salt = preferences.getString("lsalt", "")
        val base64Iv = preferences.getString("liv", "")

        //Base64 decode
        // 2 Applied a Base64 decode on the strings to convert them back to raw bytes
        val encrypted = Base64.decode(base64Encrypted, Base64.NO_WRAP)
        val iv = Base64.decode(base64Iv, Base64.NO_WRAP)
        val salt = Base64.decode(base64Salt, Base64.NO_WRAP)

        //Decrypt
        // 3 Passed that data in a HashMap to the decrypt method.
        val decrypted = Encryption().decrypt(
            hashMapOf("iv" to iv, "salt" to salt, "encrypted" to encrypted), password)

        var lastLoggedIn: String? = null
        decrypted?.let {
            lastLoggedIn = String(it, Charsets.UTF_8)
        }
        return lastLoggedIn

    }

    fun loginPressed(password: String, logPassConfirm: String) {

        var success = false
        if(isSignedUp.value == true){
            val lastLogin = lastLoggedIn(password)
            if (lastLogin != null) {
                success = true
                lastLogMsg.value = "Last login: $lastLogin"

            } else {
                lastLogMsg.value = "Please check your password and try again."

            }


        }else{

            when {
                password.isEmpty() -> lastLogMsg.value = "Please enter a password!"

                password == logPassConfirm -> workingFile?.let {
                    createDataSource(getApplication(), "pets.xml", it, password)
                    success = true
                }
                else -> lastLogMsg.value = "Passwords do not match!"
            }
        }

        if (success) {
            secureSaveLastLoggedInTime(getApplication(), password)

            proceedToListActivity.value = true
        }

    }
}