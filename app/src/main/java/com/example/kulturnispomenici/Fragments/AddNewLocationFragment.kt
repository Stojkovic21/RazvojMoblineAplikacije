package com.example.kulturnispomenici.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.FragmentResultListener
import androidx.navigation.fragment.findNavController
import com.example.kulturnispomenici.R

class AddNewLocationFragment : Fragment() {
    private lateinit var btnAdd: Button
    private lateinit var view: View
    private lateinit var etLan:EditText
    private lateinit var etLng:EditText
    private lateinit var btnSet:Button
    private lateinit var btnCansel:Button
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
        btnAdd=view.findViewById(R.id.btnAdd)

        btnSet.setOnClickListener{
            parentFragmentManager.setFragmentResultListener("lanLng",this, FragmentResultListener { requestKey: String, bundle: Bundle ->
                etLan.setText(bundle.getDouble("lan").toString())
                etLng.setText(bundle.getDouble("lng").toString())
            })
        }
        btnCansel.setOnClickListener {
            findNavController().navigate(R.id.action_addNewLocationFragment_to_mapFragment)
        }
        btnAdd.setOnClickListener {
            //Uzimanje podataka, stavljanje u klasu i slanje u bazu
        }

        return view
    }


}