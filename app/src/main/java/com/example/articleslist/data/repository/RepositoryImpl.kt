package com.example.articleslist.data.repository

import android.annotation.SuppressLint
import android.util.Log
import com.example.articleslist.data.local.db.Article
import com.example.articleslist.data.local.db.ArticlesListDao
import com.example.articleslist.data.local.db.toDBListArticle
import com.example.articleslist.data.local.db.toDBPages
import com.example.articleslist.data.remote.network.ConnectivityStateHolder
import com.example.articleslist.data.remote.response.ArticlesResponse
import com.example.articleslist.data.remote.retrofit.ServerApi
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject


class RepositoryImpl: Repository<Resource<List<Article>>, Article.ItemDetails?>,
        KoinComponent  {

    private val articlesListDao: ArticlesListDao by inject()
    private val serverApi: ServerApi by inject()
    private var isDBEmpty: Boolean = true

    override fun fetchItemDetails(id: String, primaryKey: String): Flowable<Article.ItemDetails?> {
        return Flowable.fromCallable { articlesListDao.getItemDetails(id = id, primaryKey = primaryKey) }
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
    }

    override fun fetchArticles(id: Long, page: Long, perPage: Long): Observable<Resource<List<Article>>> {

        return object: NetworkBoundRsc<List<Article>, ArticlesResponse>() {

            override fun saveCallResult(item: ArticlesResponse?) {
                val dbArticles: List<Article> = item?.articles?.toDBListArticle(item.pages?.toDBPages())!!
                articlesListDao.insertArticles(dbArticles)
            }

            @SuppressLint("CheckResult")
            override fun loadFromDb(): Flowable<List<Article>> = Flowable.just(true)
                .subscribeOn(Schedulers.io())
                .map { setUpDBPaging(id = id, page = page, dao = articlesListDao) }
                .doOnNext { dbList -> isDBEmpty = dbList.isEmpty() }

            override fun shouldFetch(): Boolean = isDBEmpty

            override fun createCall(): Observable<Resource<ArticlesResponse>> {
                return serverApi.getListOfArticles(category = id,
                                                   page = page,
                                                   per_page = perPage)
                    .observeOn(Schedulers.io())
                    .concatMap { response ->
                        Observable.just(
                            if (response.isSuccessful)
                                Resource.success(response.body())
                            else
                                Resource.error("", ArticlesResponse()) )
                    }
            }

            override fun onFetchFailed(message: String?) { Log.e("onFetchFailed", message) }

            override fun isNetworkAccessible(): Boolean = ConnectivityStateHolder.isConnected
        }.asObservable
    }

}
