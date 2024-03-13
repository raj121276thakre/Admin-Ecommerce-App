package com.app.adminecommerce.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.adminecommerce.Utils
import com.app.adminecommerce.databinding.AllOrderItemLayoutBinding
import com.app.adminecommerce.databinding.ImageItemBinding
import com.app.adminecommerce.model.AllOrderModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.checkerframework.checker.index.qual.GTENegativeOne


class AllOrderAdapter(val list: ArrayList<AllOrderModel>, val context: Context):
    RecyclerView.Adapter<AllOrderAdapter.AllOrderViewHolder>(){

    inner class AllOrderViewHolder(val binding : AllOrderItemLayoutBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllOrderViewHolder {
        val binding = AllOrderItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return AllOrderViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AllOrderViewHolder, position: Int) {
        holder.binding.productTitle.text = list[position].name
        holder.binding.productPrice.text = "₹" + list[position].price

        holder.binding.cancleButton.setOnClickListener {
          //  holder.binding.proceedButton.text = "Canceled"
            holder.binding.proceedButton.visibility = GONE
            updateStatus("Canceled",list[position].orderId!!)
        }

        when( list[position].status){

            "Ordered" ->{

                holder.binding.proceedButton.text = "Dispatched"

                holder.binding.proceedButton.setOnClickListener {
                    updateStatus("Dispatched",list[position].orderId!!)
                }
            } //

            "Dispatched" ->{
                holder.binding.proceedButton.text = "Delivered"

                holder.binding.proceedButton.setOnClickListener {
                    updateStatus("Delivered",list[position].orderId!!)
                }
            }//

            "Delivered" ->{
                holder.binding.proceedButton.text = "Already Delivered"
                holder.binding.cancleButton.visibility = GONE
                holder.binding.proceedButton.isEnabled = false

//                holder.binding.proceedButton.setOnClickListener {
//                    updateStatus("Canceled",list[position].orderId!!)
//                }
            }//

            "Canceled" ->{
                holder.binding.proceedButton.visibility = GONE
                holder.binding.cancleButton.isEnabled = false
            }//
        }

    }



    fun updateStatus(str: String, doc:String){
        val data = hashMapOf<String, Any>()
        data["status"] = str
        Firebase.firestore.collection("allOrders")
            .document(doc)
            .update(data)
            .addOnSuccessListener {
                Utils.showToast(context, "Status updated")
            }
            .addOnFailureListener {
                Utils.showToast(context, "Something went wrong")
            }

    }


}












