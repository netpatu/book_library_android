package com.example.publibrary.net.test

import android.util.Log
import com.example.publibrary.utils.FileUtil
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody

class MockDataInterceptor : Interceptor {

    companion object {
        const val TAG = "MockDataInterceptor"
        const val mockIsOpen = false
        val pathMap = mapOf("books" to "books.json")
    }

    var path = ""

    override fun intercept(chain: Interceptor.Chain): Response? {
        val request = chain.request()
        val response = chain.proceed(request)

        val requestHeaders = chain.request().headers()

        if (shouldInterceptRequest(request) && mockIsOpen) {
            Log.d(TAG, "mock")
            Log.d(TAG, "headers: $requestHeaders")

            val builder = response.newBuilder()

            val ogriginalBody = response.body().toString()
            Log.d(TAG, "原始的 responseBody$ogriginalBody")

            val responseBody: ResponseBody = ResponseBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                getMockData()
            )
            Log.d(TAG, "changed responseBody${responseBody.source()}")

            builder.body(responseBody)

            val response = builder.build()
            return response
        }
        Log.d(TAG, "no mock")
        return response
    }

    fun getMockData(): String? {
        for (key in pathMap.keys) {
            if (path.contains(key)) {
                val str = FileUtil.getFromAssets(pathMap.get(key))
                return str
            }
        }

        return ""
    }

    fun shouldInterceptRequest(request: Request): Boolean {
        path = request.url().url().path
        for (key in pathMap.keys) {
            if (path.contains(key)) {
                Log.d(TAG, "path:" + path)
                return true
            }
        }

        return false;
    }
}