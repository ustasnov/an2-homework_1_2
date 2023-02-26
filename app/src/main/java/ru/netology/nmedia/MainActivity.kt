package ru.netology.nmedia

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.viewmodel.empty
import java.util.Locale

fun formatValue(value: Double): String {
    if (value >= 1000000000.0) {
        return "\u221e"
    }
    val suffix: String
    val res = when {
        value >= 1000000.0 -> {
            suffix = "M"
            String.format(Locale.ROOT, "%f", value / 1000000.0)
        }
        value >= 1000.0 -> {
            suffix = "K"
            String.format(Locale.ROOT, "%f", value / 1000.0)
        }
        else -> {
            suffix = ""
            String.format(Locale.ROOT, "%f", value)
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

    val viewModel: PostViewModel by viewModels()

    val interactionListener = object : OnInteractionListener {

        override fun onLike(post: Post) {
            viewModel.likeById(post.id)
        }

        override fun onShare(post: Post) {
            viewModel.shareById(post.id)
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, post.content)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(intent, getString(R.string.share_post))
            startActivity(shareIntent)
        }

        override fun onRemove(post: Post) {
            viewModel.removeById(post.id)
        }

        override fun onEdit(post: Post) {
            viewModel.edit(post)
        }

        override fun onPlayVideo(post: Post) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newPostContract =
            registerForActivityResult(NewPostActivity.NewPostContract) { content ->
                content ?: return@registerForActivityResult
                viewModel.changeContent(content)
                viewModel.save()
            }

        val adapter = PostsAdapter(interactionListener, newPostContract)
        binding.list.adapter = adapter
        viewModel.data.observe(this) { posts ->
            val isNewPost = adapter.currentList.size < posts.size && adapter.currentList.size > 0
            adapter.submitList(posts) {
                if (isNewPost) {
                    binding.list.scrollToPosition(adapter.currentList.size - 1)
                }
            }
        }

        binding.add.setOnClickListener {
            viewModel.edit(empty)
            newPostContract.launch(null)
        }
    }
}

