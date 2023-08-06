package com.example.kulturnispomenici.Activitys

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import com.example.kulturnispomenici.Classes.User
import com.example.kulturnispomenici.databinding.ActivitySignupBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar


class SignupActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var writeUser: User
    private lateinit var referenceProfile:DatabaseReference
    private lateinit var datePicker: DatePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignupBinding.inflate(layoutInflater);
        setContentView(binding.root);

        binding.tiDatumRodjenja.setOnClickListener{
            val calendar:Calendar=Calendar.getInstance();
            val day=calendar.get(Calendar.DAY_OF_MONTH);
            val month=calendar.get(Calendar.MONTH);
            val year=calendar.get(Calendar.YEAR);

            DatePickerDialog(this,DatePickerDialog.OnDateSetListener{view, year, month, dayOfMonth ->
                binding.tiDatumRodjenja.setText("$dayOfMonth/"+ (month+1)+"/$year");
            },year,month,day).show()
        }


            var bul:Boolean=true;
        binding.btnSignup.setOnClickListener {
            bul=false;
            if (binding.tiConfirmPassword.text==binding.tiPassword.text) {
                //Toast.makeText(this, "Morate uneti ime", Toast.LENGTH_SHORT).show();
                binding.tiConfirmPassword.error = "Sifre moraju biti iste";
                binding.tiConfirmPassword.requestFocus();
                bul=true;
            }
            if (TextUtils.isEmpty(binding.tiPassword.text)) {
                //Toast.makeText(this, "Morate uneti ime", Toast.LENGTH_SHORT).show();
                binding.tiPassword.error = "Morate uneti "+binding.tiPassword.hint.toString();
                binding.tiPassword.requestFocus();
                bul=true;
            }
            if (TextUtils.isEmpty(binding.tiUsername.text)) {
                //Toast.makeText(this, "Morate uneti ime", Toast.LENGTH_SHORT).show();
                binding.tiUsername.error = "Morate uneti "+binding.tiUsername.hint.toString();
                binding.tiUsername.requestFocus();
                bul=true;
            }
            if (TextUtils.isEmpty(binding.tiDatumRodjenja.text)) {
                //Toast.makeText(this, "Morate uneti ime", Toast.LENGTH_SHORT).show();
                binding.tiDatumRodjenja.error = "Morate uneti "+binding.tiDatumRodjenja.hint.toString();
                binding.tiDatumRodjenja.requestFocus();
                bul=true;
            }
            if (TextUtils.isEmpty(binding.tiBrojTelefona.text)) {
                //Toast.makeText(this, "Morate uneti ime", Toast.LENGTH_SHORT).show();
                binding.tiBrojTelefona.error = "Morate uneti "+binding.tiBrojTelefona.hint.toString();
                binding.tiBrojTelefona.requestFocus();
                bul=true;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(binding.tiEmail.text).matches()) {
                binding.tiEmail.error = "Morate uneti validnu email adresi";
                binding.tiEmail.requestFocus();
                bul=true;
            }
            if (TextUtils.isEmpty(binding.tiPrezime.text)) {
                //Toast.makeText(this, "Morate uneti ime", Toast.LENGTH_SHORT).show();
                binding.tiPrezime.error = "Morate uneti "+binding.tiPrezime.hint.toString();
                binding.tiPrezime.requestFocus();
                bul=true;
            }
            if (TextUtils.isEmpty(binding.tiIme.text)) {
                //Toast.makeText(this, "Morate uneti ime", Toast.LENGTH_SHORT).show();
                binding.tiIme.error = "Morate uneti "+binding.tiIme.hint.toString();
                binding.tiIme.requestFocus();
                bul=true
            }
            if(!bul) {
                bul=false;
                binding.ProgressBar.visibility = View.VISIBLE;
                registerUser(binding.tiIme.text.toString(),binding.tiPrezime.text.toString(),binding.tiEmail.text.toString(),binding.tiBrojTelefona.text.toString()
                    ,binding.tiDatumRodjenja.text.toString(),binding.tiUsername.text.toString(),binding.tiPassword.text.toString());
            }
        }
    }

    private fun registerUser(ime: String, prezime: String, email: String, brTel: String, datRodj: String, username: String, password: String){
        firebaseAuth=FirebaseAuth.getInstance();
        referenceProfile=FirebaseDatabase.getInstance().getReference("Registrovan korisnik");

        firebaseAuth.createUserWithEmailAndPassword(binding.tiEmail.text.toString(),binding.tiPassword.text.toString())
            .addOnCompleteListener(this, OnCompleteListener<AuthResult?> { task ->
                if (task.isSuccessful){
                    Toast.makeText(this,"Created success",Toast.LENGTH_SHORT).show();
                    firebaseUser= firebaseAuth.currentUser!!;
                    writeUser= User(binding.tiIme.text.toString(),binding.tiPrezime.text.toString(),binding.tiUsername.text.toString(),binding.tiDatumRodjenja.text.toString(),
                        binding.tiBrojTelefona.text.toString());

                    referenceProfile.child(firebaseUser.uid).setValue(writeUser).addOnCompleteListener(this, OnCompleteListener<Void> { Task->
                        if(Task.isSuccessful){
                            firebaseUser.sendEmailVerification();

                            startActivity(Intent(this, UserProfileActivity::class.java));
                            finish()
                        }else{
                            Toast.makeText(this,"Fail to create acc",Toast.LENGTH_SHORT).show();
                            binding.ProgressBar.visibility = View.GONE;
                        }
                    })
                } else{
                    Toast.makeText(this,"Fail to create acc",Toast.LENGTH_SHORT).show();
                    binding.ProgressBar.visibility = View.GONE;
                }
                    binding.ProgressBar.visibility = View.GONE;
            })
    }
}