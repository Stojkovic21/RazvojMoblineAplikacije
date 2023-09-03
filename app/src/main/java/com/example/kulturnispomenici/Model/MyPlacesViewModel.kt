package com.example.kulturnispomenici.Model

import android.graphics.drawable.Drawable
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.example.kulturnispomenici.Data.myPlace
import com.example.kulturnispomenici.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.osmdroid.views.MapView

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
    fun addMarker(
        map:MapView,
        tvTitel:TextView,
        tvOpis:TextView,
        imgPicture:ImageView,
        bottomManu: FrameLayout
    ) {
        for (place in myPlacesList){
            place.addMarker(map,tvTitel,tvOpis,imgPicture,bottomManu)
        }
    }
    fun filerPlaces(filer:String):MyPlacesViewModel{
        var filtrirano:MyPlacesViewModel= MyPlacesViewModel()
        for (place in myPlacesList){
            if(place.title.toString().contains(filer)){
                filtrirano.addPlace(place)
            }
        }
        return filtrirano
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