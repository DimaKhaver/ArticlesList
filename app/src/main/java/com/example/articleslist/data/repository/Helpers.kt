package com.example.articleslist.data.repository

import com.example.articleslist.data.local.db.Article
import com.example.articleslist.data.local.db.ArticlesListDao
import io.reactivex.Flowable
import io.reactivex.Observable


enum class Status {
    SUCCESS, ERROR, LOADING
}

data class Resource<out T> (val status: Status, val data: T?, val message: String?) {

    companion object {
        fun <T> success(data: T?): Resource<T> = Resource(Status.SUCCESS, data, null)
        fun <T> error(msg: String, data: T?): Resource<T> = Resource(Status.ERROR, data, msg)
        fun <T> loading(data: T?): Resource<T> = Resource(Status.LOADING, data, null)
    }

    fun isRscLoaded(): Boolean {
        return (status != Status.LOADING) and (status != Status.ERROR)
    }
}

interface Repository<A, D> {
    fun fetchArticles(id: Long, page: Long = 0L, perPage: Long = 10L): Observable<A>
    fun fetchItemDetails(id: String, primaryKey: String): Flowable<D>

    fun setUpDBPaging(id: Long, page: Long, dao: ArticlesListDao): List<Article> {
        val all = dao.getArticlesCountByCategory(id = id)
        val limit = if (page == 0L) 10L else (page + 1) * 10
        val diff = all - limit
        val offset = if (diff > 0) diff else 0
        return dao.getArticlesByCategory(id = id, limit = limit, offset = offset)
    }
}