package com.example.kulturnispomenici.Data

import android.location.Location
import java.util.Date
import java.util.UUID

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
}
