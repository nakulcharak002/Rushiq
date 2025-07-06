package com.example.rushiq.ui.theme.utils

import android.util.Log
import com.example.rushiq.data.models.fakeapi.CartItem
import com.example.rushiq.data.models.fakeapi.Products
import com.example.rushiq.data.models.fakeapi.Rating
import com.google.gson.*
import java.lang.reflect.Type

object CartJsonAdapters {
    private const val TAG = "CartJsonAdapters"

    fun createGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(CartItem::class.java, CartItemAdapter())
            .registerTypeAdapter(Products::class.java, ProductAdapter())
            .create()
    }

    private class CartItemAdapter : JsonSerializer<CartItem>, JsonDeserializer<CartItem> {

        override fun serialize(
            src: CartItem,
            typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            val jsonObject = JsonObject()
            jsonObject.add("product", context.serialize(src.products))
            jsonObject.addProperty("quantity", src.quantity)
            return jsonObject
        }

        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): CartItem {
            return try {
                val jsonObject = json.asJsonObject
                val product = context.deserialize<Products>(
                    jsonObject.get("product"),
                    Products::class.java
                )
                val quantity = jsonObject.get("quantity").asInt
                CartItem(products = product, quantity = quantity)
            } catch (e: Exception) {
                Log.d(TAG, "Error deserializing CartItem", e)
                throw JsonParseException("Failed to parse CartItem", e)
            }
        }
    }

    private class ProductAdapter : JsonSerializer<Products>, JsonDeserializer<Products> {

        override fun serialize(
            src: Products,
            typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            val jsonObject = JsonObject()

            jsonObject.addProperty("id", src.id)
            jsonObject.addProperty("name", src.name)
            jsonObject.addProperty("price", src.price)
            jsonObject.addProperty("imageUrl", src.imageUrl ?: "")

            src.category?.let { jsonObject.addProperty("category", it) }
            src.description?.let { jsonObject.addProperty("description", it) }
            src.imageRes?.let { jsonObject.addProperty("imageRes", it) }

            src.rating?.let { rating ->
                val ratingObject = JsonObject()
                ratingObject.addProperty("rate", rating.rate)
                ratingObject.addProperty("count", rating.count)
                jsonObject.add("rating", ratingObject)
            }

            return jsonObject
        }

        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): Products {
            return try {
                val jsonObject = json.asJsonObject

                val id = getIntSafely(jsonObject, "id", 0)
                val name = getStringSafely(jsonObject, "name", "Unknown Product")
                val price = getDoubleSafely(jsonObject, "price", 0.0)
                val imageUrl = getStringSafely(jsonObject, "imageUrl", "")
                val category = getStringSafelyOrNull(jsonObject, "category")
                val description = getStringSafelyOrNull(jsonObject, "description")
                val imageRes = getIntSafely(jsonObject, "imageRes", 0)

                val rating = if (jsonObject.has("rating") && jsonObject.get("rating").isJsonObject) {
                    try {
                        val ratingObj = jsonObject.getAsJsonObject("rating")
                        val rate = getDoubleSafely(ratingObj, "rate", 0.0)
                        val count = getIntSafely(ratingObj, "count", 0)
                        Rating(rate = rate, count = count)
                    } catch (e: Exception) {
                        Log.d(TAG, "Error parsing rating object, using null", e)
                        null
                    }
                } else {
                    null
                }

                Products(
                    id = id,
                    name = name,
                    price = price,
                    imageUrl = imageUrl,
                    category = category,
                    description = description,
                    imageRes = imageRes,
                    rating = rating
                )

            } catch (e: Exception) {
                Log.d(TAG, "Error deserializing Product, returning fallback", e)
                Products(
                    id = 0,
                    name = "Error loading product",
                    price = 0.0,
                    imageUrl = "",
                    category = null,
                    description = "There was an error loading this product",
                    imageRes = 0,
                    rating = null
                )
            }
        }

        private fun getStringSafely(jsonObject: JsonObject, key: String, defaultValue: String): String {
            return try {
                if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull) {
                    jsonObject.get(key).asString
                } else {
                    defaultValue
                }
            } catch (e: Exception) {
                defaultValue
            }
        }

        private fun getStringSafelyOrNull(jsonObject: JsonObject, key: String): String? {
            return try {
                if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull) {
                    jsonObject.get(key).asString
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }

        private fun getIntSafely(jsonObject: JsonObject, key: String, defaultValue: Int): Int {
            return try {
                if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull) {
                    jsonObject.get(key).asInt
                } else {
                    defaultValue
                }
            } catch (e: Exception) {
                defaultValue
            }
        }

        private fun getDoubleSafely(jsonObject: JsonObject, key: String, defaultValue: Double): Double {
            return try {
                if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull) {
                    jsonObject.get(key).asDouble
                } else {
                    defaultValue
                }
            } catch (e: Exception) {
                defaultValue
            }
        }

        private fun getFloatSafely(jsonObject: JsonObject, key: String, defaultValue: Float): Float {
            return try {
                if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull) {
                    jsonObject.get(key).asFloat
                } else {
                    defaultValue
                }
            } catch (e: Exception) {
                defaultValue
            }
        }
    }
}
