package com.example.astrataskapp.presentation.home

import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.astrataskapp.domain.model.Post
import com.example.astrataskapp.domain.use_case.PostUseCase
import com.example.astrataskapp.utils.Resource
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postUseCase: PostUseCase,
    private val application: Application,
) : ViewModel() {

    private val _getPostsLiveData = MutableLiveData<Resource<List<Post>>>()
    val getPostsLiveData: LiveData<Resource<List<Post>>> get() = _getPostsLiveData

    private val _addPostLiveData = MutableLiveData<Resource<Post>>()
    val addPostLiveData: LiveData<Resource<Post>> get() = _addPostLiveData


    fun getAllPosts() {
        viewModelScope.launch {
            _getPostsLiveData.value = Resource.Loading

            try {
                val posts = postUseCase.getAllPosts()
                _getPostsLiveData.value = Resource.Success(posts)

            } catch (t: Throwable) {
                when (t) {
                    is IOException -> _getPostsLiveData.value =
                        Resource.Failure("Network failure")

                    else -> _getPostsLiveData.value = Resource.Failure("Conversion Error")
                }
            }
        }
    }


    fun addPost(title: String, content: String, imageUri: Uri?) {
        viewModelScope.launch {
            _addPostLiveData.value = Resource.Loading

            try {
                val returnPost = postUseCase.addPost(title, content, imageUri)
                _addPostLiveData.value = Resource.Success(returnPost)

            } catch (t: Throwable) {
                when (t) {
                    is IOException -> _addPostLiveData.value =
                        Resource.Failure("Network failure")

                    else -> _addPostLiveData.value = Resource.Failure("Conversion Error")
                }
            }
        }
    }

}