
package com.jimmy.encryptionstarter.views.petslist


import androidx.lifecycle.ViewModel
import com.jimmy.encryptionstarter.datalogic.model.Pet
import com.jimmy.encryptionstarter.datalogic.model.Pets
import org.simpleframework.xml.core.Persister
import java.io.File

class PetViewModel : ViewModel() {

  private var pets: ArrayList<Pet>? = null

  fun getPets(file: File, password: CharArray) : ArrayList<Pet> {
    if (pets == null) {
      loadPets(file, password)
    }

    return pets ?: arrayListOf()
  }

  private fun loadPets(file: File, password: CharArray) {

    /*
    var decrypted: ByteArray? = null
    ObjectInputStream(FileInputStream(file)).use { it ->
      val data = it.readObject()

      when(data) {
        is Map<*, *> -> {

          if (data.containsKey("iv") && data.containsKey("salt") && data.containsKey("encrypted")) {
            val iv = data["iv"]
            val salt = data["salt"]
            val encrypted = data["encrypted"]
            if (iv is ByteArray && salt is ByteArray && encrypted is ByteArray) {
              //TODO: Add decrypt call here
            }
          }
        }
      }
    }

    if (decrypted != null) {
    */
      val serializer = Persister()
      val inputStream = file.inputStream() //TODO: Replace me
      val pets = try { serializer.read(Pets::class.java, inputStream) } catch (e: Exception) {null}
      pets?.list?.let {
        this.pets = ArrayList(it)
      }
    /*} */
  }
}