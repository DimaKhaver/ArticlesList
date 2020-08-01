package com.example.articleslist.di

import android.content.Context
import com.example.articleslist.data.local.db.Article
import com.example.articleslist.data.local.db.getArticlesDB
import com.example.articleslist.data.remote.retrofit.ServerApi
import com.example.articleslist.data.repository.Repository
import com.example.articleslist.data.repository.RepositoryImpl
import com.example.articleslist.data.repository.Resource
import com.example.articleslist.di.DIHelper.provideDAO
import com.example.articleslist.di.DIHelper.provideHttpClient
import com.example.articleslist.di.DIHelper.provideInterceptor
import com.example.articleslist.di.DIHelper.provideRetrofit
import com.example.articleslist.di.DIHelper.provideServerApi
import com.example.articleslist.presentation.uiflow.categories.details.DetailsViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit


const val API_BASE_URL = "https://www.heikoschrang.de/"
const val READ_TIMEOUT = 60L
const val WRITE_TIMEOUT = 15L
const val CONNECT_TIMEOUT = 60L

val appGlobalModule = module {
    single { provideDAO(context = get()) }
    single { provideInterceptor() }
    single { provideHttpClient(interceptor = get()) }
    single { provideServerApi(retrofit = get()) }
    single { provideRetrofit(client = get()) }
    single<Repository<Resource<List<Article>>, Article.ItemDetails?>> { RepositoryImpl() }
    single { DetailsViewModel() }
}

object DIHelper {
    fun provideDAO(context: Context) = getArticlesDB(context = context).userDao

    fun provideServerApi(retrofit: Retrofit) = retrofit.create<ServerApi>()

    fun provideRetrofit(client: OkHttpClient): Retrofit = run {
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
    }

    fun provideHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .addNetworkInterceptor(interceptor)
            .build()
    }

    fun provideInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

}

