package com.example.dndgroupfinder

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.dndgroupfinder.models.Post
import com.example.dndgroupfinder.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.math.sign

private const val TAG = "createpost_activity"
var photoUri : Uri? = null

private const val PICK_PHOTO_CODE = 1234
class CreatePostActivity : ComponentActivity() {

    private var signedInUser : User? = null
    //private var it : Uri? = null


    private lateinit var firestoreDatabase: FirebaseFirestore
    // Storage reference from our app to Firebase Storage
    private lateinit var storageRef: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.createpost_activity)
        val uploadImageView= findViewById<ImageView>(R.id.uploadImageView)
        val btnSubmitPost= findViewById<Button>(R.id.btnSubmitPost)
        storageRef = FirebaseStorage.getInstance().reference

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

        val btnSlctImg= findViewById<Button>(R.id.btnSlctImg)

        val getImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {

                uploadImageView.setImageURI(it)

            }
        )
        

        btnSlctImg.setOnClickListener {
            Log.i(TAG, "Open up image picker in user device")
            getImage.launch("image/*")

//            val imagePickerIntent = Intent(Intent.ACTION_GET_CONTENT)
//            imagePickerIntent.type = "image/*"
//            if (imagePickerIntent.resolveActivity(packageManager) != null){
                //onActivityResult(imagePickerIntent, PICK_PHOTO_CODE)
        }

        btnSubmitPost.setOnClickListener{
            handleSubmitPostClick()
        }

    }

    //Error checking before uploading a post
    private fun handleSubmitPostClick() {
        val uploadImageView= findViewById<ImageView>(R.id.uploadImageView)
        val btnSubmitPost= findViewById<Button>(R.id.btnSubmitPost)
        val postDescription= findViewById<EditText>(R.id.postDescription)

        //Is there a photo?
        if (photoUri == null){
            Toast.makeText(this, "No photo has been selected", Toast.LENGTH_SHORT).show()
            return
        }
        //Is there a description?
        if (postDescription.text.isBlank()){
            Toast.makeText(this, "No description has been selected", Toast.LENGTH_SHORT).show()
            return
        }
        //Is a user signed in? Realistically they shouldn't be able to get this far without
        //signing in but it is a good precaution to take
        if (signedInUser == null) {
            Toast.makeText(this, "No user is signed in, please sign in before attempting to post", Toast.LENGTH_SHORT).show()
            return
        }


        btnSubmitPost.isEnabled = false

        //The following actions must take place in order to successfully complete
        //1. Upload image selected to Firebase Storage
        val photoUploadUri = photoUri as Uri
        val photoRef = storageRef.child("images/${System.currentTimeMillis()}-photo.jpg") //This is where the photos live
        photoRef.putFile(photoUploadUri)
            .continueWithTask{photoUploadTask ->

                Log.i(TAG, "Number of bytes uploaded: ${photoUploadTask.result?.bytesTransferred}")
                //2. Retrieve image url of said image
                photoRef.downloadUrl
        }.continueWithTask{ downloadUrlTask ->
            //3. Create the post with the image url and add it to the collection "post"
                val post = Post(
                    postDescription.text.toString(),
                    downloadUrlTask.result.toString(),
                    System.currentTimeMillis(),
                    signedInUser)
                firestoreDatabase.collection("posts").add(post)
        }.addOnCompleteListener{ postCreateTask ->
            btnSubmitPost.isEnabled = true
            if (!postCreateTask.isSuccessful) {
                Log.e(TAG, "Exception during firebase operations", postCreateTask.exception)
                Toast.makeText(this,"Failed to save post", Toast.LENGTH_SHORT).show()
            }
            postDescription.text.clear()
                uploadImageView.setImageResource(0)
                Toast.makeText(this,"Post has been successfully uploaded!",Toast.LENGTH_SHORT).show()
                val profileIntent = Intent(this, UserProfileActivity::class.java)
                profileIntent.putExtra(EXTRA_USERNAME, signedInUser?.username)
                startActivity(profileIntent)
                finish()

        }


    }

}




