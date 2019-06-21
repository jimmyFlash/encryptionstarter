package com.jimmy.encryptionstarter.uiutil.recyclerutils

import androidx.recyclerview.widget.DiffUtil
import com.jimmy.encryptionstarter.datalogic.model.Pet

class PetsDiffCallback ( private val oldList: List<Pet>,
                         private val newList: List<Pet>) :DiffUtil.Callback(){

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].imageResourceName == newList[newItemPosition].imageResourceName
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].name == newList[newItemPosition].name
    }

}