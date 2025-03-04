package ru.myitschool.nasa_bootcamp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.myitschool.nasa_bootcamp.data.model.*
import ru.myitschool.nasa_bootcamp.utils.Resource
import ru.myitschool.nasa_bootcamp.utils.Status
import javax.inject.Inject

class SocialMediaRepositoryImpl @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val newsRepository: NewsRepository
) : SocialMediaRepository {
    override suspend fun getNews(): Resource<List<LiveData<ContentWithLikesAndComments<ArticleModel>>>> {
        val newsList = mutableListOf<LiveData<ContentWithLikesAndComments<ArticleModel>>>()
        val news = newsRepository.getNews()
        if (news.status == Status.ERROR) {
            return Resource.error(news.message!!, null)
        }
        for (article in news.data.orEmpty()) {
            val data = MutableLiveData<ContentWithLikesAndComments<ArticleModel>>()
            val comments = firebaseRepository.getArticleModelComments(article.id)
            val likes = firebaseRepository.getArticleModelLikes(article.id)
            data.postValue(ContentWithLikesAndComments(article, likes, comments))
            firebaseRepository.articleModelEventListener(data, article.id)
            newsList.add(data)
        }
        return Resource.success(
            newsList
        )
    }

    override suspend fun getBlogPosts(): Resource<List<LiveData<ContentWithLikesAndComments<PostModel>>>> {
        val result = firebaseRepository.getAllPostsRawData()
        if (result.status == Status.ERROR) {
            return Resource.error(result.message.toString(), null)
        }
        return Resource.success(result.data)
    }

    override suspend fun pressedLikeOnItem(
        item: ContentWithLikesAndComments<out Any>
    ): Resource<Nothing> {
        try {
            if (item.content::class.java == ArticleModel::class.java) {
                val result = firebaseRepository.pushLike(
                    "ArticleModel",
                    (item.content as ArticleModel).id.toInt()
                )
                if (result.status == Status.ERROR) {
                    return Resource.error(result.message.toString(), null)
                }
            } else {
                val result = firebaseRepository.pushLike(
                    "UserPost",
                    (item.content as PostModel).id.toInt()
                )
                if (result.status == Status.ERROR) {
                    return Resource.error(result.message.toString(), null)
                }
            }
        } catch (e: Exception) {
            return Resource.error(e.message.toString(), null)
        }
        return Resource.success(null)
    }

    override suspend fun pressedLikeOnComment(
        item: ContentWithLikesAndComments<out Any>,
        comment: Comment
    ): Resource<Nothing> {
        try {
            if (comment::class.java != SubComment::class.java) {
                if (item.content::class.java == ArticleModel::class.java) {
                    val result = firebaseRepository.pushLikeForComment(
                        "ArticleModel",
                        (item.content as ArticleModel).id.toInt(),
                        comment.id
                    )
                    if (result.status == Status.ERROR) {
                        return Resource.error(result.message.toString(), null)
                    }
                } else {
                    val result = firebaseRepository.pushLikeForComment(
                        "UserPost",
                        (item.content as PostModel).id.toInt(),
                        comment.id
                    )
                    if (result.status == Status.ERROR) {
                        return Resource.error(result.message.toString(), null)
                    }
                }
            } else {
                if (item.content::class.java == ArticleModel::class.java) {
                    val result = firebaseRepository.pushLikeForSubComment(
                        "ArticleModel",
                        (item.content as ArticleModel).id.toInt(),
                        (comment as SubComment).parentComment.id,
                        comment.id
                    )
                    if (result.status == Status.ERROR) {
                        return Resource.error(result.message.toString(), null)
                    }
                } else {
                    val result = firebaseRepository.pushLikeForSubComment(
                        "UserPost",
                        (item.content as PostModel).id.toInt(),
                        (comment as SubComment).parentComment.id,
                        comment.id
                    )
                    if (result.status == Status.ERROR) {
                        return Resource.error(result.message.toString(), null)
                    }
                }
            }
        } catch (e: Exception) {
            return Resource.error(e.message.toString(), null)
        }
        return Resource.success(null)
    }

    override suspend fun sendComment(
        message: String,
        id: Long,
        _class: Class<*>,
        parentComment: Comment?
    ): Resource<Nothing> {
        if (parentComment == null) {
            if (_class == ArticleModel::class.java) {
                val result = firebaseRepository.pushComment("ArticleModel", id.toInt(), message)
                if (result.status == Status.ERROR) {
                    return Resource.error(result.message.toString(), null)
                }
            } else {
                val result = firebaseRepository.pushComment("UserPost", id.toInt(), message)
                if (result.status == Status.ERROR) {
                    return Resource.error(result.message.toString(), null)
                }
            }
        } else {
            if (_class == ArticleModel::class.java) {
                val result = firebaseRepository.pushSubComment(
                    "ArticleModel",
                    id.toInt(),
                    parentComment.id,
                    message
                )
                if (result.status == Status.ERROR) {
                    return Resource.error(result.message.toString(), null)
                }
            } else {
                val result = firebaseRepository.pushSubComment(
                    "UserPost",
                    id.toInt(),
                    parentComment.id,
                    message
                )
                if (result.status == Status.ERROR) {
                    return Resource.error(result.message.toString(), null)
                }
            }
        }
        return Resource.success(null)
    }

    override suspend fun getUser(uid: String): UserModel? {
        return firebaseRepository.getUser(uid)
    }

    override suspend fun createPost(title: String, postItems: List<Any>): Resource<Nothing> {
        if (postItems.isEmpty())
            return Resource.error("Post without aby data", null)
        val result = firebaseRepository.createPost(title, postItems)
        if (result.status == Status.ERROR) {
            return Resource.error(result.message.toString(), null)
        }
        return Resource.success(null)
    }

    override suspend fun deleteComment(
        comment: Comment,
        item: ContentWithLikesAndComments<out Any>
    ): Resource<Nothing> {
        val source =
            if (item.content is ArticleModel) "ArticleModel"
            else "UserPost"
        val postId = when (item.content) {
            is ArticleModel -> item.content.id
            is PostModel -> item.content.id
            else -> throw ClassCastException("Couldn't cast item to ArticleModel or PostModel")
        }
        return if (comment is SubComment) firebaseRepository.deleteSubComment(
            source = source,
            postId = postId.toInt(),
            subCommentId = comment.id,
            fatherCommentId = comment.parentComment.id
        ) else firebaseRepository.deleteComment(
            source = source,
            postId = postId.toInt(),
            commentId = comment.id
        )
    }

    override suspend fun getCurrentUser(): UserModel? = firebaseRepository.getCurrentUser()
}

