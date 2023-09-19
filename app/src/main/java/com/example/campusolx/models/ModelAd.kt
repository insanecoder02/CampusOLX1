package com.example.campusolx.models

// A data model class representing an advertisement (ad)
class ModelAd {
    var id: String? = ""             // Unique identifier for the ad (can be nullable)
    var uid: String =  ""            // User ID associated with the ad
    var brand: String = ""           // Brand or manufacturer of the item
    var category: String = ""        // Category of the ad (e.g., Electronics, Furniture)
    var price: String = ""           // Price of the item
    var title: String = ""           // Title or name of the ad
    var description: String = ""     // Description of the item or ad
    var status : String = ""         // Status of the ad (e.g., Active, Sold)
    var timestamp: Long = 0         // Timestamp when the ad was created or updated
    var latitude = 0.0               // Latitude (location) associated with the ad
    var longitude = 0.0              // Longitude (location) associated with the ad
    var imageList: ArrayList<String> = ArrayList() // List of image URLs associated with the ad
}
