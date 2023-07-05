package com.example.campusolx.dataclass

data class Product(
    val category: String,
    val description: String,
    val images: List<Image>,
    val name: String,
    val price: Int
)