package com.jimmy.encryptionstarter.views.petdetails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.jimmy.encryptionstarter.R
import com.jimmy.encryptionstarter.datalogic.model.Pet
import kotlinx.android.synthetic.main.activity_pet_detail.*

class PetDetailActivity :AppCompatActivity() {

    private lateinit var activityPetDetailBinding: com.jimmy.encryptionstarter.databinding.ActivityPetDetailBinding
    private var currentPet: Pet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityPetDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_pet_detail)
        currentPet = intent.getSerializableExtra(PET_KEY) as Pet

        nameTextView?.text = currentPet?.name
        birthdayTextView?.text = currentPet?.birthday
        descriptionTextView?.text = currentPet?.medicalNotes

        val resourceID = resources.getIdentifier(currentPet?.imageResourceName,
            "drawable", packageName)
        photoImageView.setImageResource(resourceID)


    }

    companion object {
        private const val PET_KEY = "PET"
    }

}