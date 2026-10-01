package me.basehub.templatecompose.core.remote.api

import me.basehub.templatecompose.core.remote.dto.ApiResponseDto
import me.basehub.templatecompose.core.remote.dto.LoginDataDto
import me.basehub.templatecompose.core.remote.dto.ProductDetailDto
import me.basehub.templatecompose.core.remote.dto.ProductSummaryDto
import me.basehub.templatecompose.core.remote.network.NoAuth
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/** Describes the POS endpoints available to repositories through Retrofit. */
// TODO(template): Replace these paths and response types when using another backend.
interface ApiService {

    /** Sends login credentials as form fields without a previously saved access token. */
    @NoAuth
    @FormUrlEncoded
    @Headers("Accept: application/json")
    @POST("auth/login")
    suspend fun login(
        @Field("phone") phone: String,
        @Field("password") password: String
    ): Response<ApiResponseDto<LoginDataDto>>

    /** Loads one page of products; the backend decides the page size. */
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int
    ): Response<ApiResponseDto<List<ProductSummaryDto>>>

    /** Loads a product and its per-store stock information by SKU. */
    @GET("products/{sku}")
    suspend fun getProductDetail(
        @Path("sku") sku: String
    ): Response<ApiResponseDto<ProductDetailDto>>
}
