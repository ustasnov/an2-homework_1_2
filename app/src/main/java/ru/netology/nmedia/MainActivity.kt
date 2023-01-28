package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.nmedia.databinding.ActivityMainBinding
import kotlin.math.max
import kotlin.math.min

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    var likedByMe: Boolean,
    var likes: Double,
    var shared: Double,
    var views: Double
)

fun formatValue(value: Double): String {
    if (value >= 1000000000.0) {
        return "\u221e"
    }
    val suffix: String
    val res = when {
        value >= 1000000.0 -> {
            suffix = "M"
            String.format("%f", value / 1000000.0)
        }
        value >= 1000.0 -> {
            suffix = "K"
            String.format("%f", value / 1000.0)
        }
        else -> {
            suffix = ""
            String.format("%f", value)
        }
    }
    val dotPosition = res.indexOf(".")

    return when {
        (value >= 10000.0 && value < 1000000.0) || value < 1000 || res[dotPosition + 1] == '0' ->
            res.substring(0, dotPosition) + suffix
        else -> res.substring(0, dotPosition + 2) + suffix
    }
}

fun calculateClicksCount(value: Double, increase: Boolean = true, term: Double = 1.0): Double =
    when {
        increase -> min(value + term, 1000000000.0)
        else -> max(value - term, 0.0)
    }


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растем сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остается с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия   - помочь встать на путь роста и начать цепочку   перемен -> http://www.netolo.gy/fyb",
            published = "15 января в 13:55",
            likedByMe = false,
            likes = 9999.0,
            shared = 9.0,
            views = 1000000.0
        )
        binding.apply {
            author.text = post.author
            published.text = post.published
            postText.text = post.content
            likes.text = formatValue(post.likes)
            shared.text = formatValue(post.shared)
            views.text = formatValue(post.views)
            if (post.likedByMe) {
                favorite.setImageResource(R.drawable.ic_baseline_favorite_24)
            }

            favorite.setOnClickListener {
                post.likedByMe = !post.likedByMe
                favorite.setImageResource(
                    if (post.likedByMe) R.drawable.ic_baseline_favorite_24 else R.drawable.ic_baseline_favorite_border_24
                )
                post.likes = calculateClicksCount(post.likes, post.likedByMe)
                likes.text = formatValue(post.likes)
            }

            share.setOnClickListener {
                post.shared = calculateClicksCount(post.shared, true)
                shared.text = formatValue(post.shared)
            }
        }

    }
}