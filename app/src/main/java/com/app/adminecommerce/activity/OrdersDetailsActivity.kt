package com.app.adminecommerce.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.adminecommerce.R
import com.app.adminecommerce.databinding.ActivityOrdersDetailsBinding
import com.app.adminecommerce.model.AllOrderModel
import com.app.adminecommerce.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore


class OrdersDetailsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityOrdersDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // enableEdgeToEdge()
        binding = ActivityOrdersDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainDetails)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        val order = intent.getSerializableExtra("orderDetails") as? AllOrderModel

        order?.let {
            binding.productName.text = it.name
            binding.price.text = "Selling price: ₹${it.price}"


           // binding.quantity.text = "Price Details (${it.quantity} Item)"
            binding.totalPrice.text = "Order Total: ₹${it.price}"
           // binding.orderStatus.text = "Status: ${it.status}"
            // Display other order details
        }

       // val userId = "1234567890"  // Replace with actual user ID
        val userId = "${order?.userId}"  // Replace with actual user ID
        getUserDetails(userId)
    }

    private fun getUserDetails(userId: String) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)

        userRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val userModel = document.toObject(UserModel::class.java)
                if (userModel != null) {
                    // Use the userModel object
                    Log.d("UserDetails", "User Data: $userModel")
                    binding.userName.setText(userModel.userName)
                    binding.userPhoneNumber.setText(userModel.userPhoneNumber)
                    binding.village.setText("${userModel.village}, " )
                    binding.city.setText(userModel.city)
                    binding.pinCode.setText(userModel.pinCode)
                    binding.state.setText("${userModel.state}, " )
                } else {
                    Log.d("UserDetails", "No such document")
                }
            } else {
                Log.d("UserDetails", "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d("UserDetails", "get failed with ", exception)
        }
    }
}
