package com.example.campusolx.interfaces

import com.example.campusolx.models.ModelCategory

// An interface for handling category item clicks in RecyclerView
interface RvListenerCategory {
    // Callback method invoked when a category item is clicked
    fun onCategoryClick(modelCategory: ModelCategory)
}
