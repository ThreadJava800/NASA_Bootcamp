package ru.myitschool.nasa_bootcamp.data.repository

import androidx.lifecycle.LiveData
import ru.myitschool.nasa_bootcamp.data.model.*
import ru.myitschool.nasa_bootcamp.utils.Resource

interface SocialMediaRepository {
    suspend fun getNews(): Resource<List<LiveData<ContentWithLikesAndComments<ArticleModel>>>>
    suspend fun getBlogPosts(): Resource<List<LiveData<ContentWithLikesAndComments<PostModel>>>>
    suspend fun pressedLikeOnItem(item: ContentWithLikesAndComments<out Any>): Resource<Nothing>
    suspend fun getCurrentUser(): UserModel?
    suspend fun pressedLikeOnComment(
        item: ContentWithLikesAndComments<out Any>,
        comment: Comment
    ): Resource<Nothing>
    suspend fun sendComment(
        message: String,
        id: Long,
        _class: Class<*>,
        parentComment: Comment? = null
    ): Resource<Nothing>
    suspend fun getUser(uid: String): UserModel?
    suspend fun createPost(title: String, postItems: List<Any>): Resource<Nothing>
    suspend fun deleteComment(comment: Comment, item: ContentWithLikesAndComments<out Any>): Resource<Nothing>
}