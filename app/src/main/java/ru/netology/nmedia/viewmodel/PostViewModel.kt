package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.ErrorType
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.utils.SingleLiveEvent

val empty = Post(
    id = 0L,
    author = "",
    authorAvatar = "",
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
        application, AppDb.getInstance(application).postDao()
    )
    val data: LiveData<FeedModel> =
        repository.data.map( ::FeedModel ).asLiveData(Dispatchers.Default)

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _photo = MutableLiveData<PhotoModel?>()
    val photo: LiveData<PhotoModel?>
        get() = _photo

    val edited = MutableLiveData(empty)
    var isNewPost = false

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _currentPostId = MutableLiveData<Long>()
    val currentPostId: LiveData<Long>
        get() = _currentPostId

    private val _currentPost =
        MutableLiveData(
            Post(
                0,
                "",
                "",
                "",
                "",
                false,
                0.0,
                0.0,
                0.0,
                ""
            )
        )
    val currentPost: LiveData<Post>
        get() = _currentPost

    val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewer(it.posts.firstOrNull()?.id ?: 0L)
            .catch { e -> e.printStackTrace() }
            .asLiveData(Dispatchers.Default)
    }.distinctUntilChanged()

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        _dataState.value = FeedModelState(loading = true)
        try {
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = ErrorType.LOADING)
        }
    }

    fun refresh() = viewModelScope.launch {
        _dataState.value = FeedModelState(refreshing = true)
        try {
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = ErrorType.LOADING)
        }
    }

    fun showHiddenPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.showAll()
            _dataState.value = FeedModelState()
        } catch (e: java.lang.Exception) {
            _dataState.value = FeedModelState(error = ErrorType.LOADING)
        }
    }

    fun save() = viewModelScope.launch {
        try {
            edited.value?.let {
                repository.save(it)
            }
            edited.value = empty
            _postCreated.postValue(Unit)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = ErrorType.SAVE)
        }
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

    fun likeById(post: Post) = viewModelScope.launch {
        _currentPost.setValue(post)
        try {
            if (_currentPost.value?.likedByMe == false) {
                repository.likeById(_currentPost.value!!.id)
            } else {
                repository.unlikeById(_currentPost.value!!.id)
            }
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = ErrorType.LIKE)
        }
    }

    fun shareById(id: Long) = viewModelScope.launch {
        _currentPostId.setValue(id)
        repository.shareById(_currentPostId.value!!)
    }

    fun viewById(id: Long) {
        toggleNewPost(false)
    }

    fun removeById(id: Long) = viewModelScope.launch {
        //_lastId.postValue(id)
        _currentPostId.setValue(id)
        try {
            repository.removeById(_currentPostId.value!!)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = ErrorType.REMOVE)
        }
    }

    fun saveNewPostContent(text: String) {
        repository.saveNewPostContent(text)
    }

    fun getNewPostCont(): LiveData<String> {
        return repository.getNewPostContent()
    }

    fun clearPhoto() {
        _photo.value = null
    }

    fun setPhoto(photoModel: PhotoModel) {
        _photo.value = photoModel
    }
}
