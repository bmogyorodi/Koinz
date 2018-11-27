package com.example.bence.koinz

import android.content.Context
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast.*
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode

class Map : AppCompatActivity(), PermissionsListener, LocationEngineListener,MapboxMap.OnMarkerClickListener {


    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private lateinit var permissionManager: PermissionsManager
    private lateinit var originLocation: Location
    private lateinit var pickupbutton:Button
    private lateinit var selmarker:Marker

    private  var locationEngine: LocationEngine? = null
    private var locationLayerPlugin: LocationLayerPlugin? = null

    private val coinzFile= "Coinzfile"
    private val tag="Mapview"
    private val markers=HashMap<Marker,Coinz>(50)
    private val coinindex=HashMap<String,Int>(50)
    private val wallet= Wallet()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        pickupbutton= findViewById(R.id.Pickupbutton)
        Mapbox.getInstance(applicationContext, getString(R.string.access_token))
        mapView =findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { mapboxMap ->
            map = mapboxMap
            Log.d(tag,"Map initialised!")
            for (i in 0..49)
            {
                val coin = getdailycoin(i)
                if(!coin.istaken()){
                val marker=(map.addMarker(MarkerOptions()
                        .position(LatLng(coin.getlat(), coin.getlong()))
                        .title("id: "+coin.getid()+"\ncurrency:"+coin.getcurrency()+"\nvalue:"+coin.getvalue().toString())))
                    markers[marker] = coin
                    coinindex[coin.getid()] = i

                }








            }
            map.setOnMarkerClickListener(this)
            Log.d(tag,"Markers placed on map.")
            pickupbutton.setOnClickListener(){_->
                pickupcoin(selmarker)
                pickupbutton.isEnabled=false

            }




            enableLocation()
        }



    }


        private fun getdailycoin(i: Int):Coinz{
        val setting =getSharedPreferences(coinzFile, Context.MODE_PRIVATE)
        val id=setting.getString("$i id","missingid")
        val value=setting.getFloat("$i value", 0.0F)
        val currency= setting.getString("$i currency","missingcurr")
        val markersym=setting.getInt("$i markersym",0)
        val markercolor=setting.getString("$i markercolor","000000")
        val longitude= setting.getFloat("$i longitude",0.0F)
        val latitude= setting.getFloat("$i latitude",0.0F)
         val taken =setting.getBoolean("$i taken",false)
        return Coinz(id,value,currency,markersym, markercolor, latitude, longitude,taken)
    }

    private fun enableLocation(){
        if (PermissionsManager.areLocationPermissionsGranted(this)){
            initializeLocationEngine()
            initializeLocationLayer()
            }
         else{
            permissionManager= PermissionsManager(this)
            permissionManager.requestLocationPermissions(this)
        }

    }
    @SuppressWarnings("MissingPermission")
    private fun initializeLocationEngine(){
        locationEngine= LocationEngineProvider(this).obtainBestLocationEngineAvailable()
        locationEngine?.priority= LocationEnginePriority.HIGH_ACCURACY
        locationEngine?.activate()

        val lastlocation =locationEngine?.lastLocation
        if (lastlocation != null){
            originLocation=lastlocation
            setCameraPosition(lastlocation)
        } else {
            locationEngine?.addLocationEngineListener(this)
        }
    }
    @SuppressWarnings("MissingPermission")
    private fun initializeLocationLayer(){
             locationLayerPlugin= LocationLayerPlugin(mapView,map,locationEngine)
             locationLayerPlugin?.setLocationLayerEnabled(true)
        locationLayerPlugin?.cameraMode=CameraMode.TRACKING
        locationLayerPlugin?.renderMode=RenderMode.NORMAL
    }
    private fun setCameraPosition(location: Location){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(location.latitude, location.longitude),13.0))

    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
         makeText(this,"You can't collect coinz without allowing access to your location!", LENGTH_SHORT)//Present a toast or dialogue on why they need to grant access
    }

    override fun onPermissionResult(granted: Boolean) {
         if (granted){enableLocation()}
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }



    override fun onLocationChanged(location: Location?) {
         location?.let{
             originLocation=location
             setCameraPosition(location)
         }
    }
    override fun onMarkerClick(marker: Marker): Boolean {

       Log.d(tag, "Markerclick registered")

        val selected=markers.getValue(marker)


        val distance=marker.position.distanceTo(LatLng(originLocation))
        if (distance<1000) //later change back to 25
        {
            val selcurr=selected.getcurrency()
            val value= selected.getvalue()
            pickupbutton.isEnabled=true
            pickupbutton.text=("Pick up $value in $selcurr currency")
            selmarker=marker




        }
        else{
            Log.d(tag,"Disatance is: $distance")
        }
        return true
    }

    @SuppressWarnings("MissingPermission")
    override fun onConnected() {
    locationEngine?.requestLocationUpdates()
    }
    @SuppressWarnings("MissingPermission")
    override fun onStart() {
        super.onStart()
        if(PermissionsManager.areLocationPermissionsGranted(this)){
            locationEngine?.requestLocationUpdates()
            locationLayerPlugin?.onStart()}
        mapView.onStart()

    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        locationEngine?.removeLocationUpdates()
        locationLayerPlugin?.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        locationEngine?.deactivate()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (outState != null)
        {mapView.onSaveInstanceState(outState)}
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
    private fun pickupcoin(marker: Marker){
        val coin=markers.getValue(marker)
        val index=coinindex.getValue(coin.getid())
        map.removeMarker(marker)
        val settings=getSharedPreferences(coinzFile,Context.MODE_PRIVATE)
        val editor=settings.edit()
        editor.putBoolean("$index taken",true)
        editor.apply()
        wallet.addCoin(coin)

    }



}

