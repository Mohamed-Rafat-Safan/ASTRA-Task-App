package com.example.astrataskapp.presentation.details

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.astrataskapp.domain.model.Post
import com.example.astrataskapp.domain.use_case.PostUseCase
import com.example.astrataskapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PostDetailsViewModel @Inject constructor(
    private val postUseCase: PostUseCase,
) : ViewModel() {

    private val _getOnePostsLiveData = MutableLiveData<Resource<Post>>()
    val getOnePostsLiveData: LiveData<Resource<Post>> get() = _getOnePostsLiveData

    private val _updatePostLiveData = MutableLiveData<Resource<Post>>()
    val updatePostLiveData: LiveData<Resource<Post>> get() = _updatePostLiveData

    private val _deletePostLiveData = MutableLiveData<Resource<String>>()
    val deletePostLiveData: LiveData<Resource<String>> get() = _deletePostLiveData


    fun getPostsById(id: Int) {
        viewModelScope.launch {
            _getOnePostsLiveData.value = Resource.Loading

            try {
                val post = postUseCase.getPostById(id)
                _getOnePostsLiveData.value = Resource.Success(post)

            } catch (t: Throwable) {
                when (t) {
                    is IOException -> _getOnePostsLiveData.value =
                        Resource.Failure("Network failure")

                    else -> _getOnePostsLiveData.value = Resource.Failure("Conversion Error")
                }
            }
        }
    }


    fun updatePost(postId: Int, title: String, content: String, imageUri: Uri?) {
        viewModelScope.launch {
            _updatePostLiveData.value = Resource.Loading

            try {
                val updatedPost = postUseCase.updatePost(postId, title, content, imageUri)
                _updatePostLiveData.value = Resource.Success(updatedPost)

            } catch (t: Throwable) {
                when (t) {
                    is IOException -> _updatePostLiveData.value =
                        Resource.Failure("Network failure")

                    else -> _updatePostLiveData.value = Resource.Failure("Conversion Error")
                }
            }
        }
    }


    fun deletePost(id: Int) {
        viewModelScope.launch {
            try {
                postUseCase.deletePost(id)

                _deletePostLiveData.value = Resource.Success("Post deleted successfully")

            } catch (t: Throwable) {
                when (t) {
                    is IOException -> _updatePostLiveData.value =
                        Resource.Failure("Network failure")

                    else -> _updatePostLiveData.value = Resource.Failure("Conversion Error")
                }
            }
        }
    }

}