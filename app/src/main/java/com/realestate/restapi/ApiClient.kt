package com.realestate.restapi

import android.content.Context
import android.util.Log
import com.realestate.app.RealEstateApp
import com.realestate.storage.SharedPreferenceManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.jvm.Throws


/**
 * Created by Chandan on 26/12/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
internal class ApiClient {
    private var retrofit: ApiInterface? = null
    private var isHeaderRequired = false

    fun getClient(isHeaderRequired: Boolean, context: Context?): ApiInterface? {
        this.isHeaderRequired = isHeaderRequired
        /*if (retrofit == null)
            getClient(context)
        else
            return retrofit*/
        return getClient(context)
    }

    private fun getClient(context: Context?): ApiInterface? {

        /*val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient()
        val clientWith30sTimeout: OkHttpClient = okHttpClient.newBuilder()
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .build()


        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientWith30sTimeout)
            .build()
            .create()*/

        try {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
            client.readTimeout(60, TimeUnit.SECONDS)
            client.writeTimeout(60, TimeUnit.SECONDS)
            client.connectTimeout(60, TimeUnit.SECONDS)
            client.addInterceptor(interceptor)
            if (isHeaderRequired) {
                client.addInterceptor(object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val originalRequest = chain.request()
                        val auth = SharedPreferenceManager.getInstance(RealEstateApp.context)
                            ?.getString(SharedPreferenceManager.Key.AUTH_TOKEN)
                        if (auth != null) {
                            val builder = originalRequest.newBuilder().header(
                                "Authorization", auth
                            )

                            val newRequest = builder.build()
                            return chain.proceed(newRequest)
                        } else {
                            return chain.proceed(originalRequest)
                        }

                    }
                })
            } else {
                client.addInterceptor(object : Interceptor {
                    @Throws(IOException::class)
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val request: Request = chain.request()
                        return chain.proceed(request)
                    }
                })
            }
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterface::class.java)
        } catch (e: Exception) {
            Log.d("TAGG", "ApiClient ${e.message}")
        }


        return retrofit
    }

    companion object {
        private const val BASE_URL = "http://148.66.128.74:81/api/"
        private var apiClient: ApiClient? = null
        val instance: ApiClient?
            get() {
                if (apiClient == null) {
                    apiClient = ApiClient()
                }
                return apiClient
            }
    }
}