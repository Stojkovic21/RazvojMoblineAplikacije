package com.example.kulturnispomenici.Model

import androidx.lifecycle.ViewModel
import com.example.kulturnispomenici.Data.myPlace
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MyPlacesViewModel:ViewModel() {
    var myPlacesList:ArrayList<myPlace> = ArrayList<myPlace>()
    fun addPlace(myPlace: myPlace){
        myPlacesList.add(myPlace)
    }

    fun fetchPlace(){
        val databaseReference=Firebase.database.getReference("Dodate lokacije")

        databaseReference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                myPlacesList.clear()
                val placesList= mutableListOf<myPlace>()
                for (dataSnapshot in snapshot.children){
                    val value=dataSnapshot.getValue(myPlace::class.java)
                    val pomPlace= value?.let { myPlace(value.id,value.title,value.description,value.ownerUsername,value.latitude,value.longitude,value.date,value.imageURL,value.like,value.dislike) }
                    if (pomPlace != null) {
                        myPlacesList.add(pomPlace)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    fun getItemOnPostion(position:Int):myPlace{
        return myPlacesList[position]
    }
    fun addOne(myPlace: myPlace){
        myPlacesList[0]=myPlace
    }
    fun isNotEmpty():Boolean{
        if(myPlacesList.isEmpty())
            return false
        return true
    }
}