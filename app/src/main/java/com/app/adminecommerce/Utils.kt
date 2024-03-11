package com.app.adminecommerce

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth

object Utils {

//    private var dialog: AlertDialog? = null
//    fun showDialog(context: Context, message: String) {
//        val progress = ProgressDialogBinding.inflate(LayoutInflater.from(context))
//        progress.tvMessage.text = message
//        dialog = AlertDialog.Builder(context).setView(progress.root).setCancelable(false).create()
//        dialog!!.show()
//    }
//
//    fun hideDialog() {
//        dialog?.dismiss()
//    }


    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }



}

