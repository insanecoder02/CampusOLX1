package com.example.campusolx.dataclass

data class ProductRequest(
    val name: String,
    val description: String,
    val category: String,
    val prices: Int,
    val images: List<Image>
)