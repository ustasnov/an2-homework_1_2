package ru.netology.nmedia

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.utils.AndroidUtils.showKeyboard
import ru.netology.nmedia.utils.BooleanArg
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment(R.layout.fragment_new_post) {

    var _binding: FragmentNewPostBinding? = null
    val binding: FragmentNewPostBinding
        get() = _binding!!

    val viewModel: PostViewModel by activityViewModels()

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (viewModel.isNewPost) {
                viewModel.saveNewPostContent(binding.content.text.toString())
            }
            findNavController().navigateUp()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNewPostBinding.bind(view)

        arguments?.isNewPost.let {
            if (it == null || it) {
                viewModel.toggleNewPost(true)
                binding.buttonOk.setImageResource(R.drawable.ic_add_24)
            } else {
                binding.buttonOk.setImageResource(R.drawable.ic_check_24)
            }
        }

        arguments?.textArg.let {
            if (viewModel.isNewPost) {
                val text = viewModel.getNewPostCont().value
                binding.content.setText(text)
            } else {
                binding.content.setText(it)
            }
        }
        binding.content.requestFocus()

        binding.buttonOk.setOnClickListener {
            val text = binding.content.text.toString()
            if (text.isNotBlank()) {
                viewModel.changeContent(text)
                viewModel.save()
                viewModel.saveNewPostContent("")
                findNavController().navigateUp()
            }
        }

        binding.buttonCancel.setOnClickListener {
            viewModel.toggleNewPost(false)
            viewModel.saveNewPostContent("")
            findNavController().navigateUp()
        }

        setupBackPressed()
        showKeyboard(requireContext(), binding.content)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)
    }

    companion object {
        var Bundle.textArg: String? by StringArg
        var Bundle.isNewPost: Boolean? by BooleanArg
    }
}
