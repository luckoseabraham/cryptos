package edu.utexas.cryptos.api

import com.google.gson.GsonBuilder
import edu.utexas.cryptos.model.Asset
import edu.utexas.cryptos.model.Assets
import edu.utexas.cryptos.model.DetailAsset
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {

    @GET("/api/assets/{asset}")
    suspend fun getAssetDetails(@Path("asset") id: String): DetailAsset

    @GET("/api/assets?size=50")
    suspend fun getAssetList(): Assets


    companion object {
        // Tell Gson to use our SpannableString deserializer
        private fun buildGsonConverterFactory(): GsonConverterFactory {
            val gsonBuilder = GsonBuilder()
            return GsonConverterFactory.create(gsonBuilder.create())
        }

        // Keep the base URL simple
        var httpurl = HttpUrl.Builder()
            .scheme("https")
            .host("cryptingup.com")
            .build()

        fun create(): Api = create(httpurl)
        private fun create(httpUrl: HttpUrl): Api {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    // Enable basic HTTP logging to help with debugging.
                    this.level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(buildGsonConverterFactory())
                .build()
                .create(Api::class.java)
        }
    }

}