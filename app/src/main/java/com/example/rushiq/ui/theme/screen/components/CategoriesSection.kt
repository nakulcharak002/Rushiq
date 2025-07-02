package com.example.rushiq.ui.theme.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.rushiq.data.models.fakeapi.Category
  import androidx. compose. foundation. lazy. items


@Composable
fun CategoriesSection(
    categories : List<Category>,
    onCategorySelection: (Category) -> Unit,
     selectedCategory: Category?
) {
    val lazyListState = rememberLazyListState()
    Box(
        modifier =
            Modifier.fillMaxSize()

    ){
        Column {
            LazyRow(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxWidth() // more appropriate than fillMaxSize
                    .padding(bottom = 2.dp)
            ) {
                items(categories) { category -> //  Correct
                    CategoryItem(
                        category = category,
                        onClick = { onCategorySelection(category) },
                        isSelected = category.id == selectedCategory?.id,
                    )
                }
            }

            //white tan indicator line at bottom
            Box(
             modifier =
                 Modifier.fillMaxWidth()
                     .height(2.dp)
                     .background(Color.White)
            ) {  }
        }
    }

}