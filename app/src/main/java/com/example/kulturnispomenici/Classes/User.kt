package com.example.kulturnispomenici.Classes

import java.util.Date

data class User(var ime:String, val prezime:String, val usename:String, val datumRodjenja:String, val brTelefona:String,)
{
    constructor() : this("","","","","") {}
}
