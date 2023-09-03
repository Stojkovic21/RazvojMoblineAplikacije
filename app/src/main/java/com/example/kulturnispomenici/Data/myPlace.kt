package com.example.kulturnispomenici.Data

import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.view.View
import android.view.View.OnClickListener
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.File
import java.util.Date

data class myPlace(
    var id: String ="",
    var title: String = "",
    var description: String = "",
    var ownerUsername: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var date: Date=Date(),
    var imageURL: String = "",
    var like: Int = 0,
    var dislike: Int = 0,
//    var likedByUsers: MutableList<String> = mutableListOf(),
//    var dislikedByUsers: MutableList<String> = mutableListOf()
) {
    constructor() : this("", "", "", "",  0.0, 0.0, Date(),"", 0, 0,) //mutableListOf(), mutableListOf())

    override fun toString(): String=title

    fun addMarker(
        map: MapView,
        tvTitel: TextView,
        tvOpis: TextView,
        imgPicture: ImageView,
        bottomManu: FrameLayout
    ) {
        val marker: Marker = Marker(map)
        val curLocarion: GeoPoint = GeoPoint(latitude, longitude)
        marker.position = curLocarion
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = title
        map.overlays.add(marker)

        marker.setOnMarkerClickListener(object :Marker.OnMarkerClickListener{
            override fun onMarkerClick(marker: Marker?, mapView: MapView?): Boolean {

                tvTitel.text=title
                tvOpis.text=description

                val storegeReference= FirebaseStorage.getInstance().reference.child("SlikeSpomenika/${id}.jpg")
                val localFile= File.createTempFile("tempImage","jpg")
                storegeReference.getFile(localFile).addOnSuccessListener { it->
                val bitmap= BitmapFactory.decodeFile(localFile.absolutePath)
                if (imgPicture != null) {
                    imgPicture.setImageBitmap(bitmap)
                }
                    bottomManu.visibility=View.VISIBLE
        }
                return true
            }
        })
    }
}
