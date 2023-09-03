package com.example.kulturnispomenici.Activitys

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.kulturnispomenici.R
import com.example.kulturnispomenici.Data.User
import com.example.kulturnispomenici.databinding.ActivityUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class UserProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var txtName: TextView
    private lateinit var txtPhone: TextView
    private lateinit var txtUsername: TextView
    private lateinit var txtDate: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var profilePicture: ImageView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var txtEmail:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater);
        setContentView(binding.root);
        setSupportActionBar(binding.toolbar)

        supportActionBar?.title="User profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        txtName = binding.Name
        txtPhone = binding.Phone
        txtUsername = binding.Username
        txtDate = binding.Date
        progressBar = binding.ProgressBar
        txtEmail=binding.txtEmail
        profilePicture=binding.ProfilePicture

        firebaseAuth = FirebaseAuth.getInstance()
        var firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            Toast.makeText(this, "Users details are not available", Toast.LENGTH_SHORT).show()
        } else {
            progressBar.visibility = View.VISIBLE;
            Log.d(TAG,"Pre show User")
            showUserProfile(firebaseUser)
        }
    }
    @SuppressLint("SetTextI18n")
    private fun showUserProfile(firebaseUser: FirebaseUser) {
        val userId = firebaseUser.uid
        databaseReference = FirebaseDatabase . getInstance ().getReference( "Registrovan korisnik");
        databaseReference.child(userId).get().addOnSuccessListener {

            if(it.exists()){
                val user=it.getValue(User::class.java)
                txtEmail.text=firebaseUser.email
                txtPhone.text=user?.brTelefona.toString();
                txtUsername.text=user?.usename.toString()
                txtDate.text=user?.datumRodjenja.toString()
                txtName.text=user?.ime.toString()+" "+user?.prezime.toString();

                val storegeReference= FirebaseStorage.getInstance().reference.child("ProfilePictureStorage/$userId.jpg")

                val localFile= File.createTempFile("tempImage","jpg")

                storegeReference.getFile(localFile).addOnSuccessListener { it->
                    val bitmap=BitmapFactory.decodeFile(localFile.absolutePath)
                    profilePicture.setImageBitmap(bitmap)
                }

//                val uri:Uri?=firebaseUser.photoUrl;
//                Picasso.with(this).load(uri).into(imageView)

                progressBar.visibility=View.GONE

            }else{
                Toast.makeText(this, "Ne postoji " + userId, Toast.LENGTH_LONG).show()
                progressBar.visibility=View.GONE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.common_manu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.EditProfile -> {startActivity(Intent(this,EditProfileActivity::class.java))
            }
            R.id.ChangePassword -> {startActivity(Intent(this,ChangePasswordActivity::class.java))
            }
            R.id.ChangeEmail -> {startActivity(Intent(this,ChangeEmailActivity::class.java))
            }
            R.id.Delete -> {startActivity(Intent(this,DeleteProfileActivity::class.java))
            }
            R.id.SignOut -> {
                firebaseAuth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
//lukastojkovic1111@gmail.com
