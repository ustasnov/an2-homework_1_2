package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import kotlin.math.max
import kotlin.math.min

class PostRepositoryInMemoryImplementation: PostRepository {

    private var posts = listOf(
        Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу.",
            published = "15 января в 13:55",
            likedByMe = false,
            likes = 9999.0,
            shared = 9.0,
            views = 1000000.0
        ),
        Post(
            id = 2,
            author = "Нетология. Университет интернет-профессий",
            content = "Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растем сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
            published = "20 января в 14:00",
            likedByMe = false,
            likes = 999.0,
            shared = 5.0,
            views = 500.0
        ),
        Post(
            id = 3,
            author = "Нетология. Университет интернет-профессий",
            content = "Но самое важное остается с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее.",
            published = "30 января в 18:00",
            likedByMe = false,
            likes = 9.0,
            shared = 3.0,
            views = 200.0
        ),
        Post(
            id = 4,
            author = "Нетология. Университет интернет-профессий",
            content = "Наша миссия   - помочь встать на путь роста и начать цепочку   перемен -> http://www.netolo.gy/fyb",
            published = "2 февраля в 10:55",
            likedByMe = false,
            likes = 5.0,
            shared = 1.0,
            views = 10.0
        )
    )

    val data = MutableLiveData(posts)

    private fun calculateClicksCount(value: Double, increase: Boolean = true, term: Double = 1.0): Double =
        when {
            increase -> min(value + term, 1000000000.0)
            else -> max(value - term, 0.0)
        }

    override fun getAll(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(likedByMe = !it.likedByMe, likes = calculateClicksCount(it.likes, !it.likedByMe))
        }
        data.value = posts
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(shared = calculateClicksCount(it.shared, true))
        }
        data.value = posts
    }

    override fun viewById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(views = it.views + 1.0)
        }
        data.value = posts
    }
}
