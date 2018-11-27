package com.example.bence.koinz

import android.content.Context
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
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
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode

class Map : AppCompatActivity(),OnMapReadyCallback, PermissionsListener, LocationEngineListener,MapboxMap.OnMarkerClickListener {


    private var mapView: MapView?=null
    private  var map: MapboxMap? =null
    private lateinit var permissionManager: PermissionsManager
    private lateinit var originLocation: Location
    private lateinit var pickupbutton:Button
    private lateinit var selmarker:Marker

    private  lateinit var locationEngine: LocationEngine
    private lateinit var locationLayerPlugin: LocationLayerPlugin

    private val coinzFile= "Coinzfile"
    private val tag="Mapview"
    private val markers=HashMap<Marker?,Coinz>(50)
    private val coinindex=HashMap<String,Int>(50)
    private val wallet= Wallet()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        pickupbutton= findViewById(R.id.Pickupbutton)
        Mapbox.getInstance(applicationContext, getString(R.string.access_token))
        mapView =findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
        pickupbutton.setOnClickListener{_->
            pickupcoin(selmarker)
            pickupbutton.isEnabled=false

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
            Log.d(tag,"Permissions are granted!")
            initializeLocationEngine()
            initializeLocationLayer()
            }
         else{
            Log.d(tag,"Permissions are not granted!")
            permissionManager= PermissionsManager(this)
            permissionManager.requestLocationPermissions(this)
        }

    }
    @SuppressWarnings("MissingPermission")
    private fun initializeLocationEngine(){
        locationEngine= LocationEngineProvider(this).obtainBestLocationEngineAvailable()
        locationEngine.fastestInterval=1000
        locationEngine.interval=5000
        locationEngine.priority= LocationEnginePriority.HIGH_ACCURACY
        locationEngine.activate()
        Log.d(tag,"Init location engine")

        val lastlocation =locationEngine.lastLocation
        if (lastlocation != null){
            originLocation=lastlocation
            setCameraPosition(lastlocation)
            Log.d(tag,"New location set")
        } else {
            locationEngine.addLocationEngineListener(this)
            Log.d(tag,"No new location!")
        }
    }
    @SuppressWarnings("MissingPermission")
    private fun initializeLocationLayer(){
        if(mapView==null){Log.d(tag,"mapView is null")}
        else{
            if(map==null){Log.d(tag,"map is null")}
            else{
                locationLayerPlugin= LocationLayerPlugin(mapView!!, map!!,locationEngine)
                locationLayerPlugin.setLocationLayerEnabled(true)
                locationLayerPlugin.cameraMode=CameraMode.TRACKING
                locationLayerPlugin.renderMode=RenderMode.NORMAL
                Log.d(tag,"Locationlayer init")
            }
        }

    }
    private fun setCameraPosition(location: Location){
        map?.animateCamera(CameraUpdateFactory.newLatLng(
                LatLng(location.latitude, location.longitude)))

    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
         Toast.makeText(this,"Permissions: $permissionsToExplain", LENGTH_SHORT)//Present a toast or dialogue on why they need to grant access
    }

    override fun onPermissionResult(granted: Boolean) {
        Log.d(tag,"[onPermissionResult] granted==$granted")
         if (granted){enableLocation()}
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }



    override fun onLocationChanged(location: Location?) {
         if(location==null)
         {Log.d(tag,"[onLocationChange] location is null")}
        else{
             originLocation=location
             setCameraPosition(originLocation)
         }

    }
    override fun onMarkerClick(marker: Marker): Boolean {

       Log.d(tag, "Markerclick registered")

        val selected=markers.getValue(marker)


        val distance=marker.position.distanceTo(LatLng(originLocation))
        if (distance<25) //later change back to 25
        {
            val selcurr=selected.getcurrency()
            val value= selected.getvalue()
            pickupbutton.isEnabled=true
            pickupbutton.text=("Pick up $value in $selcurr currency")
            selmarker=marker




        }
        else{
            Log.d(tag,"Disatance is: $distance")
            makeText(this,"Distance is $distance",Toast.LENGTH_SHORT)
        }
        return true
    }

    @SuppressWarnings("MissingPermission")
    override fun onConnected() {
        Log.d(tag,"[onConnected] requesting location updates")
        locationEngine.requestLocationUpdates()

    }
    @SuppressWarnings("MissingPermission")
    override fun onStart() {
        super.onStart()
        mapView?.onStart()

    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        locationEngine.removeLocationUpdates()
        locationLayerPlugin.onStop()
        mapView?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
        locationEngine.deactivate()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (outState != null)
        {mapView?.onSaveInstanceState(outState)}
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
    private fun pickupcoin(marker: Marker){
        val coin=markers.getValue(marker)
        val index=coinindex.getValue(coin.getid())
        map?.removeMarker(marker)
        val settings=getSharedPreferences(coinzFile,Context.MODE_PRIVATE)
        val editor=settings.edit()
        editor.putBoolean("$index taken",true)
        editor.apply()
        wallet.addCoin(coin)

    }
    override fun onMapReady(mapboxMap: MapboxMap?){
        if (mapboxMap==null)
        {
            Log.d(tag,"[onMapReady] mapboxMap is null")
        }
        else{
            map=mapboxMap
            //set user interface options
            map?.uiSettings?.isCompassEnabled=true
            // make location information available
            enableLocation()
            for (i in 0..49)
            {
                val coin = getdailycoin(i)
                if(!coin.istaken()){
                    val marker=(map?.addMarker(MarkerOptions()
                            .position(LatLng(coin.getlat(), coin.getlong()))
                            .title("id: "+coin.getid()+"\ncurrency:"+coin.getcurrency()+"\nvalue:"+coin.getvalue().toString())))
                    markers[marker] = coin
                    coinindex[coin.getid()] = i

                }}
            Log.d(tag,"Markers placed on map.")
            map?.setOnMarkerClickListener(this)
                    }
    }



}

