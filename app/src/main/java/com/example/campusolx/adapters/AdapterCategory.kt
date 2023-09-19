package com.example.campusolx.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.campusolx.models.ModelCategory
import com.example.campusolx.interfaces.RvListenerCategory
import com.example.campusolx.databinding.RowCategoryBinding

// Adapter for displaying categories in a RecyclerView
class AdapterCategory(
    private val context: Context,
    private val categoryArrayList: ArrayList<ModelCategory>,
    private val rvListenerCategory: RvListenerCategory
) : RecyclerView.Adapter<AdapterCategory.HolderCategory>() {

    private lateinit var binding: RowCategoryBinding

    // Companion object for defining constants and static members
    private companion object {
        private const val TAG = "ADAPTER_CATEGORY_TAG"
    }

    // Create a new ViewHolder for the category item view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
        // Inflate the row_category.xml layout to create a view for a category item
        binding = RowCategoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderCategory(binding.root)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
        val modelCategory = categoryArrayList[position]
        val icon = modelCategory.icon
        val category = modelCategory.category
        val random = java.util.Random()
        val color = Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255))

        // Set category icon, text, and background color to the ViewHolder's views
        holder.categoryIconTv.setImageResource(icon)
        holder.categoryTv.text = category
        holder.categoryIconTv.setBackgroundColor(color)

        // Handle item click events
        holder.itemView.setOnClickListener {
            rvListenerCategory.onCategoryClick(modelCategory)
        }
    }

    // Return the total number of categories in the list
    override fun getItemCount(): Int {
        return categoryArrayList.size
    }

    // ViewHolder class to hold and manage the category item views
    inner class HolderCategory(itemView: View) : ViewHolder(itemView) {
        var categoryIconTv = binding.categoryIconTv
        var categoryTv = binding.categoryTv
    }
}
