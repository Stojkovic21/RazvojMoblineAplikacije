package com.example.kulturnispomenici.Fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.kulturnispomenici.Data.myPlace
import com.example.kulturnispomenici.Model.MyPlacesViewModel
import com.example.kulturnispomenici.R
import com.example.kulturnispomenici.databinding.FragmentListaDestinacijaBinding

class ListaDestinacijaFragment : Fragment() {
    private lateinit var _binding:FragmentListaDestinacijaBinding
    private val binding get() = _binding
    private val myPlacesViewModel:MyPlacesViewModel by activityViewModels()
    private val onePlaceViewModel:MyPlacesViewModel by activityViewModels()
    private lateinit var myPlaceListView:ListView

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
        myPlaceListView=binding.MyPlacesList
        myPlaceListView.adapter=ArrayAdapter<myPlace>(view.context,android.R.layout.simple_list_item_1,myPlacesViewModel.myPlacesList)

        myPlaceListView.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->

            val chosenMyPlace:myPlace=myPlacesViewModel.getItemOnPostion(position)
            onePlaceViewModel.addOne(chosenMyPlace)
            Log.d(TAG,onePlaceViewModel.myPlacesList[0].title+" "+onePlaceViewModel.myPlacesList[0].latitude+" From ListFragment")
            findNavController().navigate(R.id.action_listaDestinacijaFragment_to_mapFragment)
        })
    }

}