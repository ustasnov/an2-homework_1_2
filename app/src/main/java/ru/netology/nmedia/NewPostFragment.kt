package ru.netology.nmedia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.utils.AndroidUtils.showKeyboard
import ru.netology.nmedia.utils.BooleanArg
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(layoutInflater, container, false)
        val viewModel: PostViewModel by activityViewModels()

        arguments?.isNewPost.let {
            if (it == null || it) {
                viewModel.toggleNewPost(true)
                binding.buttonOk.setImageResource(R.drawable.ic_add_24)
            } else {
                binding.buttonOk.setImageResource(R.drawable.ic_check_24)
            }
        }

        arguments?.textArg.let {
            binding.content.setText(it)
        }

        binding.buttonOk.setOnClickListener {
            val text = binding.content.text.toString()
            if (text.isNotBlank()) {
                viewModel.changeContent(text)
                viewModel.save()
                findNavController().navigateUp()
            }
        }

        binding.buttonCancel.setOnClickListener {
            viewModel.toggleNewPost(false)
            findNavController().navigateUp()
        }

        showKeyboard(requireContext(), binding.content)

        return binding.root
    }

    companion object {
        var Bundle.textArg: String? by StringArg
        var Bundle.isNewPost: Boolean? by BooleanArg
    }
}
