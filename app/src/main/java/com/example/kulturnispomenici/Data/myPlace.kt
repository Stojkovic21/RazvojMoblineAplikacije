package com.example.kulturnispomenici.Data

import android.location.Location
import java.util.UUID

data class myPlace(
    var id: String = UUID.randomUUID().toString(),
    var title: String = "",
    var description: String = "",
    var ownerId: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var imageURL: String = "",
    var like: Int = 0,
    var dislike: Int = 0,
//    var likedByUsers: MutableList<String> = mutableListOf(),
//    var dislikedByUsers: MutableList<String> = mutableListOf()
) {
    constructor() : this("", "", "", "",  0.0, 0.0, "", 0, 0,) //mutableListOf(), mutableListOf())
}
