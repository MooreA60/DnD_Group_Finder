package com.example.dndgroupfinder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth

private const val TAG = "UserProfileActivity"
class UserProfileActivity : PostFeedActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.userprofile_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.profile_logout) {
            Log.i(TAG, "User wishes to log out")
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, SignInActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}

