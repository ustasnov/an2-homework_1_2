package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(callback: GenericCallback<List<Post>>)
    fun likeById(post: Post, callback: GenericCallback<Post>)
    fun shareById(id: Long)
    fun viewById(id: Long)
    fun removeById(id: Long, callback: GenericCallback<Unit>)
    fun save(post: Post, callback: GenericCallback<Post>)
    fun saveNewPostContent(text: String)
    fun getNewPostContent(): LiveData<String>

    interface GenericCallback<T> {
        fun onSuccess(data: T) {}
        fun onError(e: Exception) {}
    }
}
