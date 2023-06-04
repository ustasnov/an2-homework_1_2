package ru.netology.nmedia.repository

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.R
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository.GenericCallback
import kotlin.random.Random

class PostRepositoryImpl(
    context: Application
) : PostRepository {
    private val prefs = context.getSharedPreferences("repo", Context.MODE_PRIVATE)
    private val key = "newPostContent"
    private var newPostContentValue = MutableLiveData<String>()
    private val currentAuthor = context.getString(R.string.authorName)

    //private var badResponseCode = Random.nextBoolean()
    private var badResponseCode = false

    override fun getAll(postsCallback: GenericCallback<List<Post>>) {
        PostApi.service.getAll()
            .enqueue(object: Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (!response.isSuccessful){
                        postsCallback.onError(RuntimeException(response.message()))
                    }
                    postsCallback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    postsCallback.onError(RuntimeException(t))
                }
            })
    }

    override fun likeById(id: Long, postsCallback: GenericCallback<Post>) {
        PostApi.service.likeById(id)
            .enqueue(object: Callback<Post>{
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful){
                        postsCallback.onError(RuntimeException(response.message()))
                    }

                    if (!badResponseCode) {
                        postsCallback.onSuccess(
                            response.body() ?: throw RuntimeException("body is null")
                        )
                    } else {
                        postsCallback.onError(RuntimeException(response.message()))
                    }
                    badResponseCode =! badResponseCode

                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    postsCallback.onError(RuntimeException(t))
                }
            })
    }

    override fun unlikeById(id: Long, postsCallback: GenericCallback<Post>) {
        PostApi.service.unlikeById(id)
            .enqueue(object: Callback<Post>{
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful){
                        postsCallback.onError(RuntimeException(response.message()))
                    }

                    if (!badResponseCode) {
                        postsCallback.onSuccess(
                            response.body() ?: throw RuntimeException("body is null")
                        )
                    } else {
                        postsCallback.onError(RuntimeException(response.message()))
                    }
                    badResponseCode =! badResponseCode
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    postsCallback.onError(RuntimeException(t))
                }
            })
    }

    override fun shareById(id: Long) {

    }

    override fun viewById(id: Long) {

    }

    override fun save(post: Post, postsCallback: GenericCallback<Post>) {
        PostApi.service.save(post)
            .enqueue(object: Callback<Post>{
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful){
                        postsCallback.onError(RuntimeException(response.message()))
                    }
                    if(!badResponseCode) {
                        postsCallback.onSuccess(
                            response.body() ?: throw RuntimeException("body is null")
                        )
                    } else{
                        postsCallback.onError(RuntimeException(response.message()))
                    }
                    badResponseCode =! badResponseCode
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    postsCallback.onError(RuntimeException(t))
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

    override fun removeById(id: Long, postsCallback: GenericCallback<Unit>) {
        PostApi.service.removeById(id)
            .enqueue(object: Callback<Unit>{
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (!response.isSuccessful){
                        postsCallback.onError(RuntimeException(response.message()))
                    }

                    if(!badResponseCode) {
                        postsCallback.onSuccess(Unit)
                    } else{
                        postsCallback.onError(RuntimeException(response.message()))
                    }
                    badResponseCode =! badResponseCode
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    postsCallback.onError(RuntimeException(t))
                }
            })
    }
}
