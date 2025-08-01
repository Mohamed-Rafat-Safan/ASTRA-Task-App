package com.example.astrataskapp.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.astrataskapp.databinding.ItemPostBinding
import com.example.astrataskapp.domain.model.Post


class PostAdapter(
    private val listener: PostOnClickListener,
) : ListAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemBinding =
            ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))


        holder.itemView.setOnClickListener {
            listener.onClickPost(getItem(position))
        }

    }

    inner class PostViewHolder(private val itemBinding: ItemPostBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(post: Post) {
            Glide.with(itemBinding.root.context).load(post.image)
                .into(itemBinding.ivPostImage)

            itemBinding.apply {
                tvPostTitle.text = post.title
                tvPostContent.text = post.content
            }

        }
    }

    private class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }

    interface PostOnClickListener {
        fun onClickPost(post: Post)
    }

}