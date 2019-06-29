package com.jimmy.encryptionstarter.views.petslist


import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.jimmy.encryptionstarter.R
import com.jimmy.encryptionstarter.datalogic.FileConstants
import com.jimmy.encryptionstarter.datalogic.model.Pet
import com.jimmy.encryptionstarter.uiutil.start
import com.jimmy.encryptionstarter.views.main.MainActivity
import com.jimmy.encryptionstarter.views.petdetails.PetDetailActivity
import kotlinx.android.synthetic.main.activity_pet_list.*
import java.io.File
import java.util.*

class PetListActivity : AppCompatActivity() {
  private var petList: ArrayList<Pet> = ArrayList()
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

    adapter = RecyclerAdapter(petList, object : RecyclerAdapter.PhotoHolder.OnItemClickListener{
      override fun onItemClick(position: Int, pet : Pet?) {
        val b = Bundle()
        b.putSerializable(PET_KEY, pet)
        PetDetailActivity::class.start(this@PetListActivity, b, true)
      }

    })

    recyclerView.adapter = adapter

    setupPets()
  }

  private fun setupPets() {
    val file = File(filesDir.absolutePath + File.separator +
        FileConstants.DATA_SOURCE_FILE_NAME)
    val password = intent.getCharArrayExtra(PWD_KEY)
    petList.addAll( viewModel.getPets(file, password))
    adapter.notifyDataSetChanged()
  }

  companion object {
    private const val PWD_KEY = "PWD"
    private const val PET_KEY = "PET"
  }
}