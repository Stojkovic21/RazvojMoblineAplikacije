package com.example.kulturnispomenici.Activitys

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import java.io.File

@Suppress("DEPRECATION")
class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding:ActivityEditProfileBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var uploadPicture: ImageView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storageReference: StorageReference
    private lateinit var firebaseUser: FirebaseUser
    private final var PICK_IMAGE_REQUEST=1
    private lateinit var uriImage:Uri
    private lateinit var bitMap:Bitmap
    private final val CAMERA_REQUEST_CODE=2
    private lateinit var etName:EditText;private lateinit var etUsername:EditText;private lateinit var etPhone:EditText;private lateinit var etDateOB:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title="Edit profile"
        supportActionBar?.setDisplayShowHomeEnabled(true);

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

        val storegeReference= FirebaseStorage.getInstance().reference.child("ProfilePictureStorage/${firebaseUser.uid}.jpg")

        val localFile= File.createTempFile("tempImage","jpg")

        storegeReference.getFile(localFile).addOnSuccessListener { it->
            val bitmap= BitmapFactory.decodeFile(localFile.absolutePath)
            uploadPicture.setImageBitmap(bitmap)
        }

        binding.txtEditProfilePicture.setOnClickListener {view->
            openFileChooser()
            //cameraPermission()
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
    private fun createUri():Uri{
        var imageFile=File(applicationContext.filesDir,"camera_photo.jpg")

        return FileProvider.getUriForFile(
            applicationContext,"com.example.camerapermission.fileProvider",imageFile
        )
    }
    private fun cameraPermission(){
        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),CAMERA_REQUEST_CODE)
        }
        else{
            val intent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,CAMERA_REQUEST_CODE)
        }
    }
    private fun openFileChooser()
    {
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),CAMERA_REQUEST_CODE)
        }
        else{
            var intent: Intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent,PICK_IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            uriImage= data.data!!
            if(Build.VERSION.SDK_INT>=28){
                val source= ImageDecoder.createSource(this.contentResolver,uriImage)
                bitMap= ImageDecoder.decodeBitmap(source)
                uploadPicture.setImageBitmap(bitMap)
            }
        }else if (requestCode==CAMERA_REQUEST_CODE&&resultCode==Activity.RESULT_OK &&data != null){
                uriImage= data.data!!
            if(Build.VERSION.SDK_INT>=28){
                val source= ImageDecoder.createSource(this.contentResolver,uriImage)
                bitMap= ImageDecoder.decodeBitmap(source)
                uploadPicture.setImageBitmap(bitMap)
            }
        }else{
            Toast.makeText(this,"Something gone wrong",Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_profile_manu,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.size>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
            val intent: Intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent,PICK_IMAGE_REQUEST)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.Save->{
                progressBar.visibility=View.VISIBLE
                Log.d(TAG, "uploadPhoto Uri: "+uriImage)
                uploadPhoto()
                if(updateProfile(firebaseUser)) {
                    Toast.makeText(this, "Changes succeed", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, UserProfileActivity::class.java));
                }
                progressBar.visibility=View.GONE
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
            val writeUser=User(ime,prezime ,etUsername.text.toString(),etDateOB.text.toString(),etPhone.text.toString())

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
        storageReference=FirebaseStorage.getInstance().getReference("ProfilePictureStorage")

        val fileReference : StorageReference=storageReference.child(firebaseAuth.currentUser?.uid.toString() + "."+getFileExtension(uriImage))
        Log.d(TAG, "uploadPhoto Uri: "+uriImage)
        fileReference.putFile(uriImage).addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> {
            fileReference.downloadUrl.addOnSuccessListener(OnSuccessListener<Uri>{
                val downloadUri:Uri=uriImage
                firebaseUser= firebaseAuth.currentUser!!

                val profileUpdates: UserProfileChangeRequest =UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build()
                firebaseUser.updateProfile(profileUpdates)
            })
        })
    }

    private fun getFileExtension(uriImage: Uri):String {
        val cR:ContentResolver=contentResolver
        val mime:MimeTypeMap=MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uriImage)).toString()
    }
}