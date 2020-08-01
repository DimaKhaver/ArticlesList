package com.example.articleslist.presentation.uiflow.helpers

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.articleslist.R
import com.example.articleslist.data.local.db.Article
import com.example.articleslist.databinding.CategoriesItemBinding


final class DataBoundViewHolder<out T : ViewDataBinding> constructor(val binding: T):
    RecyclerView.ViewHolder(binding.root)

abstract class DataBoundAdapter<T, V : ViewDataBinding>(
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, DataBoundViewHolder<V>>(diffCallback) {

    protected val set by lazy {
        ConstraintSet()
    }

    protected val requestOptions by lazy {
        RequestOptions().placeholder(R.drawable.placeholder)
    }
}


class CategoriesAdapter(
    private val dataBindingComponent: DataBindingComponent,
    private val onClickCallback: ((Article) -> Unit)?
) : DataBoundAdapter<Article, CategoriesItemBinding>(
    diffCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article)
                = oldItem.dateCreated == newItem.dateCreated

        override fun areContentsTheSame(oldItem: Article, newItem: Article)
                = oldItem.dateCustom == newItem.dateCustom
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<CategoriesItemBinding> {
        val binding = createBinding(parent)
        return DataBoundViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataBoundViewHolder<CategoriesItemBinding>, position: Int) {
        bind(holder, getItem(position))
        setUpGlideOptions(holder)
    }

    private fun bind(holder: DataBoundViewHolder<CategoriesItemBinding>, dbArticle: Article) {
        holder.binding.article = dbArticle
        holder.binding.executePendingBindings()
    }

    private fun setUpGlideOptions(holder: DataBoundViewHolder<CategoriesItemBinding>) {

        with (holder.binding) {
            Glide.with(root)
                .setDefaultRequestOptions(requestOptions)
                .load(article?.item?.picture)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .listener(object: RequestListener<Drawable> {
                        override fun onLoadFailed(
                                e: GlideException?, model: Any?,
                                target: Target<Drawable>?, isFirstResource: Boolean
                        ): Boolean {
                          //e?.logRootCauses()
                            Log.e("TAG", "Error loading image", e)
                            return true
                        }

                        override fun onResourceReady(
                                resource: Drawable?, model: Any?, target: Target<Drawable>?,
                                dataSource: DataSource?, isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }
                    })
                .into(previewImage)

            with(set) {
                val posterRatio = String.format("%s:%s", article?.pictureWidth, article?.pictureHeight)
                clone(cardConstraintLayout)
                setDimensionRatio(previewImage.id, posterRatio)
                applyTo(cardConstraintLayout)
            }
        }
    }

    private fun createBinding(parent: ViewGroup): CategoriesItemBinding {
        val binding = DataBindingUtil.inflate<CategoriesItemBinding>(
            LayoutInflater.from(parent.context), R.layout.categories_item,
            parent, false, dataBindingComponent
        )

        binding.root.setOnClickListener {
            onClickCallback?.invoke(binding.article!!)
        }

        return binding
    }

}