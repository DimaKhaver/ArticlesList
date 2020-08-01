package com.example.articleslist.presentation.uiflow.categories.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.articleslist.data.local.db.Article
import com.example.articleslist.data.repository.Repository
import com.example.articleslist.data.repository.Resource
import com.example.articleslist.presentation.uiflow.categories.NetworkAccessViewModel
import io.reactivex.rxkotlin.subscribeBy
import org.koin.core.KoinComponent
import org.koin.core.inject


class DetailsViewModel: NetworkAccessViewModel(), KoinComponent {

    private val repo: Repository<Resource<List<Article>>,
                      Article.ItemDetails?> by inject()

    val articlesLiveData: LiveData<Article.ItemDetails>
        get() = _articlesLiveData

    private val _articlesLiveData = MutableLiveData<Article.ItemDetails>()

    fun getItemDetails(id: String, primaryKey: String) {
        repo.fetchItemDetails(id = id, primaryKey = primaryKey)
            .toObservable()
            .map { data -> data }
            .subscribeBy(
                onNext = ::observeRepository,
                onError = { it.message },
                onComplete = {}
            )
    }

    private fun observeRepository(details: Article.ItemDetails) = _articlesLiveData.postValue(details)
}