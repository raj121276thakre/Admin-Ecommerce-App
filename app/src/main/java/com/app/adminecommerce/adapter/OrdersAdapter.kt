package com.app.adminecommerce.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.adminecommerce.R
import com.app.adminecommerce.Utils
import com.app.adminecommerce.activity.OrderDetailsActivity
import com.app.adminecommerce.model.Bill
import com.google.firebase.firestore.FirebaseFirestore


class OrdersAdapter(private val list: List<Bill>, private val context: Context) :
    RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>() {

    //Set the views to .....................................................................................................................

    inner class OrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(bill: Bill) {
           // itemView.findViewById<TextView>(R.id.order_Number).text = "Order : ${bill.orderNumber}"
            itemView.findViewById<TextView>(R.id.tv_order_id).text = bill.orderId
            itemView.findViewById<TextView>(R.id.tv_order_date).text = bill.orderDate
            itemView.findViewById<TextView>(R.id.tv_order_time).text = bill.timestamp
            itemView.findViewById<TextView>(R.id.order_status).text = bill.orderStatus
            itemView.findViewById<TextView>(R.id.tv_order_total).text = "₹${bill.totalCost}"

            itemView.findViewById<ImageView>(R.id.show_Products_btn).setOnClickListener {
                itemView.findViewById<LinearLayout>(R.id.productsll).visibility = View.VISIBLE
                itemView.findViewById<ImageView>(R.id.show_Products_btn).visibility = View.GONE
                itemView.findViewById<ImageView>(R.id.hide_Products_btn).visibility = View.VISIBLE

            }

            itemView.findViewById<ImageView>(R.id.hide_Products_btn).setOnClickListener {
                itemView.findViewById<LinearLayout>(R.id.productsll).visibility = View.GONE
                itemView.findViewById<ImageView>(R.id.show_Products_btn).visibility = View.VISIBLE
                itemView.findViewById<ImageView>(R.id.hide_Products_btn).visibility = View.GONE

            }

            // products showing
            itemView.findViewById<RecyclerView>(R.id.rvOrderedProducts).layoutManager =
                LinearLayoutManager(context)
            itemView.findViewById<RecyclerView>(R.id.rvOrderedProducts).adapter =
                OrderedProductAdapter(context, bill.productDetails)


            val statusSpinner = itemView.findViewById<Spinner>(R.id.status_spinner)
            val statusSpinnerLayout = itemView.findViewById<LinearLayout>(R.id.spinnerLayout)
            val orderStatus = itemView.findViewById<TextView>(R.id.order_status)
            val status = bill.orderStatus

            checkOrderStatus(status,statusSpinnerLayout)
            // Set up the spinner
            setupSpinner(statusSpinner, bill, orderStatus)


            itemView.setOnClickListener {
                val intent = Intent(context, OrderDetailsActivity::class.java)
                intent.putExtra("orderId", bill.orderId)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.order_bill_item, parent, false)
        return OrdersViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }


    private fun setupSpinner(spinner: Spinner, bill: Bill, orderStatus: TextView) {
        val statuses = context.resources.getStringArray(R.array.order_status)
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, statuses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Set the spinner selection based on the current status
        val statusPosition = statuses.indexOf(bill.orderStatus)
        if (statusPosition >= 0) {
            spinner.setSelection(statusPosition)
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val newStatus = parent?.getItemAtPosition(position) as String
                if (newStatus != bill.orderStatus) {
                    updateOrderStatus(bill.orderId, newStatus)
                    orderStatus.text = newStatus

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }


    private fun updateOrderStatus(
        orderId: String,
        newStatus: String,

    ) {
        // Get a reference to the Firestore instance
        val firestore = FirebaseFirestore.getInstance()

        // Specify the path to the document to update
        val orderRef = firestore.collection("bills").document(orderId)

        // Update the orderStatus field
        orderRef.update("orderStatus", newStatus)
            .addOnSuccessListener {
                // Optionally handle success
                Utils.showToast(context,"${orderId} Order Status updated to ${newStatus}")
                // For example, you could show a toast or log a message
            }
            .addOnFailureListener { e ->
                // Optionally handle failure
                // For example, you could show a toast or log an error message
                Utils.showToast(context,"Failed to Update Status of order id :${orderId} ")
                e.printStackTrace()
            }
    }



    private fun checkOrderStatus(status: String, statusSpinnerLayout: LinearLayout) {
        // Hide spinnerLayout  if the status is already canceled.
        if (status == "Order is Canceled by User" || status == "Canceled") {
            statusSpinnerLayout.visibility = View.GONE
        } else {
            statusSpinnerLayout.visibility = View.VISIBLE
        }

    }


}



