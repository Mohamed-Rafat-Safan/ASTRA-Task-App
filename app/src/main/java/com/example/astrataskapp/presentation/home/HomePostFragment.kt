package com.example.astrataskapp.presentation.home

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.astrataskapp.databinding.CustomeDialogAddEditPostBinding
import com.example.astrataskapp.databinding.FragmentHomePostBinding
import com.example.astrataskapp.domain.model.Post
import com.example.astrataskapp.presentation.adapters.PostAdapter
import com.example.astrataskapp.presentation.details.PostDetailsViewModel
import com.example.astrataskapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomePostFragment : Fragment(), PostAdapter.PostOnClickListener {
    private var _binding: FragmentHomePostBinding? = null
    private val binding get() = _binding!!
    private lateinit var mNavController: NavController

    private lateinit var postAdapter: PostAdapter

    private lateinit var bindingDialog: CustomeDialogAddEditPostBinding

    // this dagger hilt get automatic instance from homeViewModel
    private val homeViewModel: HomeViewModel by viewModels()

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                bindingDialog.layoutSelectPhotoTemp.visibility = View.GONE
                bindingDialog.ivPostPhoto.visibility = View.VISIBLE
                bindingDialog.ivPostPhoto.setImageURI(it)
                bindingDialog.ivPostPhoto.tag = it
                bindingDialog.tvEditPost.visibility = View.VISIBLE
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomePostBinding.inflate(inflater, container, false)
        mNavController = findNavController()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvAddPost.setOnClickListener {
            showCreatePostDialog()
        }

        homeViewModel.getAllPosts()
        observeAllPostsLiveData()

    }

    private fun observeAddPostLiveData(success: (Post) -> Unit) {
        homeViewModel.addPostLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarHome.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    binding.progressBarHome.visibility = View.GONE
                    val post = resource.data

                    success(post)

                    val currentList = postAdapter.currentList.toMutableList()
                    currentList.add(0, post)
                    postAdapter.submitList(currentList)
                }

                is Resource.Failure -> {
                    binding.progressBarHome.visibility = View.GONE
                    Log.e("HomePostFragment", "observeAddPostLiveData: ${resource.errorMessage}")
                }
            }
        }
    }

    private fun observeAllPostsLiveData() {
        homeViewModel.getPostsLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarHome.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    binding.progressBarHome.visibility = View.GONE
                    val listPosts = resource.data

                    Toast.makeText(
                        requireContext(),
                        "list size: ${listPosts.size}",
                        Toast.LENGTH_SHORT
                    ).show()

                    prepareRecyclerViewPosts(listPosts)
                }

                is Resource.Failure -> {
                    binding.progressBarHome.visibility = View.GONE
                    Log.e("HomePostFragment", "observeAllPostsLiveData: ${resource.errorMessage}")
                }
            }
        }
    }

    private fun prepareRecyclerViewPosts(listPosts: List<Post>) {
        postAdapter = PostAdapter(this)
        postAdapter.submitList(listPosts)

        binding.rvPosts.adapter = postAdapter
    }

    private fun showCreatePostDialog() {
        bindingDialog = CustomeDialogAddEditPostBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(bindingDialog.root)
            .create()


        bindingDialog.ivCloseDialog.setOnClickListener {
            dialog.dismiss()
        }

        // select photo from gallery
        bindingDialog.layoutSelectPhoto.setOnClickListener {
            pickImageFromGallery()
        }


        // add post
        bindingDialog.btnPost.setOnClickListener {
            val title = bindingDialog.etPostTitle.text.toString()
            val content = bindingDialog.etPostMessage.text.toString()
            val imageUri = bindingDialog.ivPostPhoto.tag as? Uri

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT)
                    .show()
            } else {
                homeViewModel.addPost(title, content, imageUri)

                observeAddPostLiveData {
                    Toast.makeText(requireContext(), "Post added successfully", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            dialog.dismiss()
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

    override fun onClickPost(post: Post) {
        val action = HomePostFragmentDirections.actionHomePostFragmentToPostDetailsFragment(post.id)
        mNavController.navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}