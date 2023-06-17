package ru.netology.nmedia

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.utils.AndroidUtils.showKeyboard
import ru.netology.nmedia.utils.BooleanArg
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment(R.layout.fragment_new_post) {

    var _binding: FragmentNewPostBinding? = null
    val binding: FragmentNewPostBinding
        get() = _binding!!

    val viewModel: PostViewModel by activityViewModels()

    private val photoPickerContract = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        when (it.resultCode) {
            ImagePicker.RESULT_ERROR -> Toast.makeText(
                requireContext(),
                "Photo pick error",
                Toast.LENGTH_SHORT
            ).show()
            Activity.RESULT_OK -> {
                val uri = it.data?.data ?: return@registerForActivityResult
                viewModel.setPhoto(PhotoModel(uri, uri.toFile()))
            }
        }
    }

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
                //binding.buttonOk.setImageResource(R.drawable.ic_add_24)
            } else {
                //binding.buttonOk.setImageResource(R.drawable.ic_check_24)
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

        /*
        binding.buttonOk.setOnClickListener {
            val text = binding.content.text.toString()
            if (text.isNotBlank()) {
                viewModel.changeContent(text)
                viewModel.save()
                viewModel.saveNewPostContent("")
            } else {
                Toast.makeText(
                    this.context,
                    getString(R.string.empty_content_warning),
                    Toast.LENGTH_SHORT
                ).show()
            }
            AndroidUtils.hideKeyboard(requireView())
        }
        */

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.new_post_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.save -> {
                        val text = binding.content.text.toString()
                        if (text.isNotBlank()) {
                            viewModel.changeContent(text)
                            viewModel.save()
                            viewModel.saveNewPostContent("")
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.empty_content_warning),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        AndroidUtils.hideKeyboard(requireView())
                        true
                    }
                    else -> false
                }
        })

        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
            findNavController().navigateUp()
        }

        binding.clear.setOnClickListener {
            viewModel.clearPhoto()
        }

        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .crop()
                .createIntent(photoPickerContract::launch)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .cameraOnly()
                .crop()
                .createIntent(photoPickerContract::launch)
        }

        viewModel.photo.observe(viewLifecycleOwner) { photo ->
            if (photo == null) {
                binding.previewContainer.isGone = true
                return@observe
            }

            binding.previewContainer.isVisible = true
            binding.preview.setImageURI(photo.uri)
        }

        /*
        binding.buttonCancel.setOnClickListener {
            viewModel.toggleNewPost(false)
            viewModel.saveNewPostContent("")
            findNavController().navigateUp()
        }
        */

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
