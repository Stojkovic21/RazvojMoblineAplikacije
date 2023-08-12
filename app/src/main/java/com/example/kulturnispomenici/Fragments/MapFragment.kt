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
import android.view.Menu
import android.view.MenuInflater
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
import androidx.fragment.app.activityViewModels
import com.example.kulturnispomenici.Model.MyPlacesViewModel
import com.google.android.gms.maps.model.LatLng
import org.osmdroid.views.overlay.Marker
import com.google.android.gms.maps.model.MarkerOptions

@Suppress("DEPRECATION")
class MapFragment : Fragment() {
    private lateinit var map:MapView
    private lateinit var latLng:LatLng
    private lateinit var fusedLocationProviderClient:FusedLocationProviderClient
    private lateinit var imgProfile:ImageView
    private lateinit var imgMyLocation:ImageView
    private lateinit var imgPlaceList:ImageView
    private lateinit var imgList:ImageView
    private lateinit var imgShowAll:ImageView
    private lateinit var locationRequest:LocationRequest
    private lateinit var locationCallBack:LocationCallback
    private lateinit var currentLocation:Location
    private final var haveLocationPermissin:Boolean=false
    private final var LOCATION_PERMISSION_REQUEST_CODE=69
    private final var DEFAULT_ZOOM:Double=10.0
    private val myPlacesViewModel: MyPlacesViewModel by activityViewModels()
    private val onePlaceViewModel:MyPlacesViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getLocationPermision()
        //setHasOptionsMenu(true)

        locationRequest=LocationRequest()
        locationRequest.interval = 1000*3
        locationRequest.fastestInterval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        locationCallBack=object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

            }
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.empty_manu,menu)
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val view= inflater.inflate(R.layout.fragment_map, container, false )
        Log.d(TAG,"From onCreateView")


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        currentLocation=Location("trLokacija")
        map=view.findViewById<MapView>(R.id.map)
        map.setMultiTouchControls(true)
//        val myToolbar:Toolbar=requireActivity().findViewById(R.id.toolbar)
//        myToolbar.inflateMenu(R.menu.map_manu)
        Log.d(TAG,"From onViewCreate")

        imgProfile= view.findViewById(R.id.imgProfile)!!
        imgProfile.setOnClickListener{
            startActivity(Intent(activity,UserProfileActivity::class.java))
        }
        imgMyLocation= view.findViewById(R.id.imgMyLocation)!!
        imgMyLocation.setOnClickListener{
            startLocatioUpdating()

        }
        myPlacesViewModel.fetchPlace()

        map.controller.setZoom(4.0)
        val startPoint= GeoPoint(59.0588,-21.1080);
        map.controller.setCenter(startPoint);

        val ctx:Context?= activity?.getApplicationContext()
        org.osmdroid.config.Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences((ctx)))


        imgPlaceList= view.findViewById(R.id.imgPlacesList)!!
        imgPlaceList.setOnClickListener {//slanje koordinata u add Fragment
            //startLocatioUpdating()

            val lan:Double= currentLocation.latitude
            val lng:Double=currentLocation.longitude
            val data4Send =Bundle()

            data4Send.putDouble("lan",lan)
            data4Send.putDouble("lng",lng)
            parentFragmentManager.setFragmentResult("lanLng",data4Send)
            Log.d(TAG,"Ode on na ADD $lan $lng")
            findNavController().navigate(R.id.action_mapFragment_to_addNewLocationFragment)
        }
        imgList=view.findViewById(R.id.imgShowList)
        imgList.setOnClickListener {

            findNavController().navigate(R.id.action_mapFragment_to_listaDestinacijaFragment)
        }
        imgShowAll=view.findViewById(R.id.imgShowAll)
        imgShowAll.setOnClickListener {
            for (place in myPlacesViewModel.myPlacesList){
                val marker: Marker = Marker(map)
                val curLocarion: GeoPoint = GeoPoint(place.latitude,place.longitude)
                marker.position = curLocarion
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.title = place.title
                map.overlays.add(marker)
            }
        }
        //getGPSLocation()
        if(onePlaceViewModel.isNotEmpty()) {
            Log.d(TAG,onePlaceViewModel.myPlacesList[0].title+" "+onePlaceViewModel.myPlacesList[0].latitude+" From MapFragment")
            val curLocarion: GeoPoint = GeoPoint(onePlaceViewModel.myPlacesList[0].latitude,onePlaceViewModel.myPlacesList[0].longitude)
            val marker: Marker = Marker(map)
            marker.position = curLocarion
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = onePlaceViewModel.myPlacesList[0].title
            map.overlays.add(marker)
            moveCamera(LatLng(onePlaceViewModel.myPlacesList[0].latitude,onePlaceViewModel.myPlacesList[0].longitude),DEFAULT_ZOOM)
            currentLocation.latitude=onePlaceViewModel.myPlacesList[0].latitude
            currentLocation.longitude=onePlaceViewModel.myPlacesList[0].longitude
        }

    }
    private fun startLocatioUpdating() {
        Log.d(TAG,"STart location update ")
        if (ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallBack,null)
            getGPSLocation()
            Log.d(TAG,"End startLocation")
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

        Log.d(TAG,"Start getGPs")

        if(ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener(requireActivity(),
                OnSuccessListener<Location>{ location->
                    Log.d(TAG,"Start funsedLocation")
                moveCamera(LatLng(location.latitude,location.longitude),DEFAULT_ZOOM)
                currentLocation=location
                latLng= LatLng(currentLocation.latitude,currentLocation.longitude)
                    //Log.d(TAG,"End startLocation")
                    val curLocarion:GeoPoint= GeoPoint(currentLocation.latitude,currentLocation.longitude)
                    val marker:Marker=Marker(map)
                    marker.position=curLocarion
                    marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM)
                    map.overlays.add(marker)
            })
        }else{
            //if(Build.VERSION>=Build.VERSION_CODES.M)
            requestPermissions(arrayOf( android.Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_PERMISSION_REQUEST_CODE)
        }
        Log.d(TAG,"End getGps")
    }
    private fun moveCamera(location:LatLng,zoom:Double)
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
    @Deprecated("Deprecated in Java")
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
                    Toast.makeText(activity,"Ova aplikacija zahteva prihvatanje dozvolu da bi radila",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}