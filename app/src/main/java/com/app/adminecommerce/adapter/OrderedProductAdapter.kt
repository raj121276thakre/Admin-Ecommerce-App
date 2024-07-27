package com.app.adminecommerce.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.adminecommerce.Utils
import com.app.adminecommerce.databinding.OrderdItemProductBinding
import com.app.adminecommerce.model.ProductDetail
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class OrderedProductAdapter(
    private val context: Context,
    private val productList: List<ProductDetail>
) : RecyclerView.Adapter<OrderedProductAdapter.OrderedProductViewHolder>() {

    inner class OrderedProductViewHolder(private val binding: OrderdItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductDetail) {
            binding.productName.text = product.productName
            binding.productPrice.text = product.productPrice
            binding.productQuantity.text = "Qty: ${product.productQuantity}"

            // Uncomment and set image loading logic here
            // binding.productImage.setImageResource(product.productImage)

            setProductImage(product.productId,binding.productImage)

            itemView.setOnClickListener {
//                val intent = Intent(context, ProductDetailsActivity::class.java)
//                intent.putExtra("id", product.productId)
//                context.startActivity(intent)
            }


        }
    }

    private fun setProductImage(proId: String?, productImage: ImageView) {

        Firebase.firestore.collection("products").document(proId!!)
            .get().addOnSuccessListener {

                val coverImage = it.get("productCoverImg")

                Glide.with(context).load(coverImage).into(productImage)

                //productImage.setImageResource(coverImage as Int)


            }.addOnFailureListener {
                Utils.showToast(context, "Something went wrong")
            }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderedProductViewHolder {
        val binding = OrderdItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderedProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderedProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size
}
