package com.example.campusolx.models

// A data model class representing an image for a slider
class ModelImageSlider {
    var id: String = ""      // Unique identifier for the image
    var imageUrl: String = "" // URL or path to the image

    // Default constructor
    constructor()

    // Parameterized constructor to initialize id and imageUrl
    constructor(id: String, imageUrl: String){
        this.id = id
        this.imageUrl = imageUrl
    }
}
