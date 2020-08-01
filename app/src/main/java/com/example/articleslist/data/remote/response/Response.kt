package com.example.articleslist.data.remote.response


import androidx.annotation.NonNull
import com.google.gson.annotations.SerializedName


data class ArticlesResponse(@SerializedName("articles")
                                  val articles: List<Article?>?,
                            @SerializedName("pages")
                                  val pages: Pages?) {

    constructor(): this(null, null)

    data class Article(
        @SerializedName("category")
        val category: Category?,
        @SerializedName("content")
        val content: String?,
        @SerializedName("date_created")
        val dateCreated: String?,
        @SerializedName("date_custom")
        val dateCustom: String?,
        @SerializedName("hit_counter")
        val hitCounter: String?,
        @SerializedName("hit_rating")
        val hitRating: String?,
        @SerializedName("id")
        @NonNull
        val id: String?,
        @SerializedName("picture")
        val picture: String?,
        @SerializedName("picture_collection")
        val pictureCollection: String?,
        @SerializedName("picture_height")
        val pictureHeight: String?,
        @SerializedName("picture_mime")
        val pictureMime: String?,
        @SerializedName("picture_width")
        val pictureWidth: String?,
        @SerializedName("podcast")
        val podcast: Any?,
        @SerializedName("podcast_duration")
        val podcastDuration: String?,
        @SerializedName("podcast_link")
        val podcastLink: Any?,
        @SerializedName("podcast_mime")
        val podcastMime: Any?,
        @SerializedName("recommended")
        val recommended: String?,
        @SerializedName("second_title")
        val secondTitle: Any?,
        @SerializedName("slug")
        val slug: String?,
        @SerializedName("source")
        val source: Any?,
        @SerializedName("status")
        val status: String?,
        @SerializedName("tags")
        val tags: List<Tag?>?,
        @SerializedName("teaser")
        val teaser: String?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("video_bitchute")
        val videoBitchute: Any?,
        @SerializedName("video_duration")
        val videoDuration: String?,
        @SerializedName("video_other")
        val videoOther: Any?,
        @SerializedName("video_vimeo")
        val videoVimeo: Any?,
        @SerializedName("video_youtube")
        val videoYoutube: String?
    ) {
        data class Category(
            @SerializedName("id")
            val id: String?,
            @SerializedName("picture")
            val picture: String?,
            @SerializedName("title")
            val title: String?)

        data class Tag(
            @SerializedName("slug")
            val slug: String?,
            @SerializedName("tag")
            val tag: String?)
    }

    data class Pages(
        @SerializedName("articles")
        val articles: Long?,
        @SerializedName("count")
        val count: Long?,
        @SerializedName("first")
        val first: Long?,
        @SerializedName("last")
        val last: Long?
    )
}