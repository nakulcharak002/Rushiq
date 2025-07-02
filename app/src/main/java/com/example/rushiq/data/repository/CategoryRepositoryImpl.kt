package com.example.rushiq.data.repository
import com.example.rushiq.data.models.fakeapi.Category
import com.example.rushiq.data.api.FakeStoreApiServices
import com.example.zepto.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val apiService : FakeStoreApiServices
): CategoryRepository{

    override fun getCategories(): Flow<List<Category>>  = flow{
        val apiCategories = apiService.fetchCategories()
        val mappedCategories = mapApiCategoriesToUiCategories(apiCategories)
        emit(mappedCategories)
    }

    override suspend fun getCategoryByIdOrName(idOrName: String): Category? {
        val categories = mapApiCategoriesToUiCategories(apiService.fetchCategories())
       val id = idOrName.toIntOrNull()
        if(id != null){
            return categories.find { it.id == id }

        }
        return categories.find {
            it.name.equals(idOrName , ignoreCase = true) ||
                    it.name.replace(" ", "").equals(idOrName.replace(" ",""), ignoreCase = true)
        }
    }


    private fun mapApiCategoriesToUiCategories(apiCategories:List<String>):List<Category>{
        val allCategory = Category(0,"All" , R.drawable.shopping_bag_svgrepo_com)
        val mappedCategories = apiCategories.mapIndexed{ index , categoryName ->
            val iconRes = when (categoryName.lowercase()){
                "electronics" -> R.drawable.headphones_round_svgrepo_com
                "jewelery" -> R.drawable.diamond_01_svgrepo_com
                "men's clothing" -> R.drawable.fashion
                "women's clothing" -> R.drawable.beauty
                else -> R.drawable.allll
            }
            Category(index + 1 , formatCategoryName(categoryName), iconRes)
        }
        return listOf(allCategory) + mappedCategories

    }

    private fun formatCategoryName(name: String):String{
        return name.split(" ").joinToString ( " " ){ word ->
            word.replaceFirstChar { it.uppercase() }

        }
    }

}
