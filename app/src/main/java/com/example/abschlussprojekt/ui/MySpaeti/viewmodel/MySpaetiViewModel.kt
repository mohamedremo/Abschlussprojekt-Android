package com.example.abschlussprojekt.ui.MySpaeti.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.abschlussprojekt.data.FirebaseRepository
import com.example.abschlussprojekt.data.model.Product

class MySpaetiViewModel(application: Application): AndroidViewModel(application) {


    private val repository = FirebaseRepository()

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>>
        get() = _products

    private val _selectedProduct = MutableLiveData<Product>()
    val selectedProduct: LiveData<Product>
        get() = _selectedProduct

    private val _cart = MutableLiveData<Map<Product,Int>>()
    val cart: LiveData<Map<Product,Int>>
        get() = _cart



    fun fetchProducts() {
        repository.getAllProductsFromDatabase { products ->
            _products.value = products
        }
    }

    fun setSelectedProduct(product: Product) {
        _selectedProduct.value = product
    }







}