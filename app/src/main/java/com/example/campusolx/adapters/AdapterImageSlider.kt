package com.example.campusolx.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campusolx.databinding.RowImageSliderBinding
import com.example.campusolx.models.ModelImageSlider
import com.google.android.material.imageview.ShapeableImageView

class AdapterImageSlider(private val context: Context, private val imageArrayList: ArrayList<ModelImageSlider>) :
    RecyclerView.Adapter<AdapterImageSlider.HolderImageSlider>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImageSlider {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowImageSliderBinding.inflate(inflater, parent, false)
        return HolderImageSlider(binding)
    }

    override fun getItemCount(): Int {
        return imageArrayList.size
    }

    override fun onBindViewHolder(holder: HolderImageSlider, position: Int) {
        val model = imageArrayList[position]
        val imageUrl = model.imageUrl
        val imageCount = "${position+1}/${imageArrayList.size}"
     holder.imageCountTv.text = imageCount
    }

    inner class HolderImageSlider(private val binding: RowImageSliderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var imageIv: ShapeableImageView = binding.imageTv
        var imageCountTv: TextView = binding.imageCount
    }


}
