package com.example.articleslist.presentation.uiflow.categories.details

import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.articleslist.R
import com.example.articleslist.databinding.FragmentDetailsBinding
import com.example.articleslist.presentation.helpers.FragmentDataBindingComponent
import com.example.articleslist.presentation.helpers.applyInsets
import com.example.articleslist.presentation.helpers.autoCleared
import com.example.articleslist.presentation.uiflow.customviews.ElasticDragDismissFrameLayout
import org.koin.androidx.viewmodel.ext.android.viewModel


class CategoriesDetailsFragment: Fragment() {

    private var binding by autoCleared<FragmentDetailsBinding>()
    private val params by navArgs<CategoriesDetailsFragmentArgs>()
    private val dataBindingComponent by lazy { FragmentDataBindingComponent(this) }
    private val detailsViewModel by viewModel<DetailsViewModel>()

    private lateinit var draggableFrame: ElasticDragDismissFrameLayout
    private lateinit var chromeFader: ElasticDragDismissFrameLayout.SystemHelper

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_details,
            container, false, dataBindingComponent
        )
        binding.args = params
        draggableFrame = binding.draggableContainer

        chromeFader = ElasticDragDismissFrameLayout.SystemHelper(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            binding.content.justificationMode = JUSTIFICATION_MODE_INTER_WORD

        detailsViewModel.getItemDetails(id = params.detailsId, primaryKey = params.tableId)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        detailsViewModel.articlesLiveData.observe(viewLifecycleOwner, Observer {
            binding.details = it
        })

        applyInsets { statusBarSize, navBarSize ->
            with (binding) {
                arrow.let { it.setPadding(it.paddingStart, statusBarSize + it.paddingTop, it.paddingEnd, it.paddingBottom) }
                draggableContainer.let { it.setPadding(it.paddingStart, it.paddingTop, it.paddingEnd, navBarSize) }
                scrollView.let { it.setPadding(it.paddingStart, it.paddingTop + header.layoutParams.height, it.paddingEnd, it.paddingBottom) }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        draggableFrame.addListener(chromeFader)
    }

    override fun onPause() {
        draggableFrame.removeListener(chromeFader)
        super.onPause()
    }
}