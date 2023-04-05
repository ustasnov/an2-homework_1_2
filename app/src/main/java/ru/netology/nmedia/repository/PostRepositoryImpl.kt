package ru.netology.nmedia.repository

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.R
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class PostRepositoryImpl (context: Application
) : PostRepository {
    private val prefs = context.getSharedPreferences("repo", Context.MODE_PRIVATE)
    private val key = "newPostContent"
    private var newPostContentValue = MutableLiveData<String>()
    private val currentAuthor = context.getString(R.string.authorName)

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeToken.type)
            }
    }

    override fun likeById(id: Long) {
        //dao.likeById(id)
    }

    override fun shareById(id: Long) {
        //dao.shareById(id)
    }

    override fun viewById(id: Long) {
        TODO("Not yet implemented")
    }

    /*
    override fun save(post: Post) {
        if (post.id == 0L) {
            dao.save(
                PostEntity.fromDto(
                    post.copy(
                        author = currentAuthor,
                        published = SimpleDateFormat("dd MMMM в HH:mm")
                            .format(Calendar.getInstance().time)
                    )
                )
            )
        } else {
            dao.save(PostEntity.fromDto(post))
        }
    }
    */

    override fun save(post: Post) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun saveNewPostContent(text: String) {
        newPostContentValue.value = text
        with(prefs.edit()) {
            putString(key, newPostContentValue.value)
            apply()
        }
    }

    override fun getNewPostContent(): LiveData<String> {
        prefs.getString(key, "")?.let {
            newPostContentValue.value = it
        }
        return newPostContentValue
    }

    /*
    override fun removeById(id: Long) {
        dao.removeById(id)
    }
    */
    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }
}
