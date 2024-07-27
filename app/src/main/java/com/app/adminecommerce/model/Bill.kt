package com.app.adminecommerce.model


data class Bill(
    val userName: String = "",
    val userPhoneNumber: String = "",
    val userAddress: String = "",
    val productDetails: List<ProductDetail> = emptyList(),
    val totalCost: String = "",
    val orderId: String = "",
    val orderDate: String = "",
    val amountInWords: String = "",
    val paymentMode: String = "",
    val receivedPayment: String = "",
    val balance: String = "",
    val orderStatus: String = "",  // New field
    val timestamp: String = "",    // New field
    val orderNumber: Int = 0       // New field
)

data class ProductDetail(
    val productName: String = "",
    val productPrice: String = "",
    val productQuantity: Int = 0,
    val productId: String = ""
)






