package me.basehub.templatecompose.core.remote.api

import me.basehub.templatecompose.core.remote.dto.ApiResponseDto
import me.basehub.templatecompose.core.remote.dto.LoginDataDto
import me.basehub.templatecompose.core.remote.dto.LoginRequestDto
import me.basehub.templatecompose.core.remote.dto.ProductDetailDto
import me.basehub.templatecompose.core.remote.dto.ProductSummaryDto
import me.basehub.templatecompose.core.remote.network.NoAuth
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/** Describes the POS endpoints available to repositories through Retrofit. */
// TODO(template): Replace these paths and response types when using another backend.
interface ApiService {

    /** Sends a phone number and password without a previously saved access token. */
    @NoAuth
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): Response<ApiResponseDto<LoginDataDto>>

    /** Loads one page of products; the backend decides the page size. */
    // TODO(template): Match the page query name to your backend's pagination contract.
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
