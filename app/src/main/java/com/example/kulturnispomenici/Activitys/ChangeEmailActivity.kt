package com.example.kulturnispomenici.Activitys

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.kulturnispomenici.R
import com.example.kulturnispomenici.databinding.ActivityChangeEmailBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ChangeEmailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeEmailBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var newEmail:EditText
    private lateinit var oldEmail:EditText
    private lateinit var password:EditText
    private lateinit var btnChangeEmail:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityChangeEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title="Change email"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        progressBar=binding.ProgressBar
        newEmail=binding.tiNewEmail
        oldEmail=binding.tiOldEmail
        password=binding.tiPassword
        btnChangeEmail=binding.btnChangeEmail
        firebaseAuth=FirebaseAuth.getInstance()
        firebaseUser= firebaseAuth.currentUser!!

        btnChangeEmail.setOnClickListener { view->
            if(isInputTextEmpty()) run {
                progressBar.visibility=View.VISIBLE
                val credential: AuthCredential = EmailAuthProvider.getCredential(
                    oldEmail.text.toString(),
                    password.text.toString()
                )
                firebaseUser.reauthenticate(credential)
                    .addOnCompleteListener(OnCompleteListener<Void> {task->
                        if(task.isSuccessful) {
                            //Toast.makeText(this, "Uspesna verifikacija", Toast.LENGTH_SHORT).show()
                            alertDialog()
                            progressBar.visibility = View.GONE
                        }else{
                            progressBar.visibility=View.GONE
                            Toast.makeText(this,"Email ili lozinka nisu dobri",Toast.LENGTH_SHORT).show()
                        }
                    })

            }
        }

    }

    private fun changeEmail(firebaseUser: FirebaseUser) {
        firebaseUser.updateEmail(newEmail.text.toString()).addOnCompleteListener(OnCompleteListener<Void>{
            if(it.isComplete){
                Toast.makeText(this,"Uspesno promenjen email",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,UserProfileActivity::class.java))
            }
        })
    }

    private fun isInputTextEmpty():Boolean {
        var bul=false
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.tiNewEmail.text).matches()) {
            binding.tiNewEmail.error = "Unesite ispravnu email adresu";
            binding.tiPassword.requestFocus();
            bul=true;
        }
        if (TextUtils.isEmpty(binding.tiPassword.text)) {
            binding.tiPassword.error = "Polje mora biti popunjeno";
            binding.tiPassword.requestFocus();
            bul=true;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.tiOldEmail.text).matches()) {
            binding.tiOldEmail.error = "Unesite ispravnu email adresu";
            binding.tiOldEmail.requestFocus();
            bul=true;
        }
        if (!bul){
            return true
        }
        return false
    }
    private fun alertDialog() {
        val builder= AlertDialog.Builder(this)
        builder.setTitle("Promena emaila")
        builder.setMessage("Da li ste siguri da zelite da promenite email")

        builder.setPositiveButton("Continut", DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
            changeEmail(firebaseUser)
        })
        builder.setNegativeButton("Cancle",null)

        val alertDialog=builder.create()

        alertDialog.show()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.empty_manu,menu)
        return super.onCreateOptionsMenu(menu)
    }
}