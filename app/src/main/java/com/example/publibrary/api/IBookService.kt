package com.example.publibrary.api

import com.example.publibrary.data.DataBook
import com.example.publibrary.data.DataMsg
import com.ipocket.wallet.api.Respbody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface IBookService {

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/deletebook")
    fun deleteBook(
        @Body dataBook: DataBook
    ): Call<Respbody<DataMsg>>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/updatebook")
    fun updateBook(
        @Body dataBook: DataBook
    ): Call<Respbody<DataMsg>>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/addbook")
    fun addbook(
        @Body dataBook: DataBook
    ): Call<Respbody<DataMsg>>

    @GET("/books")
    fun books(): Call<Respbody<List<DataBook>>>

    @GET("/books/{bookID}")
    fun books(@Path("bookID") bookID: Int): Call<Respbody<DataBook>>
}
