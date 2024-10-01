package com.example.abschlussprojekt.data.model

data class Product(
    val name: String,
    val price: Double,
    val imageUrl: String,
    val isAlcoholic: Boolean,
    val isVegan: Boolean,
    val isVegetarian: Boolean,
    val tags: List<String> = listOf()
) {
    constructor() : this("", 0.0, "", false, false, false, listOf())
}





