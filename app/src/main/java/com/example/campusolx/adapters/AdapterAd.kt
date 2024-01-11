package com.example.campusolx.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusolx.R
import com.example.campusolx.databinding.RowAdBinding
import com.example.campusolx.models.ModelAd
import com.example.campusolx.utils.Utils

// Adapter for displaying ads in a RecyclerView
class AdapterAd(private val context: Context, private val adArrayList: List<ModelAd>) :
    RecyclerView.Adapter<AdapterAd.HolderAd>() {

    // Interface to handle ad item click events
    interface OnAdClickListener {
        fun onAdClick(productId: String)
    }

    private lateinit var binding: RowAdBinding
    private var onAdClickListener: OnAdClickListener? = null

    // Create a new ViewHolder for the ad item view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderAd {
        // Inflate the row_ad.xml layout to create a view for an ad item
        binding = RowAdBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderAd(binding.root)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: HolderAd, position: Int) {
        // Get the ad data for the current position
        val modelAd = adArrayList[position]
        val title = modelAd.title
        val description = modelAd.description
        val price = modelAd.price
        val timestamp = modelAd.timestamp
        val formattedDate = Utils.formatTimestampDate(timestamp)

        // Load the first image of the ad (if available) using Glide
        loadAdFirstImage(modelAd, holder)

        // Set ad details to the ViewHolder's views
        holder.titleTv.text = title
//        holder.descriptionTv.text = description
        holder.priceTv.text = price
        holder.dateTv.text = formattedDate

        // Handle item click events
        holder.itemView.setOnClickListener {
            val productId = modelAd.id
            if (productId != null) {
                onAdClickListener?.onAdClick(productId)
            }
        }
    }

    // Set a click listener for ad item clicks
    fun setOnAdClickListener(listener: OnAdClickListener?) {
        this.onAdClickListener = listener
    }

    // Load the first image of the ad into the ImageView using Glide
    private fun loadAdFirstImage(modelAd: ModelAd, holder: HolderAd) {
        val imageList = modelAd.imageList

        if (imageList.isNotEmpty()) {
            val firstImageUrl = imageList[0] // Assuming the first image URL is at index 0
            // Load the first image into the ImageView using Glide
            // Replace 'placeholderImage' with the default image resource to display while loading
            Glide.with(context)
                .load(firstImageUrl)
                .placeholder(R.drawable.ic_person)
                .into(holder.imageTv)
        } else {
            // If no images are available, you can set a placeholder image or hide the ImageView
            holder.imageTv.setImageResource(R.drawable.ic_person)
        }
    }

    // Return the total number of ads in the list
    override fun getItemCount(): Int {
        return adArrayList.size
    }

    // ViewHolder class to hold and manage the ad item views
    inner class HolderAd(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageTv = binding.imageTv
        var titleTv = binding.titleTv
//        var descriptionTv = binding.descriptionTv
        var priceTv = binding.priceTv
        var dateTv = binding.dateTv
    }
}
