package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.utils.SingleLiveEvent

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
    private val repository: PostRepository = PostRepositoryImpl(
        application
    )
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    var isNewPost = false

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _gotServerError = SingleLiveEvent<Boolean>()
    val gotServerError: LiveData<Boolean>
        get() = _gotServerError

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.postValue(FeedModel(loading = true))
        repository.getAll(object : PostRepository.GenericCallback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        edited.value?.let {
            repository.save(it, object : PostRepository.GenericCallback<Post> {
                override fun onSuccess(data: Post) {
                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    _gotServerError.postValue(true)
                }
            })
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

    fun likeById(post: Post) {
        if (!post.likedByMe) {
            repository.likeById(post.id, object : PostRepository.GenericCallback<Post> {
                override fun onSuccess(data: Post) {
                    _data.postValue(FeedModel(posts = _data.value?.posts.orEmpty().map {
                        if (it.id == post.id) data else it
                    }))
                }

                override fun onError(e: Exception) {
                    _gotServerError.postValue(true)
                }
            })
        } else {
            repository.unlikeById(post.id, object : PostRepository.GenericCallback<Post> {
                override fun onSuccess(data: Post) {
                    _data.postValue(FeedModel(posts = _data.value?.posts.orEmpty().map {
                        if (it.id == post.id) data else it
                    }))
                }

                override fun onError(e: Exception) {
                    _gotServerError.postValue(true)
                }
            })
        }
    }

    fun shareById(id: Long) {
        toggleNewPost(false)
    }

    fun viewById(id: Long) {
        toggleNewPost(false)
    }

    fun removeById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        repository.removeById(id, object : PostRepository.GenericCallback<Unit> {
            override fun onSuccess(data: Unit) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .filter { it.id != id }
                    )
                )
            }

            override fun onError(e: Exception) {
                _gotServerError.postValue(true)
                _data.postValue(_data.value?.copy(posts = old))
            }
        })
    }

    fun saveNewPostContent(text: String) {
        repository.saveNewPostContent(text)
    }

    fun getNewPostCont(): LiveData<String> {
        return repository.getNewPostContent()
    }
}
