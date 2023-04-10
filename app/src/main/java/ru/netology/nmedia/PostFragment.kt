package ru.netology.nmedia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.LongArg
import ru.netology.nmedia.viewmodel.PostViewModel

class PostFragment : Fragment(R.layout.fragment_post) {
    var _binding: FragmentPostBinding? = null
    val binding: FragmentPostBinding
        get() = _binding!!

    val viewModel: PostViewModel by activityViewModels()

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().navigateUp()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPostBinding.bind(view)

        val viewHolder = PostViewHolder(binding.postFr, object : OnInteractionListener {
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

            override fun onEdit(post: Post) {
                viewModel.edit(post)
                findNavController().navigate(
                    R.id.action_postFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = post.content
                    }
                )
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
                findNavController().navigate(R.id.action_postFragment_to_feedFragment)
            }

            override fun onPlayVideo(post: Post) {
                viewModel.toggleNewPost(false)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                if (intent.resolveActivity(context!!.packageManager) != null) {
                    startActivity(intent)
                }
            }
        })

        val postId = requireArguments().idArg

        binding.postFr.apply {
            viewModel.data.observe(viewLifecycleOwner) { it ->
                val post = it.posts.find { it.id == postId } ?: return@observe
                post.let { viewHolder.bind(post) }
            }
        }

        setupBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)
    }

    companion object {
        var Bundle.idArg: Long? by LongArg
    }
}
