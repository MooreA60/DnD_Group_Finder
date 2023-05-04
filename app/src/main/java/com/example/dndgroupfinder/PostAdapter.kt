package com.example.dndgroupfinder

import android.content.Context
//import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dndgroupfinder.models.Post

private const val TAG = "post_item"

class PostAdapter (val context: Context, val posts: List<Post>) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false)
        return ViewHolder(view)
    }
    
    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    inner class  ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // declare and look up all your view references at construction time
        val textViewUsername = itemView.findViewById<TextView>(R.id.textViewUsername)
        val textViewDescription = itemView.findViewById<TextView>(R.id.textViewDescription)
        val imageViewPostImage = itemView.findViewById<ImageView>(R.id.imageViewPostImage)
        val textViewPostTime = itemView.findViewById<TextView>(R.id.textViewPostTime)
        fun bind(post: Post) {
            textViewUsername.text = post.user?.username
            textViewDescription.text = post.description
            Glide.with(context).load(post.imageUrl).into(imageViewPostImage)
            textViewPostTime.text = DateUtils.getRelativeTimeSpanString(post.creationTimeMs)
        }
    }
}