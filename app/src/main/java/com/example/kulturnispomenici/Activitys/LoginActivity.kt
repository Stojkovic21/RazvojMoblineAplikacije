package com.example.kulturnispomenici.Activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.Menu
import android.view.View
import android.widget.Toast
import com.example.kulturnispomenici.R
import com.example.kulturnispomenici.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity(){
    private lateinit var binding: ActivityLoginBinding;
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater);
        setContentView(binding.root);
        binding.btnSignup.setOnClickListener{
            val intent= Intent(this, SignupActivity::class.java);
            startActivity(intent);
        }

        firebaseAuth=FirebaseAuth.getInstance();
            var bul=true
        binding.btnLogin.setOnClickListener{
                bul=false;
                if (TextUtils.isEmpty(binding.tiPassword.text)) {
                    //Toast.makeText(this, "Morate uneti ime", Toast.LENGTH_SHORT).show();
                    binding.tiPassword.error = "Morate uneti " + binding.tiPassword.hint.toString();
                    binding.tiPassword.requestFocus();
                    bul = true;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(binding.tiEmail.text).matches()) {
                    binding.tiEmail.error = "Morate uneti validnu email adresi";
                    binding.tiEmail.requestFocus();
                    bul = true;
                }
            if (!bul) {
                binding.ProgressBar.visibility= View.VISIBLE;
                loginUser(binding.tiEmail.text.toString(),binding.tiPassword.text.toString());
            }
        }
        binding.btnForgotPassword.setOnClickListener{
            binding.ProgressBar.visibility=View.VISIBLE
            startActivity(Intent(this,ForgotPasswordActivity::class.java))
            binding.ProgressBar.visibility=View.GONE
        }
    }
    private fun loginUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){ task->
                if(task.isSuccessful){
                    Toast.makeText(this,"login success",Toast.LENGTH_SHORT).show();
                    val intent=Intent(this, UserProfileActivity::class.java)
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(this,"Fail to login", Toast.LENGTH_SHORT).show();
                }
                binding.ProgressBar.visibility=View.GONE;
            }
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser!=null){
            startActivity(Intent(this, UserProfileActivity::class.java))
            finish()
        }
    }
}