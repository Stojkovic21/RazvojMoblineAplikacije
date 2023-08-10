package com.example.kulturnispomenici.Activitys

import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.example.kulturnispomenici.Data.User
import com.example.kulturnispomenici.R
import com.example.kulturnispomenici.databinding.ActivityEditProfileBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding:ActivityEditProfileBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var uploadPicture: ImageView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storageReference: StorageReference
    private lateinit var firebaseUser: FirebaseUser
    private final var PICK_IMAGE_REQUEST=1
    private lateinit var uriImage:Uri
    private lateinit var etName:EditText;private lateinit var etUsername:EditText;private lateinit var etPhone:EditText;private lateinit var etDateOB:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title="Edit profile"

        etName=binding.etName
        etUsername=binding.etUsername
        etPhone=binding.etPhone
        etDateOB=binding.etDateOB


        progressBar=binding.ProgressBar
        uploadPicture=binding.UploadProfilePicture
        firebaseAuth=FirebaseAuth.getInstance()
        firebaseUser= firebaseAuth.currentUser!!
        uriImage= Uri.EMPTY

        showProfile(firebaseUser)

        binding.etDateOB.setOnClickListener{
            val textToSplit=etDateOB.text.toString().split("/")
            val day= textToSplit[0].toInt()
            val month=textToSplit[1].toInt()
            val year=textToSplit[2].toInt()

            DatePickerDialog(this,DatePickerDialog.OnDateSetListener{view, year, month, dayOfMonth ->
                binding.etDateOB.setText("$dayOfMonth/"+ (month+1)+"/$year");
            },year,month,day).show()
        }

        val uri:Uri? = firebaseUser.photoUrl

        Picasso.with(this).load(uri).into(uploadPicture)

        binding.txtEditProfilePicture.setOnClickListener {view->
            openFileChooser()
        }
    }

    private fun showProfile(firebaseUser: FirebaseUser) {
        val userId=firebaseUser.uid
        var databaseReference:DatabaseReference = FirebaseDatabase . getInstance ().getReference( "Registrovan korisnik");
        progressBar.visibility=View.VISIBLE
        databaseReference.child(userId).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user=snapshot.getValue(User::class.java)
                if (user!=null){
                    etName.setText(user.ime+" "+user.prezime)
                    etUsername.setText(user.usename)
                    etPhone.setText(user.brTelefona)
                    etDateOB.setText(user.datumRodjenja)

                }
                progressBar.visibility=View.GONE

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun openFileChooser()
    {
        var intent:Intent=Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent,PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==PICK_IMAGE_REQUEST&&resultCode== RESULT_OK&&data!=null&&data.data !=null){
            uriImage= data.data!!
            uploadPicture.setImageURI(uriImage)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_profile_manu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.Save->{
                uploadPhoto()
                if(updateProfile(firebaseUser)) {
                    Toast.makeText(this, "Changes succeed", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, UserProfileActivity::class.java));
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateProfile(firebaseUser: FirebaseUser):Boolean {
        val imeIPrezime=etName.text.split(" ")
        val ime=imeIPrezime[0]
        val prezime=imeIPrezime[1]
        var bul=false;

        if (TextUtils.isEmpty(etUsername.text)) {
            //Toast.makeText(this, "Morate uneti ime", Toast.LENGTH_SHORT).show();
            etUsername.error = "Morate uneti "+etUsername.hint.toString();
            etUsername.requestFocus();
            bul=true;
        }
        if (TextUtils.isEmpty(etDateOB.text)) {
            //Toast.makeText(this, "Morate uneti ime", Toast.LENGTH_SHORT).show();
            etDateOB.error = "Morate uneti "+etDateOB.hint.toString();
            etDateOB.requestFocus();
            bul=true;
        }
        if (TextUtils.isEmpty(etPhone.text)) {
            //Toast.makeText(this, "Morate uneti ime", Toast.LENGTH_SHORT).show();
            etPhone.error = "Morate uneti "+etPhone.hint.toString();
            etPhone.requestFocus();
            bul=true;
        }
        if (TextUtils.isEmpty(ime) || TextUtils.isEmpty(prezime)) {
            //Toast.makeText(this, "Morate uneti ime", Toast.LENGTH_SHORT).show();
            etName.error = "Morate uneti ime i prezime"
            etName.requestFocus();
            bul=true;
        }
        if(!bul) {
            progressBar.visibility = View.VISIBLE;
            val writeUser=User(ime,prezime, etUsername.text.toString(),etDateOB.text.toString(),etPhone.text.toString())

            val databaseReference=FirebaseDatabase.getInstance().getReference(("Registrovan korisnik"))
            val userId=firebaseUser.uid
            databaseReference.child(firebaseUser.uid).setValue(writeUser).addOnCompleteListener(this, OnCompleteListener<Void> { task->
                if(task.isSuccessful){
                    startActivity(Intent(this, UserProfileActivity::class.java));
                    finish()
                }else{
                    Toast.makeText(this,"Fail to create acc",Toast.LENGTH_SHORT).show();
                    progressBar.visibility = View.GONE;
                }
            })
            progressBar.visibility=View.GONE
            return true
        }
        return false
    }

    private fun uploadPhoto() {
        progressBar.visibility=View.VISIBLE
        storageReference=FirebaseStorage.getInstance().getReference("ProfilePictureStorage")

        val fileReference : StorageReference=storageReference.child(firebaseAuth.currentUser?.uid.toString() + "."+getFileExtension(uriImage))

        fileReference.putFile(uriImage).addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> {
            fileReference.downloadUrl.addOnSuccessListener(OnSuccessListener<Uri>{
                val downloadUri:Uri=uriImage
                firebaseUser= firebaseAuth.currentUser!!

                val profileUpdates: UserProfileChangeRequest =UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build()
                firebaseUser.updateProfile(profileUpdates)

            })
            progressBar.visibility=View.GONE
        })
    }

    private fun getFileExtension(uriImage: Uri):String {
        val cR:ContentResolver=contentResolver
        val mime:MimeTypeMap=MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uriImage)).toString()
    }
}