package ru.netology.nmedia.repository

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository.GenericCallback
import java.io.IOException
import java.util.concurrent.TimeUnit

class PostRepositoryImpl(
    context: Application
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

    companion object {
        //private const val BASE_URL = "http://10.0.2.2:9999"
        private const val BASE_URL = "http://192.168.1.66:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(callback: GenericCallback<List<Post>>) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: throw RuntimeException("body is null")
                    try {
                        callback.onSuccess(gson.fromJson(body, typeToken.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }


    override fun likeById(post: Post, callback: GenericCallback<Post>) {
        val request = if (post.likedByMe) {
            Request.Builder()
                .delete()
        } else {
            Request.Builder()
                .post(gson.toJson(post).toRequestBody(jsonType))
        }
            .url("${BASE_URL}/api/slow/posts/${post.id}/likes")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        callback.onError(Exception(response.message))
                    }
                    val body = requireNotNull(response.body?.string()) { "body is null" }
                    callback.onSuccess(gson.fromJson(body, Post::class.java))
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun shareById(id: Long) {

    }

    override fun viewById(id: Long) {

    }

    override fun save(post: Post, callback: GenericCallback<Post>) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        callback.onError(Exception(response.message))
                    }
                    val body = requireNotNull(response.body?.string()) { "body is null" }
                    callback.onSuccess(gson.fromJson(body, Post::class.java))
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
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

    override fun removeById(id: Long, callback: GenericCallback<Unit>) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        callback.onError(Exception(response.message))
                    }
                    callback.onSuccess(Unit)
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }
}
