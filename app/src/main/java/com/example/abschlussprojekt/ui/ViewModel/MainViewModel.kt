package com.example.abschlussprojekt.ui.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.abschlussprojekt.data.Repository
import com.example.abschlussprojekt.data.model.Category
import com.example.abschlussprojekt.data.model.Product
import com.example.abschlussprojekt.data.remote.WeatherApi

import kotlinx.coroutines.launch

private const val TAG = "MainViewModel"

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository(WeatherApi)

    //MySpaeti
    val products = listOf(
        Product(
            name = "Chips",
            price = 2.49,
            imageUrl = "https://example.com/chips.jpg",
            isAlcoholic = false,
            isVegan = true,
            isVegetarian = true,
            tags = listOf("bio", "glutenfree")
        ),
        Product(
            name = "Schokolade",
            price = 1.99,
            imageUrl = "https://example.com/schokolade.jpg",
            isAlcoholic = false,
            isVegan = false,
            isVegetarian = true,
            tags = listOf("halal", "sugarfree")
        ),
        Product(
            name = "Energydrink",
            price = 1.79,
            imageUrl = "https://example.com/energydrink.jpg",
            isAlcoholic = false,
            isVegan = true,
            isVegetarian = true,
            tags = listOf("sugarfree")
        ),
        Product(
            name = "Instantnudeln",
            price = 0.99,
            imageUrl = "https://example.com/instantnudeln.jpg",
            isAlcoholic = false,
            isVegan = true,
            isVegetarian = true,
            tags = listOf("bio", "glutenfree")
        ),
        Product(
            name = "Fruchtsaft",
            price = 2.29,
            imageUrl = "https://example.com/fruchtsaft.jpg",
            isAlcoholic = false,
            isVegan = true,
            isVegetarian = true,
            tags = listOf("bio", "sugarfree")
        ),
        Product(
            name = "Kekse",
            price = 1.49,
            imageUrl = "https://example.com/kekse.jpg",
            isAlcoholic = false,
            isVegan = false,
            isVegetarian = true,
            tags = listOf("halal")
        ),
        Product(
            name = "Nüsse",
            price = 3.99,
            imageUrl = "https://example.com/nuesse.jpg",
            isAlcoholic = false,
            isVegan = true,
            isVegetarian = true,
            tags = listOf("bio", "glutenfree")
        ),
        Product(
            name = "Gummibärchen",
            price = 1.59,
            imageUrl = "https://example.com/gummibaerchen.jpg",
            isAlcoholic = false,
            isVegan = false,
            isVegetarian = true,
            tags = listOf("sugarfree")
        ),
        Product(
            name = "Popcorn",
            price = 2.99,
            imageUrl = "https://example.com/popcorn.jpg",
            isAlcoholic = false,
            isVegan = true,
            isVegetarian = true,
            tags = listOf("bio")
        ),
        Product(
            name = "Pudding",
            price = 1.89,
            imageUrl = "https://example.com/pudding.jpg",
            isAlcoholic = false,
            isVegan = false,
            isVegetarian = true,
            tags = listOf("halal")
        )
    )

    val lastWeather = repository.lastWeather
    val selectedCategory = repository.selectedCategory

    fun getWeatherByLocation(longitude: Double, latitude: Double) {
        viewModelScope.launch {
            repository.getWeatherByLocation(longitude, latitude)
        }
    }

    fun setSelectedCategory(category: Category) {
        viewModelScope.launch {
            repository.setSelectedCategory(category)
        }
    }



}