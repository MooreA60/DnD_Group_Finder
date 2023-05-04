package com.example.dndgroupfinder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
//import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dndgroupfinder.models.Post
import com.example.dndgroupfinder.models.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

private const val TAG = "PostFeedActivity"
const val EXTRA_USERNAME = "EXTRA_USERNAME"
open class PostFeedActivity : AppCompatActivity() {

    private var signedInUser : User? = null
    private lateinit var firestoreDatabase: FirebaseFirestore
    private lateinit var posts: MutableList<Post>
    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.postfeed_activity)

        //Layout file for one post -Done
        //create data source -Done
        posts = mutableListOf()
        //create adapter
        adapter = PostAdapter(this, posts)
        //bind adapter and layout manager to RV
        val recviewposts = findViewById<RecyclerView>(R.id.recviewposts)
        recviewposts.adapter = adapter
        recviewposts.layoutManager = LinearLayoutManager(this)
        firestoreDatabase = FirebaseFirestore.getInstance()



        //Request made to sign in user
        firestoreDatabase.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener {userSnapshot ->
                signedInUser = userSnapshot.toObject(User::class.java)
                Log.i(TAG, "signed in user: $signedInUser")
            }
            .addOnFailureListener{exception ->
                Log.i(TAG, "Failure fetching signed in user", exception)
            }


        var postFeedReference = firestoreDatabase
            .collection("posts")
            .limit(20)
            .orderBy("creation_time_ms", Query.Direction.DESCENDING)

        val username = intent.getStringExtra(EXTRA_USERNAME)
        if(username != null){
            //supportActionBar?.title = username-------------------------------------------------------------
            postFeedReference = postFeedReference.whereEqualTo("user.username", username)
        }

        postFeedReference.addSnapshotListener{snapshot, exception ->
            if (exception != null || snapshot == null) {
                Log.e(TAG, "Exception when querying posts", exception)
                return@addSnapshotListener
            }
            val postList = snapshot.toObjects(Post::class.java)
            posts.clear()
            posts.addAll(postList)
            adapter.notifyDataSetChanged()
            for (post in postList) {
                Log.i(TAG, "Post $post")
            }
        }
        val fabCreatePost= findViewById<FloatingActionButton>(R.id.fabCreatePost)
        fabCreatePost.setOnClickListener{
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //menuInflater.inflate(R.menu.postfeed_menu, menu)
        //return super.onCreateOptionsMenu(menu)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.postfeed_menu, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuToProfile) {
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra(EXTRA_USERNAME, signedInUser?.username)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}

