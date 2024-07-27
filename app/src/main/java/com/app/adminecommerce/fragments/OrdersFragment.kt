package com.app.adminecommerce.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.adminecommerce.R
import com.app.adminecommerce.Utils
import com.app.adminecommerce.adapter.OrdersAdapter
import com.app.adminecommerce.databinding.FragmentOrdersBinding
import com.app.adminecommerce.model.Bill
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class OrdersFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding
    private lateinit var list: ArrayList<Bill>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersBinding.inflate(layoutInflater)
        setStatusBarColor()
        // Inflate the layout for this fragment

        list = ArrayList()
        binding.allordersRecycler.layoutManager = LinearLayoutManager(requireContext())

        fetchOrders()

        setupSpinner()



        return binding.root
    }


    private fun fetchOrders() {
        FirebaseFirestore.getInstance().collection("bills")
            .get()
            .addOnSuccessListener {
                list.clear()
                for (doc in it) {
                    val data = doc.toObject(Bill::class.java)
                    list.add(data)
                }
                sortAndDisplayOrders("Ordered") // Initial sort by date and time
            }
            .addOnFailureListener {
                Utils.showToast(requireContext(), "Something went wrong")
            }
    }

    private fun setupSpinner() {
        val statuses = resources.getStringArray(R.array.order_status)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.SpinnerSortByStatus.adapter = adapter

        // Set initial selection to "Ordered"
        val initialStatus = "Ordered"
        val initialPosition = statuses.indexOf(initialStatus)
        if (initialPosition != -1) {
            binding.SpinnerSortByStatus.setSelection(initialPosition)
        }

        binding.SpinnerSortByStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedStatus = statuses[position]
                sortAndDisplayOrders(selectedStatus)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                sortAndDisplayOrders(null)
            }
        }
    }

    private fun sortAndDisplayOrders(selectedStatus: String?) {
        val sortedList = when (selectedStatus) {
            "Canceled" -> {
                list.filter { it.orderStatus == "Canceled" || it.orderStatus == "Order is Canceled by User" }
                    .sortedByDescending { parseDateTime(mergeDateTime(it.orderDate, it.timestamp)) }
            }
            else -> {
                list.filter { it.orderStatus == selectedStatus }
                    .sortedByDescending { parseDateTime(mergeDateTime(it.orderDate, it.timestamp)) }
            }
        }
        binding.allordersRecycler.adapter = OrdersAdapter(sortedList, requireContext())
    }







    private fun mergeDateTime(date: String, time: String): String {
        return "$date $time"
    }

    private fun parseDateTime(dateTime: String): Date? {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return format.parse(dateTime)
    }



    //statusbar color
    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }


}