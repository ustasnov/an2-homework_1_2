package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

class PostRepositoryFileImplementation(
    private val context: Context
) : PostRepository {
    private val gson = Gson()
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val filename = "posts.json"
    private val defaultAuthor = "Угаров Станислав"
    private var nextId = 1L
    private var posts = emptyList<Post>()
    val data = MutableLiveData(posts)

    init {
        val file = context.filesDir.resolve(filename)
        if (file.exists()) {
            context.openFileInput(filename).bufferedReader().use {
                posts = gson.fromJson(it, type)
                nextId = posts.maxOfOrNull { it.id }?.inc() ?: 1
                data.value = posts
            }
        } else {
            sync()
        }
    }

    private fun calculateClicksCount(
        value: Double,
        increase: Boolean = true,
        term: Double = 1.0
    ): Double =
        when {
            increase -> min(value + term, 1000000000.0)
            else -> max(value - term, 0.0)
        }

    override fun getAll(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                likes = calculateClicksCount(it.likes, !it.likedByMe)
            )
        }
        data.value = posts
        sync()
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(shared = calculateClicksCount(it.shared, true))
        }
        data.value = posts
        sync()
    }

    override fun viewById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(views = it.views + 1.0)
        }
        data.value = posts
        sync()
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
        sync()
    }

    override fun save(post: Post) {
        if (post.id == 0L) {
            posts += listOf(
                post.copy(
                    id = nextId++,
                    author = defaultAuthor,
                    published = SimpleDateFormat("dd MMMM в HH:mm").format(Calendar.getInstance().time)
                )
            )
        } else {
            posts = posts.map {
                if (it.id != post.id) it else it.copy(content = post.content)
            }
        }
        data.value = posts
        sync()
    }

    override fun saveNewPostContent(text: String) {
        TODO("Not yet implemented")
    }

    override fun getNewPostContent(): LiveData<String> {
        TODO("Not yet implemented")
    }

    private fun sync() {
        context.openFileOutput(filename, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.toJson(posts))
        }
    }
}
