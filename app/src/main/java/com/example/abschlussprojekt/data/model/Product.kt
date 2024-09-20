package com.example.abschlussprojekt.data.model

data class Product(
    val name: String,
    val price: Double,
    val description: String,
    val imageUrl: String,
    val isAlcoholic: Boolean,
    val isVegan: Boolean,
    val isVegetarian: Boolean,
    val tags: List<String> = listOf()
)


