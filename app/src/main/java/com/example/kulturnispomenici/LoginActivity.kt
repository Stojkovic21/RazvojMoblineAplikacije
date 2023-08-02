package com.example.kulturnispomenici

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.kulturnispomenici.databinding.ActivityLoginBinding
import com.example.kulturnispomenici.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding;
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater);
        setContentView(binding.root);

        binding.btnSignup.setOnClickListener{
            val intent= Intent(this,SignupActivity::class.java);
            startActivity(intent);
        }

        firebaseAuth=FirebaseAuth.getInstance();
            var bul=true
        binding.btnLogin.setOnClickListener{
            if (bul) {
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
            }else{
                binding.ProgressBar.visibility= View.VISIBLE;
                loginUser(binding.tiEmail.text.toString(),binding.tiPassword.text.toString());
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){ task->
                if(task.isSuccessful)
                {
                    Toast.makeText(this,"login success",Toast.LENGTH_SHORT).show();
                    //val intetnt= Intent(this,UserProfile::class.java)
//                          startActivity(intent);
                }
                else{
                    Toast.makeText(this,"Fail to login", Toast.LENGTH_SHORT).show();
                }
                binding.ProgressBar.visibility=View.GONE;
            }
    }
}