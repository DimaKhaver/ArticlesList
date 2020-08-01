package com.example.articleslist.data.local.db

import androidx.room.TypeConverter
import com.example.articleslist.data.remote.response.ArticlesResponse
import com.google.gson.Gson


object ArticlesTypeConverters {
    @TypeConverter
    @JvmStatic
    fun jsonToList(value: String?): Article.ItemDetails = Gson().fromJson(value, Article.ItemDetails::class.java)

    @TypeConverter
    @JvmStatic
    fun listToJson(value: Article.ItemDetails): String = Gson().toJson(value)
}

fun List<ArticlesResponse.Article?>.toDBListArticle(pages: Article.Pages?) = map {
    it!!.toDBArticle(pages, it.toItemDetails())
}

fun ArticlesResponse.Article.toDBArticle(pages: Article.Pages?, itemDetails: Article.ItemDetails) = Article(

    id = id!!,
    category = category?.toDBCategory(),
    pages = pages,
    item = itemDetails,
    dateCreated = dateCreated,
    dateCustom = dateCustom,
    hitCounter = hitCounter,
    hitRating = hitRating,
    articleId = id,
    pictureCollection = pictureCollection,
    pictureHeight = pictureHeight,
    pictureMime = pictureMime,
    pictureWidth = pictureWidth,
    podcast = podcast.toString(),
    podcastDuration = podcastDuration,
    recommended = recommended,
    secondTitle = secondTitle.toString(),
    slug = slug,
    teaser = teaser,
    videoBitchute = videoBitchute.toString(),
    videoDuration = videoDuration,
    videoOther = videoOther.toString(),
    videoVimeo = videoVimeo.toString(),
    videoYoutube = videoYoutube
)

fun ArticlesResponse.Pages.toDBPages() = Article.Pages(
    articles = articles,
    count = count,
    first = first,
    last = last
)

fun ArticlesResponse.Article.toItemDetails() = Article.ItemDetails (
    status = content,
    picture = picture,
    title = title,
    content = content
)

fun ArticlesResponse.Article.Category.toDBCategory() = Article.Category(
    id = id!!,
    picture = picture,
    title = title
)
