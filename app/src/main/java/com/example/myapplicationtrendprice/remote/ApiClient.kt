package com.example.myapplicationtrendprice.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object ApiClient {

    const val GATEWAY_URL = "https://37.99.82.5:8762/"

    // Keep this true only while the university/demo server uses an IP address or a self-signed certificate.
    // For a production release, set it to false and use a valid HTTPS certificate.
    private const val USE_DEMO_SSL_MODE = true

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(GATEWAY_URL)
            .client(createHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val catalogApi: CatalogApi by lazy { retrofit.create(CatalogApi::class.java) }
    val productApi: ProductApi by lazy { retrofit.create(ProductApi::class.java) }
    val storeApi: StoreApi by lazy { retrofit.create(StoreApi::class.java) }
    val priceApi: PriceApi by lazy { retrofit.create(PriceApi::class.java) }
    val imageApi: ImageApi by lazy { retrofit.create(ImageApi::class.java) }

    fun productImageUrl(productId: String): String {
        return "${GATEWAY_URL}image-service/api/images/product/$productId"
    }

    private fun createHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)

        if (USE_DEMO_SSL_MODE) {
            builder.enableDemoSslMode()
        }

        return builder.build()
    }

    private fun OkHttpClient.Builder.enableDemoSslMode(): OkHttpClient.Builder {
        val trustManager = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>?, authType: String?) = Unit
            override fun checkServerTrusted(chain: Array<X509Certificate>?, authType: String?) = Unit
            override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
        }

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), SecureRandom())

        return sslSocketFactory(sslContext.socketFactory, trustManager)
            .hostnameVerifier { _, _ -> true }
    }
}