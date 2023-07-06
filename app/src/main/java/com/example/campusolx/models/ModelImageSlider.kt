package com.example.campusolx.models

class ModelImageSlider {
    var id: String = ""
    var imageUrl: String = ""

    constructor()
    constructor(id: String, imageUrl: String){
        this.id = id
        this.imageUrl = imageUrl
    }
}