package com.example.articleslist.presentation.uiflow.categories.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.articleslist.R
import com.example.articleslist.databinding.FragmentCategoriesBinding
import com.example.articleslist.presentation.helpers.FragmentDataBindingComponent
import com.example.articleslist.presentation.helpers.autoCleared
import com.example.articleslist.presentation.uiflow.helpers.CategoriesAdapter
import com.example.articleslist.presentation.uiflow.helpers.CustomItemDecoration
import com.example.articleslist.presentation.uiflow.helpers.Paginator
import com.example.articleslist.presentation.uiflow.helpers.SwipeCategory


abstract class CategoriesInitFragment: Fragment() {

    protected val sharedViewModel: CategoriesSharedViewModel by navGraphViewModels(R.id.main_navigation_graph)
    protected val dataBindingComponent by lazy { FragmentDataBindingComponent(this) }
    protected var binding by autoCleared<FragmentCategoriesBinding>()
    protected var categoriesAdapter by autoCleared<CategoriesAdapter>()
    protected lateinit var recyclerView: RecyclerView
    protected lateinit var paginator: Paginator

    protected val dataBinding: (inflater: LayoutInflater,
                                container: ViewGroup?) -> FragmentCategoriesBinding = { inflater, container ->
        DataBindingUtil.inflate(
            inflater, R.layout.fragment_categories,
            container, true)
    }

    protected val setUpNavigation: () -> CategoriesAdapter = {
        CategoriesAdapter(dataBindingComponent) { article ->
            findNavController().navigate(
                CategoriesSwipeFragmentDirections.showArticleDetailFragment(
                    tableId = article.id, detailsId = article.category!!.id
                )
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        this.binding = dataBinding(inflater, container)
        recyclerView = binding.categoryList
        recyclerView.addItemDecoration(CustomItemDecoration())
        recyclerView.setHasFixedSize(true)
        return binding.root
    }

    override fun onPause() {
        paginator.reset()
        super.onPause()
    }
}

class TvCategoryFragment: CategoriesInitFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.progressLoading.visibility = View.VISIBLE
        val categoriesAdapter = setUpNavigation()
        this.categoriesAdapter = categoriesAdapter
        this.recyclerView.adapter = categoriesAdapter

        sharedViewModel.articlesLiveData.observe(viewLifecycleOwner, Observer { listTV ->
            if (sharedViewModel.currentCategory == SwipeCategory.TV) {
                categoriesAdapter.submitList(listTV)
                binding.progressLoading.visibility = View.GONE
            }
        })

        paginator = object : Paginator(recyclerView) {
            override val isLoading: Boolean
                get() = binding.progressLoading.isVisible

            override val isLastPage: Boolean
                get() {
                    val lastPageConfirm = (currentPage == sharedViewModel.lastRemotePage)
                    if (lastPageConfirm) binding.progressLoading.visibility = View.GONE
                    return lastPageConfirm
                }

            override fun loadMore(start: Long, count: Long) {
                binding.progressLoading.visibility = View.VISIBLE
                sharedViewModel.getArticles(
                    category = sharedViewModel.currentCategory.value,
                    currentPage = start
                )
            }
        }

        recyclerView.addOnScrollListener(paginator)
    }
}

class TalkCategoryFragment: CategoriesInitFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.progressLoading.visibility = View.VISIBLE
        val categoriesAdapter = setUpNavigation()
        this.categoriesAdapter = categoriesAdapter
        this.recyclerView.adapter = categoriesAdapter

        sharedViewModel.articlesLiveData.observe(viewLifecycleOwner, Observer { listTalk ->
            if (sharedViewModel.currentCategory == SwipeCategory.TALK) {
                categoriesAdapter.submitList(listTalk)
                binding.progressLoading.visibility = View.GONE
            }
        })

        paginator = object : Paginator(recyclerView) {
            override val isLoading: Boolean
                get() = binding.progressLoading.isVisible

            override val isLastPage: Boolean
                get() {
                    val lastPageConfirm = (currentPage == sharedViewModel.lastRemotePage)
                    if (lastPageConfirm) binding.progressLoading.visibility = View.GONE
                    return lastPageConfirm
                }

            override fun loadMore(start: Long, count: Long) {
                binding.progressLoading.visibility = View.VISIBLE
                sharedViewModel.getArticles(
                    category = sharedViewModel.currentCategory.value,
                    currentPage = start
                )
            }
        }

        recyclerView.addOnScrollListener(paginator)
    }
}


class SpiritCategoryFragment: CategoriesInitFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.progressLoading.visibility = View.VISIBLE
        val categoriesAdapter = setUpNavigation()
        this.categoriesAdapter = categoriesAdapter
        this.recyclerView.adapter = categoriesAdapter

        sharedViewModel.articlesLiveData.observe(viewLifecycleOwner, Observer { listSpirit ->
            if (sharedViewModel.currentCategory == SwipeCategory.SPIRIT) {
                categoriesAdapter.submitList(listSpirit)
                binding.progressLoading.visibility = View.GONE
            }
        })

        paginator = object : Paginator(recyclerView) {
            override val isLoading: Boolean
                get() = binding.progressLoading.isVisible

            override val isLastPage: Boolean
                get() {
                    val lastPageConfirm = (currentPage == sharedViewModel.lastRemotePage)
                    if (lastPageConfirm) binding.progressLoading.visibility = View.GONE
                    return lastPageConfirm
                }

            override fun loadMore(start: Long, count: Long) {
                binding.progressLoading.visibility = View.VISIBLE
                sharedViewModel.getArticles(
                    category = sharedViewModel.currentCategory.value,
                    currentPage = start
                )
            }
        }

        recyclerView.addOnScrollListener(paginator)
    }
}