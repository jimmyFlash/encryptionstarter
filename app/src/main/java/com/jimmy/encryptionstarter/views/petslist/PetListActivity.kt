package com.jimmy.encryptionstarter.views.petslist


import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.jimmy.encryptionstarter.R
import com.jimmy.encryptionstarter.datalogic.FileConstants
import com.jimmy.encryptionstarter.datalogic.model.Pet
import com.jimmy.encryptionstarter.uiutil.start
import com.jimmy.encryptionstarter.views.petdetails.PetDetailActivity
import kotlinx.android.synthetic.main.activity_pet_list.*
import java.io.File
import java.util.*

class PetListActivity : AppCompatActivity() {
  private lateinit var password: CharArray
  private lateinit var file: File
  private var petList: MutableList<Pet> = mutableListOf()
  private lateinit var linearLayoutManager: LinearLayoutManager
  private lateinit var adapter: RecyclerAdapter

  private val viewModel: PetViewModel by lazy {
    ViewModelProviders.of(this).get(PetViewModel::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_pet_list)

    linearLayoutManager = LinearLayoutManager(this)
    recyclerView.layoutManager = linearLayoutManager

    adapter = RecyclerAdapter( object : RecyclerAdapter.PhotoHolder.OnItemClickListener{
      override fun onItemClick(position: Int, pet : Pet?) {
        val b = Bundle()
        b.putSerializable(PET_KEY, pet)
        PetDetailActivity::class.start(this@PetListActivity, b, false)
      }

    })

    recyclerView.adapter = adapter

    setupPets()

    val myCounter = object : CountDownTimer( 6000, 1000){
      override fun onFinish() {

        Log.e("update pets list", "petList size ${petList.size}")
        adapter.swap(viewModel.testPetsArrayChange(petList))


      }

      override fun onTick(millisUntilFinished: Long) {
       // do nothing here.
      }
    }
//    myCounter.start()

  }

  private fun setupPets() {
     file = File(filesDir.absolutePath + File.separator +
        FileConstants.DATA_SOURCE_FILE_NAME)
    password = intent.getCharArrayExtra(PWD_KEY)
    petList.addAll( viewModel.getPets(file, password))
    adapter.swap(petList)
  }


    fun sortDesendingByName (petsList : List<Pet>) : List<Pet> {


        return petsList.sortedByDescending { it.name }

    }

    fun sortDesendingByBDate(petsList : List<Pet>): List<Pet>{
        return  petsList.sortedByDescending { it.birthday }
    }



    companion object {
    private const val PWD_KEY = "PWD"
    private const val PET_KEY = "PET"
  }
}