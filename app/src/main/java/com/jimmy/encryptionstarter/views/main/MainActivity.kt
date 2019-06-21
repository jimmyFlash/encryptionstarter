package com.jimmy.encryptionstarter.views.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.jimmy.encryptionstarter.R
import com.jimmy.encryptionstarter.databinding.ActivityMainBinding
import com.jimmy.encryptionstarter.uiutil.showToast
import com.jimmy.encryptionstarter.uiutil.start
import com.jimmy.encryptionstarter.views.petslist.PetListActivity
import com.jimmy.testmodule.TestActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var mViewModel: MainActivityViewModel
    private var workingFile: File? = null

    lateinit var activityMainBinding :ActivityMainBinding

    companion object {
        private const val PWD_KEY = "PWD"
    }

    object FileConstants {
        const val DATA_SOURCE_FILE_NAME = "pets.xml"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        println("testing invocation of the member from TestActivity from testmodule module: ${TestActivity.TEST_PRPERTY}")
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Get the ViewModel
         mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        // Specify the current activity as the lifecycle owner.
        activityMainBinding.lifecycleOwner = this


        workingFile = File(filesDir.absolutePath + File.separator + FileConstants.DATA_SOURCE_FILE_NAME)

        login_password.setOnClickListener(this::loginPressed)

        activityMainBinding.viewmodel = mViewModel

        mViewModel.lastLogMsg.observe(this, Observer {
            this@MainActivity.showToast(it)
        })

        mViewModel.proceedToListActivity.observe(this, Observer {

            val b = Bundle()
            b.putCharArray(PWD_KEY, login_password.text.toString().toCharArray())
            PetListActivity::class.start(this@MainActivity, b, true)
        })

    }

    fun loginPressed (view : View){
        val password = login_password.text.toString()

        login_confirm_password.text.toString()
        mViewModel.loginPressed(password, login_confirm_password.text.toString())
    }



}
