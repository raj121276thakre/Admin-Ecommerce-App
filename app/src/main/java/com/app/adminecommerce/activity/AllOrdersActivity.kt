package com.app.adminecommerce.activity

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.adminecommerce.R
import com.app.adminecommerce.Utils
import com.app.adminecommerce.adapter.AllOrderAdapter
import com.app.adminecommerce.databinding.ActivityAllOrdersBinding
import com.app.adminecommerce.model.AllOrderModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class AllOrdersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAllOrdersBinding
    private lateinit var list: ArrayList<AllOrderModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        list= ArrayList()

        Firebase.firestore.collection("allOrders")
            .get()
            .addOnSuccessListener {
                list.clear()

                for (doc in it){
                    val data = doc.toObject(AllOrderModel::class.java)
                    list.add(data)

                }
                binding.allordersRecycler.adapter = AllOrderAdapter(list,this)
            }
            .addOnFailureListener {
                Utils.showToast(this, "Something went wrong")
            }




    }


}