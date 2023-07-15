package com.example.campusolx.dataclass

data class CreateProductRequest(
    val name: String,
    val description: String,
    val category: String,
    val price: Int,
    var images: List<String>?
)