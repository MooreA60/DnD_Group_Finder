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
import java.math.BigInteger
import java.security.MessageDigest

private const val TAG = "post_item"

class PostAdapter (val context: Context, val posts: List<Post>) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    inner class  ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // declare and look up all your view references at construction time
        val textViewUsername = itemView.findViewById<TextView>(R.id.textViewUsername)
        val textViewDescription = itemView.findViewById<TextView>(R.id.textViewDescription)
        val imageViewPostImage = itemView.findViewById<ImageView>(R.id.imageViewPostImage)
        val textViewPostTime = itemView.findViewById<TextView>(R.id.textViewPostTime)
        val ivProfImg = itemView.findViewById<ImageView>(R.id.ivProfImg)
        val textViewTitle = itemView.findViewById<TextView>(R.id.textViewTitle)
        val textViewContact = itemView.findViewById<TextView>(R.id.textViewContact)

        fun bind(post: Post) {
            val username = post.user?.username as String
            textViewUsername.text = username
            textViewDescription.text = post.description
            textViewTitle.text = post.game_title
            textViewContact.text = post.contact
            Glide.with(context).load(post.imageUrl).into(imageViewPostImage)
            Glide.with(context).load(getProfImgUrl(username)).into(ivProfImg)
            textViewPostTime.text = DateUtils.getRelativeTimeSpanString(post.creationTimeMs)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false)
        return ViewHolder(view)
    }
    
    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    //While the user does not have the ability to upload a profile picture a unique profile image
    //will be provided for them, using a service called Gravatar (Globally Recognised avatar)
    private fun getProfImgUrl(username: String): String{
        val digest = MessageDigest.getInstance("MD5")
        val hash = digest.digest(username.toByteArray())
        val bigInt = BigInteger(hash)
        val hex = bigInt.abs().toString(16)
        return "https://www.gravatar.com/avatar/$hex?d=identicon"
    }

}