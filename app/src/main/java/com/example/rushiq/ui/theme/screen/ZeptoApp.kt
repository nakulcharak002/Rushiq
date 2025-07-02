import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.rushiq.data.models.fakeapi.Category

fun getCategoryGradient(category: Category): Brush {
    return when (category.name.lowercase()) {
        "jewelery" -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF9D6300), // Dark gold
                Color(0xFFFDD38F)  // Light gold
            ),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )

        "electronics" -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF012654), // Deep navy blue
                Color(0xFF78C4FF)  // Bright blue
            ),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )

        "women's clothing" -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF77011F), //  Rich red
                Color(0xFFFF89A3)  // Light pink
            ),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )

        "men's clothing" -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF005F1C), // Deep green
                Color(0xFF7AFAA5)  //  Light mint
            ),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )


        "all" -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF2B0466), // Deep violet
                Color(0xFFA669FF)  // Light purple
            ),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )
        else -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF985C00), // Deep amber
                Color(0xFFFFCC80)  // Peachy gold
            ),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )





    }
}
