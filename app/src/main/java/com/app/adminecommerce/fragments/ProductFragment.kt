package com.app.adminecommerce.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.adminecommerce.R
import com.app.adminecommerce.adapter.ProductAdapter
import com.app.adminecommerce.databinding.FragmentProductBinding
import com.app.adminecommerce.model.AddProductModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class ProductFragment : Fragment() {
    private lateinit var binding: FragmentProductBinding
    private lateinit var productList: ArrayList<AddProductModel>
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductBinding.inflate(layoutInflater)
        setStatusBarColor()
        // Inflate the layout for this fragment

        binding.floatingActionButton.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_productFragment_to_addProductFragment)
        }

        productList = ArrayList()
        adapter = ProductAdapter(productList, requireContext(), ::onEditClicked, ::onDeleteClicked)
        binding.productRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.productRecyclerView.adapter = adapter

        fetchProducts()


        return binding.root
    }

    private fun fetchProducts() {
        FirebaseFirestore.getInstance().collection("products")
            .get().addOnSuccessListener { result ->
                productList.clear()
                for (document in result) {
                    val product = document.toObject(AddProductModel::class.java)
                    productList.add(product)
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun onEditClicked(product: AddProductModel) {
        val action = ProductFragmentDirections.actionProductFragmentToAddProductFragment(product)
        Navigation.findNavController(requireView()).navigate(action)
    }


    private fun onDeleteClicked(productId: String) {
        FirebaseFirestore.getInstance().collection("products").document(productId)
            .delete().addOnSuccessListener {
                fetchProducts() // Refresh the list
            }
    }




    //status bar color
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