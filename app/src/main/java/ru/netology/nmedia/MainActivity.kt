package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel
import java.util.Locale

fun formatValue(value: Double): String {
    if (value >= 1000000000.0) {
        return "\u221e"
    }
    val suffix: String
    val res = when {
        value >= 1000000.0 -> {
            suffix = "M"
            String.format(Locale.ROOT,"%f", value / 1000000.0)
        }
        value >= 1000.0 -> {
            suffix = "K"
            String.format(Locale.ROOT,"%f", value / 1000.0)
        }
        else -> {
            suffix = ""
            String.format(Locale.ROOT,"%f", value)
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

    val interactionListener = object: OnInteractionListener {
        override fun onLike(post: Post) {
            viewModel.likeById(post.id)
        }

        override fun onShare(post: Post) {
            viewModel.shareById(post.id)
        }

        override fun onRemove(post: Post) {
            viewModel.removeById(post.id)
        }

        override fun onEdit(post: Post) {
            viewModel.edit(post)
        }

        override fun onCancel() {
            viewModel.cancel()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = PostsAdapter(interactionListener)

        binding.list.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        viewModel.edited.observe(this) { post ->
            if (post.id != 0L) {
                with(binding.content) {
                    requestFocus()
                    setText(post.content)
                    AndroidUtils.showKeyboard(this)
                }
            } else {
                with(binding.content) {
                    setText("")
                    clearFocus()
                    AndroidUtils.hideKeyboard(this)
                }
            }
        }

        binding.save.setOnClickListener {
            with(binding.content) {
                if (text.isNullOrBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        context.getString(R.string.error_empty_content),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                viewModel.changeContent(text.toString())
                viewModel.save()

                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)
            }
            val postAdapter = binding.list.getAdapter()
            if (postAdapter != null) {
                binding.list.scrollToPosition(postAdapter.getItemCount() - 1)
            }
        }

        binding.cancel.setOnClickListener {
            interactionListener.onCancel()
        }
    }
}
