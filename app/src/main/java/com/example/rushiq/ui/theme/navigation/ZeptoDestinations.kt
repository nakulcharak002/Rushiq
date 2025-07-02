package com.example.rushiq.ui.theme.navigation

import okhttp3.Route
    // sealed  class represent all destination in the app
sealed class ZeptoDestinations(val route: String) {
    data object Auth : ZeptoDestinations("auth")
    // home screen destination

        data object Home : ZeptoDestinations("home")

        data object Search : ZeptoDestinations("search")





        data object CategoryDetail : ZeptoDestinations("category/{categoryId}"){
            fun createRoute(categoryId : String):String{
                return "category/$categoryId"
            }
        }
        data object ProductDetails : ZeptoDestinations("product/{productId}"){
            fun createRoute(productId:Int ):String{
                return "product/$productId"
            }
        }

}