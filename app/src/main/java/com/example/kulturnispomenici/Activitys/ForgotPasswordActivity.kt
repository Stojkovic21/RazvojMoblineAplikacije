package com.example.kulturnispomenici.Activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.kulturnispomenici.databinding.ActivityForgotPasswordBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding:ActivityForgotPasswordBinding
    private lateinit var confirmingEmail:EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var btnRestart:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater);
        setContentView(binding.root)
        actionBar?.title="Forgot password"

        confirmingEmail=binding.tiEmail;
        progressBar=binding.ProgressBar;
        btnRestart=binding.brnRestartPassword

        binding.brnRestartPassword.setOnClickListener {
            val email=confirmingEmail.text.toString();
            if (!TextUtils.isEmpty(email)){
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    progressBar.visibility=View.VISIBLE
                    restartPassword(email)
                }else{
                    confirmingEmail.error="Unesite validnu email adresu"                }
            }else{
                confirmingEmail.error="Nedostaje email"
            }
        }
    }

    private fun restartPassword(email:String) {
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(OnCompleteListener<Void>{
            if(it.isSuccessful){
                Toast.makeText(this,"Zahtev za promenu lozinke je poslat na vas mail",Toast.LENGTH_SHORT)
                startActivity(Intent(this,ForgotPasswordActivity::class.java))
            }
        })
        progressBar.visibility=View.GONE
    }
}