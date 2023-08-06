package com.example.kulturnispomenici.Activitys

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.kulturnispomenici.R
import com.example.kulturnispomenici.databinding.ActivityChangePasswordBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var newPassword: EditText
    private lateinit var currentPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var btnChangePassword: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title="Change password"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        progressBar=binding.ProgressBar
        newPassword=binding.tiNewPassword
        currentPassword=binding.tiCurrentPassword
        confirmPassword=binding.tiConfirmPassword
        btnChangePassword=binding.btnChangePassword
        firebaseAuth= FirebaseAuth.getInstance()
        firebaseUser= firebaseAuth.currentUser!!

        btnChangePassword.setOnClickListener { view->
            if(isInputTextEmpty()) run {
                progressBar.visibility= View.VISIBLE
                val credential: AuthCredential = EmailAuthProvider.getCredential(
                    firebaseUser.email.toString(),
                    currentPassword.text.toString()
                )
                firebaseUser.reauthenticate(credential)
                    .addOnCompleteListener(OnCompleteListener<Void> { task->
                        if(task.isSuccessful) {
                            //Toast.makeText(this, "Uspesna verifikacija", Toast.LENGTH_SHORT).show()
                            alertDialog()
                            progressBar.visibility = View.GONE
                        }else{
                            progressBar.visibility= View.GONE
                            Toast.makeText(this,"Trenutna lozinka nije dobra", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }

    }

    private fun changePassword(firebaseUser: FirebaseUser) {
        firebaseUser.updatePassword(newPassword.text.toString()).addOnCompleteListener(OnCompleteListener<Void>{
            if(it.isComplete){
                Toast.makeText(this,"Uspesno promenjena sifra", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,UserProfileActivity::class.java))
            }
        })
    }

    private fun isInputTextEmpty():Boolean {
        var bul=false
        if (TextUtils.isEmpty(binding.tiCurrentPassword.text)) {
            binding.tiCurrentPassword.error = "Polje mora biti popunjeno";
            binding.tiCurrentPassword.requestFocus();
            bul=true;
        }
        if (TextUtils.isEmpty(binding.tiNewPassword.text)) {
            binding.tiNewPassword.error = "Polje mora biti popunjeno";
            binding.tiNewPassword.requestFocus();
            bul=true;
        }
        if (binding.tiNewPassword.text.toString()!=binding.tiConfirmPassword.text.toString()) {
            binding.tiConfirmPassword.error = "Sifte moraju biti iste";
            binding.tiConfirmPassword.requestFocus();
            bul=true;
        }
        if (!bul){
            return true
        }
        return false
    }
    private fun alertDialog() {
        val builder= AlertDialog.Builder(this)
        builder.setTitle("Promena sifre")
        builder.setMessage("Da li ste siguri da zelite da promenite sifru")

        builder.setPositiveButton("Continut", DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
            changePassword(firebaseUser)
        })
        builder.setNegativeButton("Cancle",null)

        val alertDialog=builder.create()

        alertDialog.show()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.empty_manu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}