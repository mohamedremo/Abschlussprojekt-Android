package com.example.abschlussprojekt.data.model

import com.google.firebase.firestore.PropertyName

data class Product(
    val name: String,
    val price: Double,
    val imageUrl: String,
    @PropertyName("alcoholic") // Der "@Propertyname" deklariert den Property Namen im Firestore
    val isAlcoholic: Boolean,
    @PropertyName("vegan")
    val isVegan: Boolean,
    @PropertyName("vegetarian")
    val isVegetarian: Boolean,
    val tags: List<String> = listOf(),
    val type: String
) {
    constructor() : this("", 0.0, "",false,  false, false, listOf(),"")
}




