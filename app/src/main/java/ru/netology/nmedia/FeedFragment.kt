package ru.netology.nmedia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.NewPostFragment.Companion.textArg
import ru.netology.nmedia.PostFragment.Companion.idArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.ErrorType
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.viewmodel.empty
import java.util.*

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

class FeedFragment : Fragment(R.layout.fragment_feed) {
    var _binding: FragmentFeedBinding? = null
    val binding: FragmentFeedBinding
        get() = _binding!!

    val viewModel: PostViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFeedBinding.bind(view)
        val adapter = PostsAdapter(object : OnInteractionListener {

            override fun onLike(post: Post) {
                viewModel.likeById(post)
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
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = post.content
                    }
                )
            }

            override fun onPlayVideo(post: Post) {
                viewModel.toggleNewPost(false)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                if (intent.resolveActivity(context!!.packageManager) != null) {
                    startActivity(intent)
                }
            }

            override fun onViewAttachment(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_postPhotoFragment,
                    Bundle().apply {
                        textArg = "${BuildConfig.BASE_URL}media/${post.attachment!!.url}"
                    })
            }

            override fun onViewPost(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_postFragment,
                    Bundle().apply {
                        idArg = post.id
                    }
                )
            }
        })

        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { state ->

            adapter.submitList(state.posts)
            binding.emptyText.isVisible = state.empty
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.swiperefresh.isRefreshing = state.refreshing
            when (state.error) {
                ErrorType.LOADING ->
                    Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                        .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                        .show()
                ErrorType.SAVE ->
                    Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                        .setAction(R.string.retry_loading) { viewModel.save() }
                        .show()
                ErrorType.LIKE ->
                    Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                        .setAction(R.string.retry_loading) { viewModel.likeById(viewModel.currentPost.value!!) }
                        .show()
                ErrorType.REMOVE ->
                    Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                        .setAction(R.string.retry_loading) { viewModel.removeById(viewModel.currentPostId.value!!) }
                        .show()
                null -> Unit
            }
        }

        adapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
        })

        binding.add.setOnClickListener {
            viewModel.edit(empty)
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        viewModel.newerCount.observe(viewLifecycleOwner) {
            if (it > 0) {
                binding.newPostsButton.visibility = VISIBLE
            }
        }

        binding.newPostsButton.setOnClickListener {
            viewModel.showHiddenPosts()
            it.visibility = GONE
        }

        val swipeRefresh = binding.swiperefresh
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = true
            viewModel.refresh()
            swipeRefresh.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
