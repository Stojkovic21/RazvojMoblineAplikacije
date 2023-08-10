package com.example.kulturnispomenici.Data

data class User(var ime:String, val prezime:String, val usename:String, val datumRodjenja:String, val brTelefona:String,)
{
    constructor() : this("","","","","") {}
}
