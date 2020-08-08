package com.example.articleslist.presentation.uiflow.categories.list

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.MarginLayoutParams
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.viewpager2.widget.ViewPager2
import com.example.articleslist.R
import com.example.articleslist.data.remote.network.ConnectivityStateHolder
import com.example.articleslist.data.repository.RepositoryImpl
import com.example.articleslist.databinding.FragmentArticlesListBinding
import com.example.articleslist.presentation.helpers.applyInsets
import com.example.articleslist.presentation.helpers.autoCleared
import com.example.articleslist.presentation.main.MainActivity
import com.example.articleslist.presentation.uiflow.helpers.CATEGORIES_FRAGMENTS_COUNT
import com.example.articleslist.presentation.uiflow.helpers.CategoriesCollectionAdapter
import com.example.articleslist.utils.displaySnackbar
import com.example.articleslist.utils.withColor
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_articles_list.*


class CategoriesSwipeFragment: Fragment() {

    companion object {
        private const val PREV_STATE_TAG = "PREV_STATE_TAG"
    }

    private var previousState = true
    var navBarSize: Int = 0
    private var binding by autoCleared<FragmentArticlesListBinding>()
    private lateinit var tabLayoutItem: LinearLayout

    private val sharedViewModel: CategoriesSharedViewModel by navGraphViewModels(R.id.main_navigation_graph)

    @SuppressLint("UseRequireInsteadOfGet")
    private val customTabLayout: (item: LinearLayout?, position: Int, color: Int) -> Unit
            = { item, position, color ->
        item?.getChildAt(position)?.setBackgroundColor(ContextCompat.getColor(context!!, color))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedViewModel.getArticles(category = 1)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (!ConnectivityStateHolder.isConnected && previousState)
            displaySnackbar(view = root, message = "Internet is OFF!").withColor().show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(PREV_STATE_TAG, previousState)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentArticlesListBinding.inflate(inflater, container, false)
        with(binding) {

            lifecycleOwner = this@CategoriesSwipeFragment
            sharedViewModel = this@CategoriesSwipeFragment.sharedViewModel

            toolbar.title = ""
            toolbar.subtitle = ""

            setHasOptionsMenu(true)

            val appBarConfiguration = AppBarConfiguration(setOf(R.id.action_coins))
            val navHostFragment = NavHostFragment.findNavController(this@CategoriesSwipeFragment)
            NavigationUI.setupWithNavController(toolbar, navHostFragment,appBarConfiguration)

            (activity as MainActivity).setSupportActionBar(toolbar)
            (activity as MainActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)
            (activity as MainActivity).supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
        }
        return binding.root
    }

    @SuppressLint("WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        applyInsets { statusBarSize, navBarSize ->
            this@CategoriesSwipeFragment.navBarSize = navBarSize
            toolbar.setPadding(0, statusBarSize, 0, 0)
        }

        viewPager2.adapter = CategoriesCollectionAdapter(this@CategoriesSwipeFragment)
        viewPager2.isUserInputEnabled = false
        viewPager2.offscreenPageLimit = CATEGORIES_FRAGMENTS_COUNT
        setUpSwipeViews(tabLayout, viewPager2)

        toolbar.setNavigationOnClickListener {
            displaySnackbar(view = root, message = "Not Implemented yet").withColor().show()
        }

        sharedViewModel.networkLiveData.observe(viewLifecycleOwner, Observer {
            if (ConnectivityStateHolder.isConnected) {
                sharedViewModel.getArticles(category = tabLayout.selectedTabPosition + 1L)
            }

            previousState = ConnectivityStateHolder.isConnected
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setUpSwipeViews(tabLayout: TabLayout, viewPager2: ViewPager2) {
        tabLayoutItem = tabLayout[0] as LinearLayout

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = resources.getText(R.string.schrang_tv)
                1 -> tab.text = resources.getText(R.string.talk)
                2 -> tab.text = resources.getText(R.string.spirit)
            }
        }.attach()

        (tabLayout.getChildAt(0) as ViewGroup).let {
            for (i in 0 until tabLayout.tabCount - 1) {
                val params = it.getChildAt(i).layoutParams as MarginLayoutParams
                params.setMargins(0, 0, 30, 0)
                it.requestLayout()
            }
            setUpTabLayoutItemsOptions()
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.position.run {
                    setUpTabLayoutItemsOptions(this)
                    sharedViewModel.getArticles((this + 1).toLong())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                setUpTabLayoutItemsOptions(position = -1)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                setUpTabLayoutItemsOptions(position = tab.position)
            }
        })

    }

    private fun setUpTabLayoutItemsOptions(position: Int = 0) {
        when (position) {
            0 -> {
                customTabLayout(tabLayoutItem, position, R.color.colorTabsBlue)
                customTabLayout(tabLayoutItem, position+1, R.color.colorTabsGrey)
                customTabLayout(tabLayoutItem, position+2, R.color.colorTabsGrey)
            }
            1 -> {
                customTabLayout(tabLayoutItem, position, R.color.colorTabsGreen)
                customTabLayout(tabLayoutItem, position+1, R.color.colorTabsGrey)
                customTabLayout(tabLayoutItem, position-1, R.color.colorTabsGrey)
            }
            2 -> {
                customTabLayout(tabLayoutItem, position, R.color.colorTabsOrange)
                customTabLayout(tabLayoutItem, position-1, R.color.colorTabsGrey)
                customTabLayout(tabLayoutItem, position-2, R.color.colorTabsGrey)
            }
            else -> customTabLayout(tabLayoutItem, position, R.color.colorTabsGrey)
        }
    }

}
