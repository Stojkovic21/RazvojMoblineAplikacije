package com.example.kulturnispomenici.Activitys

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.kulturnispomenici.R
import com.example.kulturnispomenici.databinding.ActivityDeleteProfileBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class DeleteProfileActivity : AppCompatActivity() {
    private lateinit var binding:ActivityDeleteProfileBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var currentPassword: EditText
    private lateinit var btnDeleteUser: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDeleteProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title="Delete profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        firebaseAuth= FirebaseAuth.getInstance()
        firebaseUser=firebaseAuth.currentUser!!
        progressBar=binding.ProgressBar
        currentPassword=binding.tiPassword
        btnDeleteUser=binding.btnChangePassword

        btnDeleteUser.setOnClickListener { view->
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

    private fun alertDialog() {
        val builder=AlertDialog.Builder(this)
        builder.setTitle("Brisanje profila")
        builder.setMessage("Da li ste siguri da zelite da obrisete profil")

        builder.setPositiveButton("Continut", DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
            deleteUser(firebaseUser)
        })
        builder.setNegativeButton("Cancle",null)

        val alertDialog=builder.create()

        alertDialog.show()
    }

    private fun deleteUser(firebaseUser: FirebaseUser) {
        firebaseUser.delete().addOnCompleteListener(OnCompleteListener<Void> {task ->
            if(task.isSuccessful){
                deleteUserData()
                firebaseAuth.signOut();
                Toast.makeText(this,"Profil uspesno obrisan",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,LoginActivity::class.java))
            }
        })
    }

    private fun deleteUserData() {
        val firebaseString=FirebaseStorage.getInstance()
        if(firebaseUser.photoUrl!=null) {
            val storageReference = firebaseString.getReferenceFromUrl(firebaseUser.photoUrl.toString())
            storageReference.delete().addOnCompleteListener(OnCompleteListener<Void>() {
                if (it.isSuccessful) {
                    Toast.makeText(this,"Korisnik uspesno obrisna",Toast.LENGTH_SHORT).show()
                } else {

                }
            })
        }
        val databaseReference=FirebaseDatabase.getInstance().getReference("Registrovan korisnik")
        databaseReference.child(firebaseUser.uid).removeValue().addOnSuccessListener(
            OnSuccessListener <Void>{

            })
    }

    private fun isInputTextEmpty(): Boolean {
        var bul=false
        if (TextUtils.isEmpty(binding.tiPassword.text)) {
            binding.tiPassword.error = "Potvrdite vasi lozinku";
            binding.tiPassword.requestFocus();
            bul=true;
        }
        if (!bul){
            return true
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.empty_manu,menu)
        return super.onCreateOptionsMenu(menu)
    }
}