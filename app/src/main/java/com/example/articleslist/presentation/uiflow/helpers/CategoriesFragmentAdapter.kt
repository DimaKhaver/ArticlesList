package com.example.articleslist.presentation.uiflow.helpers

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.articleslist.presentation.uiflow.categories.list.SpiritCategoryFragment
import com.example.articleslist.presentation.uiflow.categories.list.TalkCategoryFragment
import com.example.articleslist.presentation.uiflow.categories.list.TvCategoryFragment


const val CATEGORIES_FRAGMENTS_COUNT: Int = 3

enum class SwipeCategory(val value: Long) {
    TV(1L),
    TALK(2L),
    SPIRIT(3L);

    companion object {
        fun create(x: Long): SwipeCategory {
            return when (x) {
                1L -> TV
                2L -> TALK
                3L -> SPIRIT
                else -> throw IllegalStateException()
            }
        }
    }
}

class CategoriesCollectionAdapter(private val fragment: Fragment): FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = CATEGORIES_FRAGMENTS_COUNT

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TvCategoryFragment()
            1 -> TalkCategoryFragment()
            else -> SpiritCategoryFragment()
        }
    }
}