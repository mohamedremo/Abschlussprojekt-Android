package com.example.abschlussprojekt.ui.MySpaeti.viewmodel

import android.app.Application
import android.graphics.pdf.PdfDocument
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.abschlussprojekt.data.FirebaseRepository
import com.example.abschlussprojekt.data.model.Order
import com.example.abschlussprojekt.data.model.Product

private const val TAG = "MySpaetiViewModel"

class MySpaetiViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FirebaseRepository()

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>>
        get() = _products

    private val _selectedProduct = MutableLiveData<Product>()
    val selectedProduct: LiveData<Product>
        get() = _selectedProduct

    private val _cart = MutableLiveData<MutableMap<Product, Int>>(mutableMapOf())
    val cart: LiveData<MutableMap<Product, Int>>
        get() = _cart

    private val _productCount = MutableLiveData<Int>(1)
    val productCount: LiveData<Int>
        get() = _productCount

    private val _totalPrice = MutableLiveData<Double>()
    val totalPrice: LiveData<Double>
        get() = _totalPrice

    private val _favoriteProducts = MutableLiveData<List<Product>>()
    val favoriteProducts: LiveData<List<Product>>
        get() = _favoriteProducts

    private val _lastAdress = MutableLiveData<String>()
    val lastAdress: LiveData<String>
        get() = _lastAdress

    init {
        Log.d(TAG, "$TAG initialisiert.")
        fetchProducts()
    }

    fun fetchProducts() {
        Log.d(TAG, "Produkte werden aus der Datenbank abgerufen.")
        repository.getAllProductsFromFirestore { products ->
            _products.value = products
            Log.d(TAG, "Produkte erfolgreich abgerufen: ${products.size} Produkte.")
        }
    }

    fun setSelectedProduct(product: Product) {
        _selectedProduct.value = product
        Log.d(TAG, "Ausgewähltes Produkt: ${product.name}")
    }

    fun increaseProductCount() {
        _productCount.value = _productCount.value?.plus(1)
        Log.d(TAG, "Produktanzahl erhöht: ${_productCount.value}")
    }

    fun decreaseProductCount() {
        if ((_productCount.value ?: 0) > 1) {
            _productCount.value = _productCount.value?.minus(1)
            Log.d(TAG, "Produktanzahl verringert: ${_productCount.value}")
        }
    }

    fun increaseProductInCart(product: Product) {
        val currentCart = _cart.value?.toMutableMap() ?: mutableMapOf()
        val currentCount =
            currentCart[product] ?: 0 // Wenn kein Eintrag gefunden wird, setze die Menge auf 0
        currentCart[product] = currentCount + 1
        _cart.postValue(currentCart)
        Log.d(TAG, "Produkt erhöht im Warenkorb: ${product.name}, neue Menge: ${currentCount + 1}")
    }

    fun decreaseProductInCart(product: Product) {
        val currentCart = _cart.value?.toMutableMap() ?: mutableMapOf()
        val currentCount = currentCart[product] ?: 0
        if (currentCount > 1) {
            currentCart[product] = currentCount - 1
            Log.d(
                TAG,
                "Produkt verringert im Warenkorb: ${product.name}, neue Menge: ${currentCount - 1}"
            )
        } else {
            currentCart.remove(product)
            Log.d(TAG, "Produkt entfernt aus dem Warenkorb: ${product.name}")
        }
        _cart.postValue(currentCart)
    }

    //run ist
    fun addProductToCart() {
        // Überprüfen, ob ein Produkt ausgewählt wurde
        _selectedProduct.value?.let { product ->
            val count = _productCount.value ?: 0
            if (count > 0) {
                val currentCart = _cart.value?.toMutableMap() ?: mutableMapOf()
                currentCart[product] = currentCart.getOrDefault(product, 0) + count
                _cart.postValue(currentCart) // Aktualisiere den Warenkorb
                _productCount.postValue(1) // Setze die LiveData von Count zurück
                Log.d(TAG, "Produkt hinzugefügt: ${product.name}, Menge: $count")
            } else {
                Log.e(TAG, "Kann Produkt nicht hinzufügen: Anzahl muss größer als 0 sein.")
            }
        } ?: run {
            Log.e(TAG, "Kein Produkt ausgewählt.")
        }
    }

    //Diese Funktion wird verwendet um ein Produkt direkt in den Warenkorb zu schieben.
    // ohne vorher in die Detail View zu navigieren und den Count zu setzen
    // hier wird simply ein Produkt hinzugefügt und wenn es schon ein Produkt gibt wird die Menge erhöht.
    fun simpleAddToCart(product: Product) {
        val currentCart = _cart.value?.toMutableMap() ?: mutableMapOf()
        currentCart[product] = currentCart.getOrDefault(product, 0) + 1
        _cart.postValue(currentCart)
    }

    //Hier wird die Gesamtsummer berechnet und nur Intern benutzt daher private.
    fun calculateTotalPrice() {
        var totalPrice = 0.0
        _cart.value?.forEach { (product, count) ->
            totalPrice += product.price * count
        }
        _totalPrice.value = totalPrice
    }

    //Diese Funktion sorgt dafür das der aktuelle Gesamtpreis aus der LiveData berechnet wird.
    fun toggleFavorite(product: Product) {
        val currentFavorites = _favoriteProducts.value?.toMutableList() ?: mutableListOf()
        if (currentFavorites.contains(product)) {
            currentFavorites.remove(product)
        } else {
            currentFavorites.add(product)
        }
        _favoriteProducts.postValue(currentFavorites)
    }

    fun removeFromCart(product: Product) {
        val currentCart = _cart.value?.toMutableMap() ?: mutableMapOf()
        currentCart.remove(product)
        _cart.postValue(currentCart)
    }

    fun clearCart() {
        _cart.postValue(mutableMapOf())
        _productCount.postValue(1)
        _totalPrice.postValue(0.0)
        Log.d(TAG, "Der Warenkorb wurde geleert.")
    }

    fun saveOrderInFireStore(order: Order) {
        repository.saveOrderInFireStore( order)
    }

    fun uploadPdfToFirebaseStorage(filePath: String, orderId: String) {
        repository.uploadPdfToFirebaseStorage(filePath, orderId)
    }

    fun setLastAdress(adress: String) {
        _lastAdress.value = adress
    }


}