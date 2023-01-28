package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel

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

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel by viewModels<PostViewModel>()
        viewModel.data.observe(this) {post ->
            binding.apply {
                author.text = post.author
                published.text = post.published
                postText.text = post.content
                favorite.setImageResource(
                    if (post.likedByMe) R.drawable.ic_baseline_favorite_24 else R.drawable.ic_baseline_favorite_border_24
                )
                likes.text = formatValue(post.likes)
                shared.text = formatValue(post.shared)
                views.text = formatValue(post.views)
            }
        }

        binding.favorite.setOnClickListener {
            viewModel.like()
        }

        binding.share.setOnClickListener {
            viewModel.share()
        }
    }
}
