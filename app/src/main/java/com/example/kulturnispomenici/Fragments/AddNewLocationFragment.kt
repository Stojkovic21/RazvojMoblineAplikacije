package com.example.kulturnispomenici.Fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.kulturnispomenici.Data.User
import com.example.kulturnispomenici.Data.myPlace
import com.example.kulturnispomenici.Model.MyPlacesViewModel
import com.example.kulturnispomenici.R
import com.example.kulturnispomenici.databinding.FragmentAddNewLocationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Date

class AddNewLocationFragment : Fragment() {
    private var _binding: FragmentAddNewLocationBinding? = null
    private val binding get() = _binding!!
    private lateinit var btnAddNewLocation: Button
    private lateinit var view: View
    private lateinit var etTitle:EditText
    private lateinit var etDescriprion:EditText
    private lateinit var etLan:EditText
    private lateinit var etLng:EditText
    private lateinit var btnSet:Button
    private lateinit var btnCansel:Button
    private lateinit var progressBar: ProgressBar
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var newLocationDBReference:DatabaseReference
    private lateinit var addLocation:myPlace
    private lateinit var firebaseUser: FirebaseUser
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
                            getTodayDate()
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
            }
        }
        return view
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

    private fun getTodayDate(): Date {
        return Date()
    }
}