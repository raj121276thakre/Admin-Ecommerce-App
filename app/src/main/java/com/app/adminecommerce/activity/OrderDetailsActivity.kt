package com.app.adminecommerce.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.adminecommerce.R
import com.app.adminecommerce.Utils
import com.app.adminecommerce.databinding.ActivityOrderDetailsBinding
import com.app.adminecommerce.model.Bill
import com.app.adminecommerce.adapter.OrderedProductAdapter

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class OrderDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.orderDetails)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setStatusBarColor()


        val orderId = intent.getStringExtra("orderId") ?: return

        Firebase.firestore.collection("bills").document(orderId).get()
            .addOnSuccessListener { document ->
                val bill = document.toObject(Bill::class.java)
                if (bill != null) {
                    displayOrderDetails(bill)
                }
            }
            .addOnFailureListener {
                // Handle the error
            }


    }

    private fun displayOrderDetails(bill: Bill) {

        binding.tvOrderId.text = bill.orderId
        binding.toolbarOrderId.text = "${bill.orderId}"
        binding.tvOrderDate.text = bill.orderDate
        binding.tvOrderTime.text = bill.timestamp
        binding.tvOrderPayment.text = bill.paymentMode
        binding.tvOrderTotal.text = "₹${bill.totalCost}"
        binding.tvOrderSubtotal.text = "₹${bill.totalCost}" // Assuming subtotal and total cost are the same
        binding.tvOrderDelivery.text = "FREE" // Set this appropriately
        binding.tvOrderFinalTotal.text = "₹${bill.totalCost}"

        binding.userName.text = bill.userName
        binding.userNumber.text = bill.userPhoneNumber
        binding.shippingAddress.text = bill.userAddress
        binding.tvTotalProducts.text = bill.productDetails.size.toString()

        // Update order status views
        binding.orederStatus.text = bill.orderStatus
        // These need to be updated according to the order status, assuming icons are set elsewhere

        // Update product details
        // Set up RecyclerView
        binding.rvOrderedProducts.layoutManager = LinearLayoutManager(this)
        binding.rvOrderedProducts.adapter = OrderedProductAdapter(this, bill.productDetails)



    }




    // Set the status bar color
    private fun setStatusBarColor() {
        window.apply {
            val statusBarColors = ContextCompat.getColor(this@OrderDetailsActivity, R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }






}