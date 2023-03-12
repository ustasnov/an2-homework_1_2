package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositorySQLiteImpl

val empty = Post(
    id = 0L,
    author = "",
    content = "",
    published = "",
    likedByMe = false,
    likes = 0.0,
    shared = 0.0,
    views = 0.0,
    video = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositorySQLiteImpl(
        AppDb.getInstance(application).postDao,
        application
    )
    val data = repository.getAll()
    val edited = MutableLiveData(empty)
    var isNewPost = false
    var newPostContent = repository.getNewPostContent()

    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        toggleNewPost(false)
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun toggleNewPost(isNew: Boolean) {
        isNewPost = isNew
    }

    fun likeById(id: Long) {
        toggleNewPost(false)
        repository.likeById(id)
    }

    fun shareById(id: Long) {
        toggleNewPost(false)
        repository.shareById(id)
    }

    fun viewById(id: Long) {
        toggleNewPost(false)
        repository.viewById(id)
    }

    fun removeById(id: Long) {
        toggleNewPost(false)
        repository.removeById(id)
    }

    fun saveNewPostContent(text: String) {
        repository.saveNewPostContent(text)
    }

    fun getNewPostCont(): LiveData<String> {
        return repository.getNewPostContent()
    }
}
