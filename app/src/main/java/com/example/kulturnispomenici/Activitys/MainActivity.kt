package com.example.kulturnispomenici.Activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kulturnispomenici.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding;
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root);

        binding.btnSignup.setOnClickListener{
            val intent= Intent(this, SignupActivity::class.java);
            startActivity(intent);
        }

        firebaseAuth=FirebaseAuth.getInstance();
        binding.btnLogin.setOnClickListener{


        }

    }
}