package com.example.campusolx.models

import android.net.Uri

// A data model class representing an image that has been picked
class ModelImagePicked {
    var id = ""                // Unique identifier for the image
    var imageUri: Uri? = null  // Uri for the local image (can be null if from the internet)
    var imageUrl: String? = null // URL for the image (can be null if from the local storage)
    var fromInternet = false   // Indicates whether the image is from the internet

    // Default constructor
    constructor()

    // Parameterized constructor to initialize id, imageUri, imageUrl, and fromInternet
    constructor(id: String, imageUri: Uri?, imageUrl: String?, fromInternet: Boolean) {
        this.id = id
        this.imageUri = imageUri
        this.imageUrl = imageUrl
        this.fromInternet = fromInternet
    }
}
