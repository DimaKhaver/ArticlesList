package com.example.articleslist.presentation.uiflow.helpers

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import java.lang.IllegalArgumentException


abstract class Paginator(private val recyclerView: RecyclerView): RecyclerView.OnScrollListener() {

    private val batchSize = 19L
    var currentPage: Long = 0L
    private val threshold = 3
    private var endWithAuto = false
    private val layoutManager: RecyclerView.LayoutManager?

    private val startSize: Long
        get() = ++currentPage

    private val maxSize: Long
        get() = currentPage + batchSize

    init {
        recyclerView.addOnScrollListener(this)
        this.layoutManager = recyclerView.layoutManager
    }

    abstract val isLoading: Boolean
    abstract val isLastPage: Boolean
    abstract fun loadMore(start: Long, count: Long)

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (newState == SCROLL_STATE_IDLE) {
            val visibleItemCount = layoutManager!!.childCount
            val totalItemCount = layoutManager.itemCount

            val firstVisibleItemPosition: Int = when (layoutManager) {
                is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
                is GridLayoutManager -> layoutManager.findLastVisibleItemPosition()
                else -> throw IllegalArgumentException()
            }

            if (isLoading or isLastPage)
                return

            if (visibleItemCount + firstVisibleItemPosition + threshold >= totalItemCount) {
                if (!endWithAuto) {
                    endWithAuto = true
                    loadMore(startSize, maxSize)
                }
            } else
                endWithAuto = false
        }
    }

    fun reset() {
        currentPage = 0L
    }
}