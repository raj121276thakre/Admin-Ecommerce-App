package com.app.adminecommerce.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.app.adminecommerce.R
import com.app.adminecommerce.Utils
import com.app.adminecommerce.adapter.CategoryAdapter
import com.app.adminecommerce.databinding.FragmentCategoryBinding
import com.app.adminecommerce.model.CategoryModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


class CategoryFragment : Fragment() {
    private lateinit var binding: FragmentCategoryBinding

    private var imageUrl: Uri? = null
    private lateinit var dialog: Dialog

    private var launchGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            imageUrl = it.data!!.data
            binding.categoryImagePreview.setImageURI(imageUrl)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryBinding.inflate(layoutInflater)
        setStatusBarColor()
        // Inflate the layout for this fragment


        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        val textViewProgress = dialog.findViewById<TextView>(R.id.dialogText)
        textViewProgress.text = "Uploading category..." // Set your dynamic text here
        dialog.setCancelable(false)

        getData()

        binding.apply {
            categoryImagePreview.setOnClickListener {
                val intent = Intent("android.intent.action.GET_CONTENT")
                intent.type = "image/*"
                launchGalleryActivity.launch(intent)
            }

            btnCategory.setOnClickListener {

                validateData(binding.categoryName.text.toString())


            }
        }




        return binding.root
    }

    private fun getData() {
        val categoryList = ArrayList<CategoryModel>()
        Firebase.firestore.collection("categories")
            .get().addOnSuccessListener {

                categoryList.clear()
                for(doc in it.documents){
                    val data = doc.toObject(CategoryModel::class.java)
                    categoryList.add(data!!)
                }
                binding.categoryRecycler.adapter = CategoryAdapter(requireContext(), categoryList)
            }
    }

    private fun validateData(categoryName: String) {
        if (categoryName.isEmpty()) {
            Utils.showToast(requireContext(), "Please provide category name")

        } else if (imageUrl == null) {
            Utils.showToast(requireContext(), "Please select image")
        } else{
            uploadImage(categoryName)
        }
    }

    private fun uploadImage(categoryName: String) {
        dialog.show()
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("category/$fileName")
        refStorage.putFile(imageUrl!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image ->
                    storeData(categoryName  ,image.toString())
                }
            }
            .addOnFailureListener {
                dialog.dismiss()
                Utils.showToast(requireContext(), "Something went wrong")
            }


    }

    private fun storeData(categoryName: String, url: String) {

        val db = Firebase.firestore
        val data = hashMapOf<String, Any>(
            "cat" to categoryName,
            "img" to url
        )
        db.collection("categories").add(data)
            .addOnSuccessListener {
                dialog.dismiss()
                binding.categoryImagePreview.setImageResource(R.drawable.category_preview)
                binding.categoryName.text = null
                getData()
                Utils.showToast(requireContext(), "Category Added")
            }
            .addOnFailureListener {
                dialog.dismiss()
                Utils.showToast(requireContext(), "Something went wrong")
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