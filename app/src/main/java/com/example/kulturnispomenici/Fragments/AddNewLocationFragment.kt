package com.example.kulturnispomenici.Fragments

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.kulturnispomenici.Data.User
import com.example.kulturnispomenici.Data.myPlace
import com.example.kulturnispomenici.Model.MyPlacesViewModel
import com.example.kulturnispomenici.R
import com.example.kulturnispomenici.databinding.FragmentAddNewLocationBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.Date

class AddNewLocationFragment : Fragment() {
    private var _binding: FragmentAddNewLocationBinding? = null
    private val binding get() = _binding!!
    private lateinit var btnAddNewLocation: Button
    private lateinit var view: View
    private lateinit var storageReference: StorageReference
    private lateinit var etTitle:EditText
    private lateinit var etDescriprion:EditText
    private lateinit var etLan:EditText
    private lateinit var etLng:EditText
    private lateinit var btnSet:Button
    private lateinit var btnCansel:Button
    private lateinit var imgAddPhoto:ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var uriImage:Uri
    private lateinit var bitMap:Bitmap
    private lateinit var newLocationDBReference:DatabaseReference
    private lateinit var addLocation:myPlace
    private lateinit var firebaseUser: FirebaseUser
    private final var PICK_IMAGE_REQUEST=2
    private val myPlacesViewModel: MyPlacesViewModel by activityViewModels()
    private val onePlaceViewModel:MyPlacesViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view= inflater.inflate(R.layout.fragment_add_new_location, container, false)
        etLan= view.findViewById(R.id.etLatitude)
        etLng=view.findViewById(R.id.etLongitude)
        btnSet=view.findViewById(R.id.btnSet)
        btnCansel=view.findViewById(R.id.btnCansle)
        btnAddNewLocation=view.findViewById(R.id.btnAdd)
        etTitle=view.findViewById(R.id.etTitel)
        etDescriprion=view.findViewById(R.id.etDescription)
        progressBar=view.findViewById(R.id.progressBar)
        imgAddPhoto=view.findViewById(R.id.imgAddPhoto)
        uriImage= Uri.EMPTY
        //bitMap=

        btnSet.setOnClickListener{ // uzimanje koordinata iz map fragmenta
            parentFragmentManager.setFragmentResultListener("lanLng",this, FragmentResultListener { requestKey: String, bundle: Bundle ->
                etLan.setText(bundle.getDouble("lan").toString())
                etLng.setText(bundle.getDouble("lng").toString())
            })
        }
        btnCansel.setOnClickListener {
            onePlaceViewModel.myPlacesList.clear()
            findNavController().navigate(R.id.action_addNewLocationFragment_to_mapFragment)
        }

        btnAddNewLocation.setOnClickListener {

            if (!isEditTextEmpty()) {
                progressBar.visibility = View.VISIBLE
                firebaseAuth = FirebaseAuth.getInstance()
                firebaseUser = firebaseAuth.currentUser!!
                newLocationDBReference =FirebaseDatabase.getInstance().getReference("Dodate lokacije")
                val date = getTodayDate()
                val userKey = newLocationDBReference.push().key
                var username: String = ""
                uploadPhoto(userKey.toString())
                val databaseReference =FirebaseDatabase.getInstance().getReference("Registrovan korisnik"); //Uzima username iz baze
                databaseReference.child(firebaseUser.uid).get().addOnSuccessListener {

                    if (it.exists()) {
                        val user = it.getValue(User::class.java)
                        if (user != null) {
                            username = user.usename
                        }
                        progressBar.visibility = View.GONE
                        addLocation = myPlace(
                            userKey!!,
                            etTitle.text.toString(),
                            etDescriprion.text.toString(),
                            username,
                            etLan.text.toString().toDouble(),
                            etLng.text.toString().toDouble(),
                            getTodayDate(),
                            userKey.toString()+".jpg"
                        )
                        myPlacesViewModel.addPlace(myPlace())

                        newLocationDBReference.child(addLocation.id).get()
                            .addOnCompleteListener { task ->
                                val dataSnapshot = task.result
                                newLocationDBReference.child(addLocation.id).setValue(addLocation)
                                    .addOnSuccessListener {
                                        onePlaceViewModel.myPlacesList.clear()
                                        findNavController().navigate(R.id.action_addNewLocationFragment_to_mapFragment)
                                        Toast.makeText(context,"Uspesno dodata nova lokacija",Toast.LENGTH_LONG).show()
                                    }
                                    .addOnFailureListener {Toast.makeText(context,"Bezuspesno dodavanje lutalice, pokusajte ponovo",Toast.LENGTH_LONG).show()
                                    }
                            }
                    }
                }
                //dodavanje u storage
            }
        }
        imgAddPhoto.setOnClickListener {
            openFileChooser()
        }
        return view
    }
    private fun uploadPhoto(pictureName:String) {
        storageReference= FirebaseStorage.getInstance().getReference("SlikeSpomenika")

        val fileReference : StorageReference =storageReference.child(pictureName + ".jpg")

        fileReference.putFile(uriImage).addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> {
            fileReference.downloadUrl.addOnSuccessListener(OnSuccessListener<Uri>{
                val downloadUri:Uri=uriImage
                firebaseUser= firebaseAuth.currentUser!!

                val profileUpdates: UserProfileChangeRequest =
                    UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build()
                firebaseUser.updateProfile(profileUpdates)
            })
        })
    }
    private fun isEditTextEmpty(): Boolean {
        var bul:Boolean=false
        if (TextUtils.isEmpty(etTitle.text)) {
            //Toast.makeText(this, "Morate uneti ime", Toast.LENGTH_SHORT).show();
            etTitle.error = "Morate uneti naziv lokacije";
            etTitle.requestFocus();
            bul=true;
        }
        if (TextUtils.isEmpty(etDescriprion.text)) {
            //Toast.makeText(this, "Morate uneti ime", Toast.LENGTH_SHORT).show();
            etDescriprion.error = "Morate uneti opis lokacije"
            etDescriprion.requestFocus();
            bul=true;
        }
        if (TextUtils.isEmpty(etLan.text)) {
            //Toast.makeText(this, "Morate uneti ime", Toast.LENGTH_SHORT).show();
            etLan.error = "Polje mora biti popunjeno.To mozete uciniti klikom na dugme set"
            etLan.requestFocus();
            bul=true;
        }
        if (TextUtils.isEmpty(etLng.text)) {
            //Toast.makeText(this, "Morate uneti ime", Toast.LENGTH_SHORT).show();
            etLng.error = "Polje mora biti popunjeno.To mozete uciniti klikom na dugme set"
            etLng.requestFocus();
            bul=true;
        }
        return bul
    }
    private fun openFileChooser()
    {
        if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }
        else{
            var intent: Intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent,PICK_IMAGE_REQUEST)
        }
    }
    private fun getTodayDate(): Date {
        return Date()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.size>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            val intent: Intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent,PICK_IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            uriImage= data.data!!
            if(Build.VERSION.SDK_INT>=28){
                val source=ImageDecoder.createSource(requireActivity().contentResolver,uriImage)
                bitMap=ImageDecoder.decodeBitmap(source)
                imgAddPhoto.setImageBitmap(bitMap)
            }
        }
    }
}