package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(post: Post): Post
    fun shareById(id: Long)
    fun viewById(id: Long)
    fun removeById(id: Long)
    fun save(post: Post)
    fun saveNewPostContent(text: String)
    fun getNewPostContent(): LiveData<String>
}
