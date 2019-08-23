
package com.jimmy.encryptionstarter.views.petslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jimmy.encryptionstarter.R
import com.jimmy.encryptionstarter.databinding.RecyclerviewItemRowBinding
import com.jimmy.encryptionstarter.datalogic.model.Pet


class RecyclerAdapter(private var listener: PhotoHolder.OnItemClickListener) :
  RecyclerView.Adapter<RecyclerAdapter.PhotoHolder>() {


  var diffAsync : AsyncListDiffer<Pet>

  lateinit var inflatedViewBinding : RecyclerviewItemRowBinding


  init{


    val diffUtilCallback = object : DiffUtil.ItemCallback<Pet>() {

      override fun areItemsTheSame(@NonNull newUser: Pet, @NonNull oldUser: Pet): Boolean {
        return newUser.name.equals(oldUser.name)
      }

      override fun areContentsTheSame(@NonNull newUser: Pet, @NonNull oldUser: Pet): Boolean {
        return newUser.equals(oldUser)
      }
    }

    diffAsync = AsyncListDiffer(this@RecyclerAdapter, diffUtilCallback  )

  }

  override fun getItemCount() = diffAsync.currentList.size

  override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
    val pet = diffAsync.currentList[position]
    holder.bindPet(pet, listener)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    inflatedViewBinding = DataBindingUtil.inflate(layoutInflater,
      R.layout.recyclerview_item_row, parent, false)
    return PhotoHolder(inflatedViewBinding)
  }

  fun swap(actors: MutableList<Pet>) {
   diffAsync.submitList(actors)
  }

  class PhotoHolder(private var binding: RecyclerviewItemRowBinding) :
    RecyclerView.ViewHolder(binding.root){
    private val view = binding.root

      companion object {
          private const val PET_KEY = "PET"
      }

      // interface for click handeling
    interface OnItemClickListener {
      fun onItemClick(position: Int, aniOb : Pet? = null)
    }

    fun bindPet(pet: Pet, listener: OnItemClickListener?) {
      binding.pet = pet
//      binding.itemName.text = pet.name
//      binding.itemDate.text = pet.birthday

      if (listener != null)
        binding.root.setOnClickListener { listener.onItemClick(layoutPosition, pet) }


//      val resourceID = itemView.context.resources.getIdentifier(pet.imageResourceName,
//          "drawable", itemView.context.packageName)
//      view.itemImage.setImageResource(resourceID)

    }
      /*
       private var pet: Pet? = null
       init {
         view.setOnClickListener(this)
       }

       override fun onClick(view: View) {
         val context = itemView.context
         val showDetailsIntent = Intent(context, PetDetailActivity::class.java)
         showDetailsIntent.putExtra(PET_KEY, pet)
         context.startActivity(showDetailsIntent)

       }
   */

  }
}