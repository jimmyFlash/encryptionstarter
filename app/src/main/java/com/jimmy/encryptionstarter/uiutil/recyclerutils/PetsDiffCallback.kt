package com.jimmy.encryptionstarter.uiutil.recyclerutils

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.jimmy.encryptionstarter.datalogic.model.Pet

class PetsDiffCallback ( private val oldList: List<Pet>,
                         private val newList: List<Pet>) :DiffUtil.Callback() {


    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        Log.e("areItemsTheSame", "${oldList[oldItemPosition].name}, ${newList[newItemPosition].name}")
      return oldList[oldItemPosition].name == newList[newItemPosition].name
   }


    override fun getOldListSize(): Int  = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {


        val (name, birthday, imageResourceName, medicalNotes) = oldList[oldItemPosition]
        val (name1, birthday1, imageResourceName1, medicalNotes1) = newList[newItemPosition]
        Log.e("areContentsTheSame", "$name , $name1}")

        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {

      return super.getChangePayload(oldItemPosition, newItemPosition)
    }

}