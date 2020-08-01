package com.example.articleslist.presentation.uiflow.categories.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.articleslist.data.local.db.Article
import com.example.articleslist.data.repository.Repository
import com.example.articleslist.data.repository.Resource
import com.example.articleslist.presentation.uiflow.categories.NetworkAccessViewModel
import com.example.articleslist.presentation.uiflow.helpers.CATEGORIES_FRAGMENTS_COUNT
import com.example.articleslist.presentation.uiflow.helpers.SwipeCategory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject


class CategoriesSharedViewModel constructor(): NetworkAccessViewModel(), KoinComponent {

    private val repo: Repository<Resource<List<Article>>, Article.ItemDetails?> by inject()


    var lastRemotePage: Long = 0
    lateinit var currentCategory: SwipeCategory

    val articlesLiveData: LiveData<MutableList<Article>>
        get() = _articlesLiveData

    private val _articlesLiveData = MutableLiveData<MutableList<Article>>()

    val networkLiveData: LiveData<Boolean> = connectivityLiveData


    fun getArticles(category: Long, currentPage: Long = 0L) {
        if (category in 1..CATEGORIES_FRAGMENTS_COUNT) {
            currentCategory = SwipeCategory.create(category)
            repo.fetchArticles(id = currentCategory.value, page = currentPage)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribeBy(
                    onNext = ::observeRepository,
                    onError = { it.message },
                    onComplete = {}
                )
        }
    }

    private fun observeRepository(rsc: Resource<List<Article>>) {
        if (rsc.isRscLoaded()) {
            with(rsc.data!!) {
                if (this.isNotEmpty()) {
                    lastRemotePage = this[0].pages!!.last!!
                    _articlesLiveData.postValue(this.toMutableList())
                }
            }
        }
    }

}