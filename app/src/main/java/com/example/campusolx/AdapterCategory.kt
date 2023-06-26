package com.example.campusolx

import android.content.Context
import android.graphics.Color
import android.view.Display.Mode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.campusolx.databinding.FragmentAccountBinding
import com.example.campusolx.databinding.RowCategoryBinding
import kotlin.random.Random

class AdapterCategory(
    private val context: Context,
    private val categoryArrayList: ArrayList<ModelCategory>,
    private val rvListenerCategory: RvListenerCategory

) : RecyclerView.Adapter<AdapterCategory.HolderCategory>(){

    private lateinit var binding: RowCategoryBinding
    private companion object{
        private const val TAG = "ADAPTER_CATEGORY_TAG"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
        binding = RowCategoryBinding.inflate(LayoutInflater.from(context), parent , false)
        return HolderCategory(binding.root)
    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
        val modelCategory = categoryArrayList[position]
        val icon = modelCategory.icon
        val category = modelCategory.category
        val random = java.util.Random()
        val color = Color.argb(255,random.nextInt(255),random.nextInt(255),random.nextInt(255))
        holder.categoryIconTv.setImageResource(icon)
        holder.categoryTv.text = category
        holder.categoryIconTv.setBackgroundColor(color)
        holder.itemView.setOnClickListener{
            rvListenerCategory.onCategoryClick(modelCategory)
        }
    }

    override fun getItemCount(): Int {
        return categoryArrayList.size
    }

    inner class HolderCategory (itemView: View) : ViewHolder(itemView){
        var categoryIconTv = binding.categoryIconTv
        var categoryTv = binding.categoryTv

    }

}