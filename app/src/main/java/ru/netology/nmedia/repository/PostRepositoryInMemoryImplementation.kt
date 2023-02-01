package ru.netology.nmedia.repository

import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import kotlin.math.max
import kotlin.math.min

class PostRepositoryInMemoryImplementation: PostRepository {

    private var post = Post(
        id = 1,
        author = "Нетология. Университет интернет-профессий",
        content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растем сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остается с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия   - помочь встать на путь роста и начать цепочку   перемен -> http://www.netolo.gy/fyb",
        published = "15 января в 13:55",
        likedByMe = false,
        likes = 9999.0,
        shared = 9.0,
        views = 1000000.0
    )

    val data = MutableLiveData(post)

    private fun calculateClicksCount(value: Double, increase: Boolean = true, term: Double = 1.0): Double =
        when {
            increase -> min(value + term, 1000000000.0)
            else -> max(value - term, 0.0)
        }

    override fun get() = data

    override fun like() {
        post = post.copy(likedByMe = !post.likedByMe, likes = calculateClicksCount(post.likes, !post.likedByMe))
        data.value = post
    }

    override fun share() {
        post = post.copy(shared = calculateClicksCount(post.shared, true))
        data.value = post
    }

    override fun view() {
        post = post.copy(views = post.views + 1.0)
        data.value = post
    }
}
