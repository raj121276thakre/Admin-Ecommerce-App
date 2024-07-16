package com.app.adminecommerce.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app.adminecommerce.R
import com.app.adminecommerce.Utils
import com.app.adminecommerce.adapter.AddProductImageAdapter
import com.app.adminecommerce.databinding.FragmentAddProductBinding
import com.app.adminecommerce.model.AddProductModel
import com.app.adminecommerce.model.CategoryModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide


class AddProductFragment : Fragment() {

    private lateinit var binding: FragmentAddProductBinding
    private lateinit var list: ArrayList<Uri>
    private lateinit var listImages: ArrayList<String>
    private lateinit var adapter: AddProductImageAdapter
    private var coverImage: Uri? = null
    private var coverImgUrl: String? = ""
    private lateinit var dialog: Dialog
    private lateinit var categoryList: ArrayList<String>

    private var productId: String? = null

    private val args: AddProductFragmentArgs by navArgs()

    private var launchGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            coverImage = it.data!!.data
            binding.productCoverImg.setImageURI(coverImage)
            binding.productCoverImg.visibility = VISIBLE
        }
    }

    private var launchProductActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val imageUrl = it.data!!.data
            list.add(imageUrl!!)
            adapter.notifyDataSetChanged()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddProductBinding.inflate(layoutInflater)
        setStatusBarColor()
        // Inflate the layout for this fragment

        list = ArrayList()
        listImages = ArrayList()


        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        val textViewProgress = dialog.findViewById<TextView>(R.id.dialogText)
        textViewProgress.text = "Adding Product..." // Set your dynamic text here
        dialog.setCancelable(false)

        binding.selectCoverImg.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchGalleryActivity.launch(intent)
        }

        binding.productImgBtn.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchProductActivity.launch(intent)
        }

        setProductCategory()

        adapter = AddProductImageAdapter(list)
        binding.productImgRecyclerView.adapter = adapter


        // Check if args.product is null to determine button text
        if (args.product == null) {
            binding.submitProductBtn.text = "Add Product"
        } else {
            binding.submitProductBtn.text = "Update Product"
        }

        binding.submitProductBtn.setOnClickListener {
            if (productId.isNullOrEmpty()) {

                validateDataForNewProduct()

            } else {

                updateProductData()
            }
        }


        populateProductDetails()

        return binding.root
    }



    private fun populateProductDetails() {
        val product = args.product
        if (product != null) {
            binding.productNameEdt.setText(product.productName)
            binding.productDescriptionEdt.setText(product.productDescription)
            binding.productMrpEdt.setText(product.productMrp)
            binding.productSpEdt.setText(product.productSp)
            productId = product.productId
            coverImgUrl = product.productCoverImg

            // Load cover image
            Glide.with(this).load(product.productCoverImg).into(binding.productCoverImg)
            binding.productCoverImg.visibility = VISIBLE

            // Load product images
            listImages.addAll(product.productImages)
            adapter.notifyDataSetChanged()
        }
    }

    private fun validateDataForNewProduct() {
        if (binding.productNameEdt.text.toString().isEmpty()) {
            binding.productNameEdt.requestFocus()
            binding.productNameEdt.error = "Empty"
        } else if (binding.productDescriptionEdt.text.toString().isEmpty()) {
            binding.productDescriptionEdt.requestFocus()
            binding.productDescriptionEdt.error = "Empty"
        } else if (binding.productMrpEdt.text.toString().isEmpty()) {
            binding.productMrpEdt.requestFocus()
            binding.productMrpEdt.error = "Empty"
        } else if (binding.productSpEdt.text.toString().isEmpty()) {
            binding.productSpEdt.requestFocus()
            binding.productSpEdt.error = "Empty"
        } else if (coverImage == null) {
            Utils.showToast(requireContext(), "Please select cover image")
        } else if (list.size < 1) {
            Utils.showToast(requireContext(), "Please select product images")
        } else {
            uploadImage()
        }
    }

    private fun updateProductData() {
        dialog.show()
        val db = Firebase.firestore
        val productRef = db.collection("products").document(productId!!)

        val updatedData = hashMapOf<String, Any>(
            "productName" to binding.productNameEdt.text.toString(),
            "productDescription" to binding.productDescriptionEdt.text.toString(),
            "productMrp" to binding.productMrpEdt.text.toString(),
            "productSp" to binding.productSpEdt.text.toString(),
            "productImages" to listImages
        )

        productRef.update(updatedData as HashMap<String, Any>)
            .addOnSuccessListener {
                dialog.dismiss()
                Utils.showToast(requireContext(), "Product Updated")
            }
            .addOnFailureListener {
                dialog.dismiss()
                Utils.showToast(requireContext(), "Failed to update product")
            }
    }







//    private fun validateData() {
//        if (binding.productNameEdt.text.toString().isEmpty()) {
//            binding.productNameEdt.requestFocus()
//            binding.productNameEdt.error = "Empty"
//
//        } else if (binding.productDescriptionEdt.text.toString().isEmpty()) {
//            binding.productDescriptionEdt.requestFocus()
//            binding.productDescriptionEdt.error = "Empty"
//
//        } else if (binding.productMrpEdt.text.toString().isEmpty()) {
//            binding.productMrpEdt.requestFocus()
//            binding.productMrpEdt.error = "Empty"
//
//        } else if (binding.productSpEdt.text.toString().isEmpty()) {
//            binding.productSpEdt.requestFocus()
//            binding.productSpEdt.error = "Empty"
//
//        } else if (coverImage == null) {
//            Utils.showToast(requireContext(), "Please select cover image")
//
//        } else if (list.size < 1) {
//            Utils.showToast(requireContext(), "Please select product images")
//        } else {
//            uploadImage()
//        }
//    }


    private fun uploadImage() {
        dialog.show()
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("products/$fileName")
        refStorage.putFile(coverImage!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image ->
                    coverImgUrl = image.toString()
                    uploadProductImage()
                }
            }
            .addOnFailureListener {
                dialog.dismiss()
                Utils.showToast(requireContext(), "Something went wrong")
            }


    }

    private var i = 0
    private fun uploadProductImage() {
        dialog.show()
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("products/$fileName")
        refStorage.putFile(list[i])
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image ->
                    listImages.add(image!!.toString())
                    if (list.size == listImages.size) {
                        storeData()
                    } else {
                        i += 1
                        uploadProductImage()
                    }

                }
            }
            .addOnFailureListener {
                dialog.dismiss()
                Utils.showToast(requireContext(), "Something went wrong")
            }


    }

    private fun storeData() {
        val db = Firebase.firestore.collection("products")
        val key = db.document().id
        val data = AddProductModel(
            binding.productNameEdt.text.toString(),
            binding.productDescriptionEdt.text.toString(),
            coverImgUrl.toString(),
            categoryList[binding.productCategoryDropdown.selectedItemPosition],
            key,
            binding.productMrpEdt.text.toString(),
            binding.productSpEdt.text.toString(),
            listImages
        )
        db.document(key).set(data).addOnSuccessListener {
            dialog.dismiss()
            Utils.showToast(requireContext(), "Product Added")
            binding.productNameEdt.text = null
            binding.productDescriptionEdt.text = null
            binding.productMrpEdt.text = null
            binding.productSpEdt.text = null
        }
            .addOnFailureListener {

                dialog.dismiss()
                Utils.showToast(requireContext(), "Something went wrong")
            }

    }

    private fun setProductCategory() {
        categoryList = ArrayList()
        Firebase.firestore.collection("categories")
            .get().addOnSuccessListener {

                categoryList.clear()
                for (doc in it.documents) {
                    val data = doc.toObject(CategoryModel::class.java)
                    data?.cat?.let { it1 -> categoryList.add(it1) }
                }
                categoryList.add(0, "Select Category")

                val arrayAdapter =
                    ArrayAdapter(requireContext(), R.layout.dropdown_item_layout, categoryList)

                binding.productCategoryDropdown.adapter = arrayAdapter
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