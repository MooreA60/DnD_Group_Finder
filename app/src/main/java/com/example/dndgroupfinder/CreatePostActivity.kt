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
import com.example.dndgroupfinder.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.sign

private const val TAG = "createpost_activity"
private const val PICK_PHOTO_CODE = 1234
class CreatePostActivity : ComponentActivity() {

    private var signedInUser : User? = null
    private var it: Uri? = null
    private lateinit var firestoreDatabase: FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.createpost_activity)

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
        val uploadImageView= findViewById<ImageView>(R.id.uploadImageView)
        val btnSubmitPost= findViewById<Button>(R.id.btnSubmitPost)






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
        //Is there a photo?
        if (it == null){
            Toast.makeText(this, "No photo has been selected", Toast.LENGTH_SHORT).show()
            return
        }
        //Is there a description?
        val postDescription= findViewById<EditText>(R.id.postDescription)
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

        //The following actions must take place in order to successfully complete
        //Upload image selected to Firebase Storage
        //Retrieve image url of said image
        //Create the post with the image url and add it to the collection "post"
    }

}




