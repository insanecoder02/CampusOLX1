package com.example.campusolx.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campusolx.databinding.RowImageSliderBinding
import com.example.campusolx.models.ModelImageSlider
import com.google.android.material.imageview.ShapeableImageView

// Adapter for displaying images in a slider view
class AdapterImageSlider(
    private val context: Context,
    private val imageArrayList: ArrayList<ModelImageSlider>
) : RecyclerView.Adapter<AdapterImageSlider.HolderImageSlider>() {

    // Create a new ViewHolder for the image item view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImageSlider {
        // Inflate the row_image_slider.xml layout to create a view for an image item in the slider
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowImageSliderBinding.inflate(inflater, parent, false)
        return HolderImageSlider(binding)
    }

    // Return the total number of images in the slider
    override fun getItemCount(): Int {
        return imageArrayList.size
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: HolderImageSlider, position: Int) {
        val model = imageArrayList[position]
        val imageUrl = model.imageUrl
        val imageCount = "${position + 1}/${imageArrayList.size}"
        holder.imageCountTv.text = imageCount
    }

    // ViewHolder class to hold and manage the image item views in the slider
    inner class HolderImageSlider(private val binding: RowImageSliderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var imageIv: ShapeableImageView = binding.imageTv
        var imageCountTv: TextView = binding.imageCount
    }
}
