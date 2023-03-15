package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import ru.netology.nmedia.R
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import java.text.SimpleDateFormat
import java.util.*

class PostRepositoryImpl(
    private val dao: PostDao,
    context: Context
) : PostRepository {
    private val prefs = context.getSharedPreferences("repo", Context.MODE_PRIVATE)
    private val key = "newPostContent"
    private var newPostContentValue = MutableLiveData<String>()
    private val currentAuthor = context.getString(R.string.authorName)

    override fun getAll() = Transformations.map(dao.getAll()) { list ->
        list.map {
            it.toDto()
        }
    }

    override fun likeById(id: Long) {
        dao.likeById(id)
    }

    override fun shareById(id: Long) {
        dao.shareById(id)
    }

    override fun viewById(id: Long) {
        TODO("Not yet implemented")
    }

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

    override fun removeById(id: Long) {
        dao.removeById(id)
    }
}
