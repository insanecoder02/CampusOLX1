package com.example.campusolx.dataclass

data class Product(
    val __v: Int,
    val _id: String,
    val category: String,
    val createdAt: String,
    val createdBy: String,
    val description: String,
    val images: List<String>?,
    val isSold: Boolean,
    val name: String,
    val price: Int
)