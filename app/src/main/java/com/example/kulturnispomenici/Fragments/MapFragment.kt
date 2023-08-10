package com.example.kulturnispomenici.Fragments

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.kulturnispomenici.Activitys.UserProfileActivity
import com.example.kulturnispomenici.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.model.LatLng

@Suppress("DEPRECATION")
class MapFragment : Fragment() {
    private lateinit var map:MapView
    private lateinit var fusedLocationProviderClient:FusedLocationProviderClient
    private lateinit var imgProfile:ImageView
    private lateinit var imgMyLocation:ImageView
    private lateinit var imgPlaceList:ImageView
    private lateinit var locationRequest:LocationRequest
    private lateinit var locationCallBack:LocationCallback
    private lateinit var currentLocation:Location
    private final var haveLocationPermissin:Boolean=false
    private final var LOCATION_PERMISSION_REQUEST_CODE=69
    private final var DEFAULT_ZOOM:Double=10.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getLocationPermision()

        locationRequest= LocationRequest()
        locationRequest.interval = 1000*3
        locationRequest.fastestInterval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        locationCallBack=object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        map=requireView().findViewById<MapView>(R.id.map)
        map.setMultiTouchControls(true)
//        val myToolbar:Toolbar=requireActivity().findViewById(R.id.toolbar)
//        myToolbar.inflateMenu(R.menu.map_manu)

        currentLocation=Location("trLokacija")

        imgProfile= view?.findViewById(R.id.imgProfile)!!
        imgProfile.setOnClickListener{
            startActivity(Intent(activity,UserProfileActivity::class.java))
        }
        imgMyLocation=view?.findViewById(R.id.imgMyLocation)!!
        imgMyLocation.setOnClickListener{
            startLocatioUpdating()
        }

        map.controller.setZoom(4.0)
        val startPoint= GeoPoint(59.0588,-21.1080);
        map.controller.setCenter(startPoint);

        var ctx:Context?= activity?.getApplicationContext()
        org.osmdroid.config.Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences((ctx)))



        imgPlaceList= view.findViewById(R.id.imgPlacesList)!!
        imgPlaceList.setOnClickListener {
            var lan:Double= currentLocation.latitude
            var lng:Double=currentLocation.longitude
            var data4Send =Bundle()

            data4Send.putDouble("lan",lan)
            data4Send.putDouble("lng",lng)
            parentFragmentManager.setFragmentResult("lanLng",data4Send)

            findNavController().navigate(R.id.action_mapFragment_to_addNewLocationFragment)
        }

        getGPSLocation()

    }
    private fun startLocatioUpdating() {
        Log.d(TAG,"STart location update ")
        if (ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallBack,null)
            getGPSLocation()
        }
    }
    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
    private fun getGPSLocation(){

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if(ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener(requireActivity(),
                OnSuccessListener<Location>{ location->
                moveCamera(location,DEFAULT_ZOOM)
                currentLocation=location
            })
        }else{
            //if(Build.VERSION>=Build.VERSION_CODES.M)
            requestPermissions(arrayOf( android.Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_PERMISSION_REQUEST_CODE)
        }
    }
    private fun moveCamera(location:Location,zoom:Double)
    {
        Log.d(TAG,"moveCamera: Lat: "+location.latitude+", Lng: "+location.longitude)
        map.controller.setCenter(GeoPoint(location.latitude,location.longitude))
        map.controller.setZoom(zoom)

    }

    private fun getLocationPermision()
    {
        val permission= arrayOf<String>(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION)

        if(ActivityCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            haveLocationPermissin=true
        }
        else{
            ActivityCompat.requestPermissions(requireActivity(),permission,LOCATION_PERMISSION_REQUEST_CODE)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG,"OnRequestPermissionResult: caled")
        when(requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG,"Permission granded")
                    haveLocationPermissin=true
                }
                else{
                    Toast.makeText(activity,"Ova aplikacija zahteva prihvatanje dozvolu da bi radila",Toast.LENGTH_SHORT)
                }
            }
        }
    }
}