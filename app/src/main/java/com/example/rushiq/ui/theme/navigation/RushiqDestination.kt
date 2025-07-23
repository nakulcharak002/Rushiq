package com.example.rushiq.ui.theme.navigation

// sealed  class represent all destination in the app
sealed class RushiqDestination(val route: String) {
    data object Auth : RushiqDestination("auth")
    // home screen destination

        data object Home : RushiqDestination("home")

        data object Search : RushiqDestination("search")

        data object Cart : RushiqDestination("cart")

        data object Account : RushiqDestination("account")

        data object Welcome : RushiqDestination("welcome")

        data object Cafe : RushiqDestination("cafe")











        data object CategoryDetail : RushiqDestination("category/{categoryId}"){
            fun createRoute(categoryId : String):String{
                return "category/$categoryId"
            }
        }
        data object ProductDetails : RushiqDestination("product/{productId}"){
            fun createRoute(productId:Int ):String{
                return "product/$productId"
            }
        }

}