package com.example.publibrary.net

import android.util.Log
import com.example.publibrary.api.IBookService
import com.example.publibrary.net.test.MockDataInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.concurrent.TimeUnit


class BookApiImplementation() {

    var url = "http://192.168.1.17:8081/"

    private var service: IBookService? = null

    private var interceptor = HttpLoggingInterceptor { message ->
        try {
            val text = URLDecoder.decode(message, "utf-8")
            Log.e("OKHttp-----", text)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            Log.e("OKHttp-----", message)
        }
    }

    init {
        service = createService(url)
    }

    private fun createService(url: String): IBookService {
        interceptor.level = HttpLoggingInterceptor.Level.BODY;
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(MockDataInterceptor())
            .addInterceptor(interceptor)
            .retryOnConnectionFailure(false)
            .build()

        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(IBookService::class.java)
    }

    fun reConstructService() {
        service = createService(url)
    }

    fun getBookService(): IBookService? {
        return service
    }
}