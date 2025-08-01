package com.example.astrataskapp.presentation.details

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.astrataskapp.databinding.CustomeDialogAddEditPostBinding
import com.example.astrataskapp.databinding.FragmentPostDetailsBinding
import com.example.astrataskapp.domain.model.Post
import com.example.astrataskapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostDetailsFragment : Fragment() {
    private var _binding: FragmentPostDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mNavController: NavController

    // [by] to get value direct therefor don't (.value)
    private val args: PostDetailsFragmentArgs by navArgs()

    private lateinit var post: Post

    // this dagger hilt get automatic instance from postDetailsViewModel
    private val postDetailsViewModel: PostDetailsViewModel by viewModels()

    private lateinit var bindingDialog: CustomeDialogAddEditPostBinding

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                bindingDialog.layoutSelectPhotoTemp.visibility = View.GONE
                bindingDialog.ivPostPhoto.visibility = View.VISIBLE
                bindingDialog.ivPostPhoto.setImageURI(it)
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPostDetailsBinding.inflate(inflater, container, false)

        mNavController = findNavController()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.postDetailsBack.setOnClickListener {
            mNavController.popBackStack()
        }

        // get post by id
        postDetailsViewModel.getPostsById(args.postId)
        observePostByIdLiveData { returnPost ->
            post = returnPost
        }

        observeDeletePostLiveData()

        observeUpdatePostLiveData()

        binding.btnEditPost.setOnClickListener {
            showUpdatePostDialog()
        }

        binding.btnDeletePost.setOnClickListener {
            postDetailsViewModel.deletePost(post.id)
            mNavController.popBackStack()
        }
    }

    private fun observeDeletePostLiveData() {
        postDetailsViewModel.deletePostLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarPostDetails.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    binding.progressBarPostDetails.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.data, Toast.LENGTH_SHORT).show()
                }

                is Resource.Failure -> {
                    binding.progressBarPostDetails.visibility = View.GONE
                }
            }
        }
    }


    // observe post by id
    private fun observePostByIdLiveData(returnPost: (Post) -> Unit) {
        postDetailsViewModel.getOnePostsLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarPostDetails.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    binding.progressBarPostDetails.visibility = View.GONE
                    val post = resource.data

                    Glide.with(requireContext()).load(post.image)
                        .into(binding.ivPostImage)

                    binding.tvPostTitle.text = post.title
                    binding.tvPostContent.text = post.content

                    returnPost(post)
                }

                is Resource.Failure -> {
                    binding.progressBarPostDetails.visibility = View.GONE
                }
            }
        }
    }

    // observe update post
    private fun observeUpdatePostLiveData() {
        postDetailsViewModel.updatePostLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarPostDetails.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    binding.progressBarPostDetails.visibility = View.GONE
                    val post = resource.data

                    Glide.with(requireContext()).load(post.image)
                        .into(binding.ivPostImage)

                    binding.tvPostTitle.text = post.title
                    binding.tvPostContent.text = post.content

                    Toast.makeText(
                        requireContext(),
                        "Post updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is Resource.Failure -> {
                    binding.progressBarPostDetails.visibility = View.GONE
                }
            }
        }
    }


    private fun showUpdatePostDialog() {
        bindingDialog = CustomeDialogAddEditPostBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(bindingDialog.root)
            .create()

        bindingDialog.apply {
            tvDialogTitle.text = "Edit Post"
            btnPost.text = "Edit"
            if (post.image.isNotEmpty()) {
                ivPostPhoto.visibility = View.VISIBLE
                Glide.with(requireContext()).load(post.image)
                    .into(ivPostPhoto)

                tvEditPost.visibility = View.VISIBLE
            }

            etPostTitle.setText(post.title)
            etPostMessage.setText(post.content)
        }

        bindingDialog.ivCloseDialog.setOnClickListener {
            dialog.dismiss()
        }

        // select photo from gallery
        bindingDialog.layoutSelectPhoto.setOnClickListener {
            pickImageFromGallery()
        }

        // change photo from gallery
        bindingDialog.tvEditPost.setOnClickListener {
            pickImageFromGallery()
        }

        // add post
        bindingDialog.btnPost.setOnClickListener {
            val title = bindingDialog.etPostTitle.text.toString().trim()
            val message = bindingDialog.etPostMessage.text.toString().trim()

            if (title.isEmpty() || message.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val imageUri =
                if (bindingDialog.ivPostPhoto.tag != null && bindingDialog.ivPostPhoto.visibility == View.VISIBLE) {
                    bindingDialog.ivPostPhoto.tag.toString().toUri()
                } else {
                    null
                }


            postDetailsViewModel.updatePost(
                postId = post.id,
                title = title,
                content = message,
                imageUri = imageUri
            )

            dialog.dismiss()
            return@setOnClickListener
        }


        // cancel dialog
        bindingDialog.btnCancelPost.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun pickImageFromGallery() {
        pickImageLauncher.launch("image/*")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}