package com.example.kulturnispomenici.Model

import android.content.ContentValues.TAG
import android.location.Location
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.example.kulturnispomenici.Data.myPlace
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.osmdroid.views.MapView
import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt

class MyPlacesViewModel:ViewModel() {
    var myPlacesList:ArrayList<myPlace> = ArrayList()
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
    fun filterPlaces(filer:String):MyPlacesViewModel{
        var filtrirano:MyPlacesViewModel= MyPlacesViewModel()
        for (place in myPlacesList){
            if(place.title.toString().contains(filer)){
                filtrirano.addPlace(place)
            }
        }
        return filtrirano
    }
    fun filterPlaces(currentLocation:Location, radius:Double):List<myPlace>{
        val filter=myPlacesList.filter { place->

            getDistance(currentLocation.latitude,currentLocation.longitude,place.latitude,place.longitude)<radius
        }
        return filter
    }
    private fun getDistance(currentLat: Double, currentLon: Double, strayLat: Double, strayLon: Double): Double {
        val earthRadius = 6371000.0

        val currentLatRad = Math.toRadians(currentLat)
        val strayLatRad = Math.toRadians(strayLat)
        val deltaLat = Math.toRadians(strayLat - currentLat)
        val deltaLon = Math.toRadians(strayLon - currentLon)

        val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
                cos(currentLatRad) * cos(strayLatRad) *
                sin(deltaLon / 2) * sin(deltaLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
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