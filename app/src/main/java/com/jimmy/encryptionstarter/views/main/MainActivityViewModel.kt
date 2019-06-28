package com.jimmy.encryptionstarter.views.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jimmy.encryptionstarter.R
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.util.*

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

        updateLoggedInState()

        buttonText.value = getApplication<Application>().getString(R.string.login)

    }

    private fun saveLastLoggedInTime(context : Context) {
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())

        //Save to shared prefs
        val editor = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit()
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

    private fun updateLoggedInState() {
        val fileExists = workingFile?.exists() ?: false
        if (fileExists) {
            isSignedUp.value = true
            buttonText.value = getApplication<Application>().getString(R.string.login)
//            login_confirm_password.visibility = View.INVISIBLE
            loginConfirmPass.value = true
        } else {
            isSignedUp.value = false
            buttonText.value = getApplication<Application>().getString(R.string.signup)
            loginConfirmPass.value = false
        }
    }

    private fun lastLoggedIn(): String? {
        //Retrieve shared prefs data
        val preferences = getApplication<Application>().getSharedPreferences("MyPrefs",
            Context.MODE_PRIVATE)
        return preferences.getString("l", "")
    }

    fun loginPressed(password: String, logPassConfirm: String) {

        var success = false
        if(isSignedUp.value == true){
            val lastLogin = lastLoggedIn()
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
                    createDataSource(getApplication(), "pets.xml", it)
                    success = true
                }
                else -> lastLogMsg.value = "Passwords do not match!"
            }
        }

        if (success) {
            saveLastLoggedInTime(getApplication())

            proceedToListActivity.value = true
        }

    }
}