package com.example.articleslist.data.repository

import android.annotation.SuppressLint
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers


abstract class NetworkBoundRsc<ResultType, RequestType>
    @MainThread protected constructor() {

    val asObservable: Observable<Resource<ResultType>>
        get() = result

    private var result: Observable<Resource<ResultType>>

    private val serverCall: () -> Observable<ResultType> = {
        createCall()
            .doOnNext { apiResponse -> saveCallResult(processResponse(apiResponse)) }
            .flatMap { loadFromDb().toObservable() }
    }


     init {
        result = loadFromDb().toObservable()
            .flatMap { dbData ->
                if (isNetworkAccessible() or shouldFetch()) {
                    serverCall()
                } else {
                    Observable.just(dbData)
                }
            }
            .map { data -> Resource.success(data) }
            .doOnError { error ->
                onFetchFailed(error.message)
                Resource.error( "db is empty", error.message)
            }
          //.onErrorResumeNext { t: Throwable? -> // TODO: check network errors
          //    Observable.create { serverCall() }
          //}
            .observeOn(Schedulers.io())
    }

    @WorkerThread
    fun processResponse(response: Resource<RequestType>): RequestType? {
        return response.data
    }

    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType?)

    @MainThread
    protected abstract fun shouldFetch(): Boolean

    @SuppressLint("CheckResult")
    @MainThread
    protected abstract fun loadFromDb(): Flowable<ResultType>

    @MainThread
    protected abstract fun createCall(): Observable<Resource<RequestType>>

    @MainThread
    protected open fun onFetchFailed(message: String?) {}

    @WorkerThread
    protected abstract fun isNetworkAccessible(): Boolean
}
