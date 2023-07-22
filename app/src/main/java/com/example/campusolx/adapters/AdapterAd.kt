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

class AdapterAd(private val context: Context, private val adArrayList: List<ModelAd>) :
    RecyclerView.Adapter<AdapterAd.HolderAd>() {

    interface OnAdClickListener {
        fun onAdClick(productId: String)
    }

    private lateinit var binding: RowAdBinding
    private var onAdClickListener: OnAdClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderAd {
        binding = RowAdBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderAd(binding.root)
    }

    override fun onBindViewHolder(holder: HolderAd, position: Int) {
        val modelAd = adArrayList[position]
        val title = modelAd.title
        val description = modelAd.description
        val price = modelAd.price
        val timestamp = modelAd.timestamp
        val formattedDate = Utils.formatTimestampDate(timestamp)

        loadAdFirstImage(modelAd, holder)
        holder.titleTv.text = title
        holder.descriptionTv.text = description
        holder.priceTv.text = price
        holder.dateTv.text = formattedDate

        holder.itemView.setOnClickListener {
            val productId = modelAd.id
            if (productId != null) {
                onAdClickListener?.onAdClick(productId)
            }
        }
    }

    fun setOnAdClickListener(listener: OnAdClickListener) {
        this.onAdClickListener = listener
    }

    private fun loadAdFirstImage(modelAd: ModelAd, holder: HolderAd) {
        val imageList = modelAd.imageList

        if (imageList.isNotEmpty()) {
            val firstImageUrl = imageList[0] // Assuming the first image URL is at index 0
            // Load the first image into the ImageView using a library like Glide or Picasso
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

    override fun getItemCount(): Int {
        return adArrayList.size
    }

    inner class HolderAd(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageTv = binding.imageTv
        var titleTv = binding.titleTv
        var descriptionTv = binding.descriptionTv
//        var favBtn = binding.favBtn
        var priceTv = binding.priceTv
        var dateTv = binding.dateTv
    }
}
