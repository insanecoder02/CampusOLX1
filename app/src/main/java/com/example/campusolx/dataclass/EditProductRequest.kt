package com.example.campusolx.dataclass

data class EditProductRequest(
    val name: String?,
    val description: String?,
    val category: String?,
    val prices: Int?,
    var images: List<String>?
)
