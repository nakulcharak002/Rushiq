package com.example.rushiq.data.models.mealDB

import com.example.rushiq.data.models.fakeapi.Products
import com.example.rushiq.data.models.fakeapi.Rating
import com.google.gson.annotations.SerializedName
import kotlin.random.Random


data class MealResponse (
    @SerializedName("meals")
    val meals : List<MealDto>?
)
data class CategoryResponse(
    @SerializedName("categories")
    val categories : List<CategoryDto>?
)
data class MealDto(
    @SerializedName("idMeal")
    val id : String,

    @SerializedName("strMeal")
    val name: String,

    @SerializedName("strCategory")
    val category: String?,

    @SerializedName("strArea")
    val area : String?,

    @SerializedName("strInstructions")
    val instructuions : String?,

    @SerializedName("strMealThumb")
    val thumbnailUrl : String?,

    @SerializedName("strTags")
    val tags : String?

    ){
    fun toProducts(): Products{
        val randomPrice = Random.nextDouble(50.0 , 500.0)
        val randomRating = Random.nextDouble(3.5, 5.0)
        val randomReviewCount = Random.nextInt(10 , 200)

        return Products(
            id = id.toInt(),
            name = name,
            price = randomPrice,
            category = category?:"",
            imageUrl = thumbnailUrl?:"",
            imageRes = 0,
            description = instructuions?:"",
            rating = Rating(
                rate = randomRating,
                count = randomReviewCount
            )
        )

    }
}
data class CategoryDto(
    @SerializedName("idCategory")
    val id : String,

    @SerializedName("strCategory")
    val name : String?,

    @SerializedName("strCategoryThumb")
    val thumbnailUrl: String?,

    @SerializedName("strCategoryDescription")
    val description: String?,
){
    fun toCategory(): MealCategory{
        return MealCategory(
            id = id,
            name = name?:"",
            imageRes = thumbnailUrl?:""
        )
    }

}

