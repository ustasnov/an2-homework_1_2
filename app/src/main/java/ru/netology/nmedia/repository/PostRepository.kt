package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: Flow<List<Post>>
    fun getNewer(id: Long): Flow<Int>
    suspend fun getAll()
    suspend fun getAllVisible()
    suspend fun showAll()
    suspend fun likeById(id: Long)
    suspend fun unlikeById(id: Long)
    suspend fun shareById(id: Long)
    suspend fun viewById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(post: Post)
    fun saveNewPostContent(text: String)
    fun getNewPostContent(): LiveData<String>
}
