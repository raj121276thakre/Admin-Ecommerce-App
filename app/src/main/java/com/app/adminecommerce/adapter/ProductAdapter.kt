package com.app.adminecommerce.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.adminecommerce.R
import com.app.adminecommerce.databinding.ItemProductBinding
import com.app.adminecommerce.model.AddProductModel
import com.bumptech.glide.Glide

class ProductAdapter(
    private val productList: ArrayList<AddProductModel>,
    private val context: Context,
    private val onEditClicked: (AddProductModel) -> Unit,
    private val onDeleteClicked: (String) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.binding.productName.text = product.productName
        holder.binding.productPrice.text = "₹${product.productSp}"

//        Glide.with(context)
//            .load(product.productCoverImg)
//            .placeholder(R.drawable.ic_placeholder_image)
//            .into(holder.binding.productImage)


        Glide.with(context).load(product.productCoverImg).into(holder.binding.productImage)

        holder.binding.editProductBtn.setOnClickListener {
            onEditClicked(product)
        }

        holder.binding.deleteProductBtn.setOnClickListener {
            product.productId?.let { it1 -> onDeleteClicked(it1) }
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}
