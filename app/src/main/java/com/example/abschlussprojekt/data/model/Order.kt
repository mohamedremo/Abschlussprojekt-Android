package com.example.abschlussprojekt.data.model

import java.util.Date

data class Order(
    val orderId: String,
    val orderDate: Date,
    val orderTo: String, ///
    val orderStatus: String,
    val products: List<Product>,
    val totalPrice: Double
)
