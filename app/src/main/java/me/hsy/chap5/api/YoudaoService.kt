package me.hsy.chap5.api


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface YoudaoService {
    @GET("api")
    fun getMovieInfo(@Query("id") id: Int): Call<YoudaoBean>
}