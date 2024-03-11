package com.app.adminecommerce.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.app.adminecommerce.R
import com.app.adminecommerce.databinding.ItemCategoryLayoutBinding
import com.app.adminecommerce.model.CategoryModel
import com.bumptech.glide.Glide

class CategoryAdapter(var context: Context, val categoryList: ArrayList<CategoryModel>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(view: View) : ViewHolder(view) {
        val binding = ItemCategoryLayoutBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_category_layout, parent, false)
        )
    }


    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.binding.catName.text = categoryList[position].cat
        Glide.with(context).load(categoryList[position].img).into(holder.binding.categoryImg)
    }


    override fun getItemCount(): Int {
        return categoryList.size
    }

}




