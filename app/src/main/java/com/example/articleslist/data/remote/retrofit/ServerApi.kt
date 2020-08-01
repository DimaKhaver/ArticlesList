package com.example.articleslist.data.remote.retrofit

import com.example.articleslist.data.remote.response.ArticlesResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ServerApi {

    @GET("applications/app/content/list")
    fun getListOfArticles(@Query("category") category: Long,
                          @Query("page") page: Long,
                          @Query("per_page") per_page: Long,
                          @Query("order") order: String = "desc"): Observable<Response<ArticlesResponse>>
}

