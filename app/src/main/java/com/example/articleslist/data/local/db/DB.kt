package com.example.articleslist.data.local.db

import android.content.Context
import androidx.annotation.NonNull
import androidx.room.*


@Dao
interface ArticlesListDao {

    @Query("SELECT * FROM articles WHERE category_id = :id LIMIT :limit OFFSET :offset")
    fun getArticlesByCategory(id: Long, limit: Long, offset: Long): List<Article>

    @Query("SELECT Count(*) FROM articles")
    fun getAllArticlesCount(): Int

    @Query("SELECT * FROM articles")
    fun getAllArticles(): List<Article>

    @Query("SELECT Count(*) FROM articles WHERE category_id = :id ")
    fun getArticlesCountByCategory(id: Long): Int

    @Query("SELECT item FROM articles WHERE category_id = :id AND id = :primaryKey")
    fun getItemDetails(id: String, primaryKey: String): Article.ItemDetails?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticles(articles: List<Article>)
}

@Entity(tableName = "articles")
data class Article constructor(

    @PrimaryKey(autoGenerate = false)
    @NonNull
    val id: String,

    @Embedded(prefix = "category_")
    val category: Category? = null,

    @Embedded(prefix = "pages_")
    val pages: Pages? = null,

    @ColumnInfo
    val item: ItemDetails? = null,

    val dateCreated: String? = "",
    val dateCustom: String? = "",
    val hitCounter: String? = "",
    val hitRating: String? = "",
    val articleId: String? = "",
    val pictureCollection: String? = "",
    val pictureHeight: String? = "",
    val pictureMime: String? = "",
    val pictureWidth: String? = "",
    val podcast: String?  = null,
    val podcastDuration: String?  = "",
    val recommended: String?  = "",
    val secondTitle: String? = null,
    val slug: String? = "",
    val teaser: String? = "",
    val videoBitchute: String?  = null,
    val videoDuration: String? = "",
    val videoOther: String? = null,
    val videoVimeo: String? = null,
    val videoYoutube: String? = ""
) {

    data class ItemDetails constructor(
        val status: String? = "",
        val picture: String? = "",
        val title: String? = "",
        val content: String? = ""
    )

    data class Category constructor(
        val id: String,
        val picture: String? = "",
        val title: String? = ""
    )

    data class Pages constructor(
        val articles: Long? = 0,
        val count: Long? = 0,
        val first: Long? = 0,
        val last: Long? = 0
    )
}

@Database(entities = [Article::class], version = 1, exportSchema = true)
@TypeConverters(ArticlesTypeConverters::class)
abstract class ArticlesDB: RoomDatabase() {
    abstract val userDao: ArticlesListDao
}

private lateinit var INSTANCE: ArticlesDB

fun getArticlesDB(context: Context): ArticlesDB {
    synchronized(ArticlesDB::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                ArticlesDB::class.java, "users"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}
