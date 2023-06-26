package com.example.campusolx

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.campusolx.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private lateinit var mContext : Context
    override fun onAttach(context: Context) {
        mContext=context
        super.onAttach(context)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadCategories()
    }

    private fun loadCategories() {
        val categoryArrayList = ArrayList<ModelCategory>()
        for (i in 0 until Utils.categories.size) {
            val modelCategory = ModelCategory(Utils.categories[i], Utils.categoryIcons[i])
            categoryArrayList.add(modelCategory)
        }
        val adapterCategory = AdapterCategory(mContext, categoryArrayList, object : RvListenerCategory {
            override fun onCategoryClick(modelCategory: ModelCategory) {

            }
        })

        // Set the adapter to the RecyclerView
        binding.categoriesRv.adapter = adapterCategory
    }
}
