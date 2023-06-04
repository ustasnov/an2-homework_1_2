package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(postsCallback: GenericCallback<List<Post>>)
    fun likeById(id: Long, postsCallback: GenericCallback<Post>)
    fun unlikeById(id: Long, postsCallback: GenericCallback<Post>)
    fun shareById(id: Long)
    fun viewById(id: Long)
    fun removeById(id: Long, postsCallback: GenericCallback<Unit>)
    fun save(post: Post, postsCallback: GenericCallback<Post>)
    fun saveNewPostContent(text: String)
    fun getNewPostContent(): LiveData<String>

    interface GenericCallback<T> {
        fun onSuccess(data: T) {}
        fun onError(e: Exception) {}
    }
}
