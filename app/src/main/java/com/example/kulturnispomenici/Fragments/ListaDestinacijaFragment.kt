package com.example.kulturnispomenici.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.viewModels
import com.example.kulturnispomenici.Model.MyPlacesViewModel
import com.example.kulturnispomenici.R
import com.example.kulturnispomenici.databinding.FragmentListaDestinacijaBinding

class ListaDestinacijaFragment : Fragment() {
    private lateinit var _binding:FragmentListaDestinacijaBinding
    private val binding get() = _binding
    private val myPlacesViewModel:MyPlacesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentListaDestinacijaBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myPlacesViewModel.myPlacesList.add("Decak fudbaler")
        myPlacesViewModel.myPlacesList.add("Toma Zdravkovic")
        myPlacesViewModel.myPlacesList.add("Mija Stanimirovic")

        val myPlacesList:ListView=requireView().findViewById(R.id.MyPlacesList)
        myPlacesList.adapter=ArrayAdapter<String>(view.context,android.R.layout.simple_list_item_1,myPlacesViewModel.myPlacesList)
    }
}