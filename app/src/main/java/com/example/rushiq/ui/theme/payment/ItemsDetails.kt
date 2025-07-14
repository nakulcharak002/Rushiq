package com.example.rushiq.ui.theme.payment

 data class ItemsDetails(val name : String , val priceQuantity: String) {
     data class  PriceQuantity(val price : String,
         val quantity: String,
         val subtotal : String,
     )
}