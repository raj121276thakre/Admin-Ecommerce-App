package com.app.adminecommerce.model

//
//data class AddProductModel(
//    val productName: String = "",
//    val productDescription: String = "",
//    val productCoverImg: String = "",
//    val productCategory: String = "",
//    val productId: String = "",
//    val productMrp: String = "",
//    val productSp: String = "",
//    val productImages: List<String> = listOf()
//) {
//    // No-argument constructor for Firestore
//    constructor() : this("", "", "", "", "", "", "", listOf())
//}


import android.os.Parcel
import android.os.Parcelable

data class AddProductModel(
    val productName: String = "",
    val productDescription: String = "",
    val productCoverImg: String = "",
    val productCategory: String = "",
    val productId: String = "",
    val productMrp: String = "",
    val productSp: String = "",
    val productImages: List<String> = listOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: listOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productName)
        parcel.writeString(productDescription)
        parcel.writeString(productCoverImg)
        parcel.writeString(productCategory)
        parcel.writeString(productId)
        parcel.writeString(productMrp)
        parcel.writeString(productSp)
        parcel.writeStringList(productImages)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddProductModel> {
        override fun createFromParcel(parcel: Parcel): AddProductModel {
            return AddProductModel(parcel)
        }

        override fun newArray(size: Int): Array<AddProductModel?> {
            return arrayOfNulls(size)
        }
    }

    // No-argument constructor for Firestore
    constructor() : this("", "", "", "", "", "", "", listOf())
}
