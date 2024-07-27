package com.app.adminecommerce.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app.adminecommerce.R
import com.app.adminecommerce.Utils
import com.app.adminecommerce.databinding.FragmentSliderBinding
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


class SliderFragment : Fragment() {
    private lateinit var binding: FragmentSliderBinding

    private var imageUrl: Uri? = null
    private lateinit var dialog: Dialog

    private var launchGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            imageUrl = it.data!!.data
            binding.SliderImagePreview.setImageURI(imageUrl)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSliderBinding.inflate(layoutInflater)
        setStatusBarColor()
        // Inflate the layout for this fragment

        getSliderImage()

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        val textViewProgress = dialog.findViewById<TextView>(R.id.dialogText)
        textViewProgress.text = "Updating Slider Image..." // Set your dynamic text here
        dialog.setCancelable(false)

        binding.apply {
            SliderImagePreview.setOnClickListener {
                val intent = Intent("android.intent.action.GET_CONTENT")
                intent.type = "image/*"
                launchGalleryActivity.launch(intent)
            }

            uploadBtn.setOnClickListener {
                if (imageUrl != null) {
                    uploadImage(imageUrl!!)
                } else {
                    Utils.showToast(requireContext(), "Please select image")
                }
            }
        }


        return binding.root
    }

    private fun getSliderImage() {
        Firebase.firestore.collection("slider").document("item")
            .get().addOnSuccessListener {
                Glide.with(requireContext()).load(it.get("img")).into(binding.SliderImage)
            }
            .addOnFailureListener {
                Utils.showToast(requireContext(), "Something went wrong")
            }
    }


    private fun uploadImage(imageUri: Uri) {
        dialog.show()
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("slider/$fileName")
        refStorage.putFile(imageUri)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image ->
                    storeData(image.toString())
                }
            }
            .addOnFailureListener {
                dialog.dismiss()
                Utils.showToast(requireContext(), "Something went wrong")
            }


    }

    private fun storeData(image: String) {

        val db = Firebase.firestore
        val data = hashMapOf<String, Any>(
            "img" to image
        )
        db.collection("slider").document("item").set(data)
            .addOnSuccessListener {
                dialog.dismiss()
                binding.SliderImagePreview.setImageResource(R.drawable.preview)
                Utils.showToast(requireContext(), "Slider Updated")
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









