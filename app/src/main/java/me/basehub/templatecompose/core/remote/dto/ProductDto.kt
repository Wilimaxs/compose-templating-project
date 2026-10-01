package me.basehub.templatecompose.core.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Holds the fields displayed for one item in the POS product list. */
@Serializable
data class ProductSummaryDto(
    @SerialName("sku")
    val sku: String,
    @SerialName("image_path")
    val imagePath: String? = null,
    @SerialName("name")
    val name: String,
    @SerialName("category_name")
    val categoryName: String,
    @SerialName("is_active")
    val isActive: Boolean
)

/** Holds the additional fields returned by the POS product detail endpoint. */
@Serializable
data class ProductDetailDto(
    @SerialName("sku")
    val sku: String,
    @SerialName("category_code")
    val categoryCode: String,
    @SerialName("category_name")
    val categoryName: String,
    @SerialName("barcode")
    val barcode: String? = null,
    @SerialName("name")
    val name: String,
    @SerialName("unit")
    val unit: String,
    @SerialName("cost_price")
    val costPrice: Long,
    @SerialName("description")
    val description: String? = null,
    @SerialName("is_active")
    val isActive: Boolean,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("product_stocks")
    val productStocks: List<ProductStockDto> = emptyList()
)

/** Describes stock and selling price for a product at one store. */
@Serializable
data class ProductStockDto(
    @SerialName("store_code")
    val storeCode: String,
    @SerialName("stock_minimum")
    val stockMinimum: Int,
    @SerialName("stock_quantity")
    val stockQuantity: Int,
    @SerialName("selling_price")
    val sellingPrice: Long,
    @SerialName("store")
    val store: ProductStoreDto
)

/** Holds the store details nested inside a product stock entry. */
@Serializable
data class ProductStoreDto(
    @SerialName("name")
    val name: String,
    @SerialName("phone")
    val phone: String,
    @SerialName("is_active")
    val isActive: Boolean
)
