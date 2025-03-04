package ru.myitschool.nasa_bootcamp.ui.home.social_media

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import ru.myitschool.nasa_bootcamp.data.model.*
import ru.myitschool.nasa_bootcamp.utils.Resource

interface SocialMediaViewModel {
    fun getBlogs(): LiveData<Resource<List<LiveData<ContentWithLikesAndComments<PostModel>>>>>
    fun getNews(): LiveData<Resource<List<LiveData<ContentWithLikesAndComments<ArticleModel>>>>>
    suspend fun loadBlogs()
    suspend fun loadNews()
    suspend fun loadCurrentUser()
    fun getViewModelScope(): CoroutineScope
    fun setSelectedPost(post: LiveData<ContentWithLikesAndComments<PostModel>>)
    fun getSelectedPost(): LiveData<ContentWithLikesAndComments<PostModel>>?
    fun setSelectedArticle(article: LiveData<ContentWithLikesAndComments<ArticleModel>>)
    fun getSelectedArticle(): LiveData<ContentWithLikesAndComments<ArticleModel>>?
    suspend fun pressedLikeOnComment(
        item: ContentWithLikesAndComments<out Any>,
        comment: Comment
    ): Resource<Nothing>

    suspend fun pressedLikeOnItem(item: ContentWithLikesAndComments<out Any>): Resource<Nothing>
    suspend fun sendMessage(
        message: String,
        id: Long,
        _class: Class<*>,
        parentComment: Comment? = null
    ): Resource<Nothing>

    fun getCurrentUser(): LiveData<UserModel?>
    suspend fun createPost(title: String, postItems: List<Any>): Resource<Nothing>
    suspend fun deleteComment(
        comment: Comment,
        item: ContentWithLikesAndComments<out Any>
    ): Resource<Nothing>
//    suspend fun getCommentsByItemId(id: Long, _class: Class<*>): List<LiveData<Comment>>
//    suspend fun getLikesByItemId(id: Long, _class: Class<*>): List<UserModel>
//    suspend fun getLikesByCommentId(id: Long): List<UserModel>
//    suspend fun getCommentById(id: Long): LiveData<Comment>
}