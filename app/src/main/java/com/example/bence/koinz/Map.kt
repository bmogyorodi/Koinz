package com.example.bence.koinz

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
    private val bonusprefs="MyPrefsFile"
    private val tag="Mapview"
    private val markers=HashMap<Marker?,Coinz>(50)
    private val coinindex=HashMap<String,Int>(50)
    private val wallet= Wallet()
    private var radius=25
    private var shilx=0.0
    private var dolrx=0.0
    private var penyx=0.0
    private var quidx=0.0



    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        supportActionBar?.title="Coinz Map"
        pickupbutton= findViewById(R.id.Pickupbutton)
        wallet.getwallet()
        Mapbox.getInstance(applicationContext, getString(R.string.access_token)) //getting mapbox map with provided token
        mapView =findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
        pickupbutton.setOnClickListener{_->
            if(!wallet.isfull()){


            pickupcoin(selmarker)
            pickupbutton.isEnabled=false}
            else{
                Toast.makeText(this,"Wallet is full!",Toast.LENGTH_SHORT)
            }
            //picks up selected coin and disables the button again

        }
    }
        private fun getdailycoin(i: Int):Coinz{
        val setting =getSharedPreferences(coinzFile, Context.MODE_PRIVATE)
        val id=setting.getString("$i id","missingid")
        val value=setting.getFloat("$i value", 0.0F)
        val currency= setting.getString("$i currency","missingcurr")
        val longitude= setting.getFloat("$i longitude",0.0F)
        val latitude= setting.getFloat("$i latitude",0.0F)
         val taken =setting.getBoolean("$i Taken",false)
        return Coinz(id,value,currency, latitude, longitude,taken)
            //returns a coin from the set of daily coinz using an index
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
        Log.d(tag,"Init location engine") //set location engine which finds userlocation

        val lastlocation =locationEngine.lastLocation
        if (lastlocation != null){
            originLocation=lastlocation
            setCameraPosition(lastlocation)
            Log.d(tag,"New location set") //sets new user location
        } else {
            locationEngine.addLocationEngineListener(this) //waits for user location input
            Log.d(tag,"No new location yet!")
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
                Log.d(tag,"Locationlayer initialized") //sets locationlayer
            }
        }

    }
    private fun setCameraPosition(location: Location){
        map?.animateCamera(CameraUpdateFactory.newLatLng(
                LatLng(location.latitude, location.longitude))) //setting camera position

    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
         Toast.makeText(this,"Permissions: $permissionsToExplain", LENGTH_SHORT).show()//Present a toast or dialogue on why they need to grant access
    }

    override fun onPermissionResult(granted: Boolean) {
        Log.d(tag,"[onPermissionResult] granted==$granted")
         if (granted){enableLocation()} //enable location when permission is granted
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }





    override fun onLocationChanged(location: Location?) {
         if(location==null)
         {Log.d(tag,"[onLocationChange] location is null")}
        else{
             originLocation=location
             setCameraPosition(originLocation)
         }
        //keep changing locations

    }
    override fun onMarkerClick(marker: Marker): Boolean {
        //set up marker on click command

       Log.d(tag, "Markerclick registered")

        val selected=markers.getValue(marker) //get coin by accessing it from marker-coin hashmap


        val distance=marker.position.distanceTo(LatLng(originLocation)) //measure distance between selected marker and position of user
        if (distance<radius)
        {
            val selcurr=selected.getcurrency()
            val value= (selected.getvalue()+0.5).toInt() //rounds up value to the closest integer
            pickupbutton.isEnabled=true
            pickupbutton.text=("Pick up $value in $selcurr currency") //display value and currency of selected coin
            selmarker=marker




        }
        else{
            Log.d(tag,"Disatance is: $distance")
            makeText(this,"Distance is $distance \n coin is too far",Toast.LENGTH_SHORT).show()
            //if the marker is too far to be selected then the distance is displayed
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
        updateradius()
        updaterates()

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
        wallet.savewallet()
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
        editor.putBoolean("$index Taken",true)
        editor.apply()
        coin.taken()
        wallet.addCoin(coin)
        wallet.savewallet()//adds coin to the wallet after the pickup button is pushed, rewrites taken boolean of coin to be true, so it won't be displayed on the map anymore

    }
    private fun updateradius(){
        val settings=getSharedPreferences(bonusprefs, Context.MODE_PRIVATE)
        val tier1=settings.getBoolean("radius1",false)
        val tier2=settings.getBoolean("radius2",false)
        val tier3=settings.getBoolean("radius3",false)
        if(tier1){
            radius=40
            if(tier2){
                radius=65
                if(tier3){
                    radius=100
                }
            }
        }
        Log.d(tag,"Radius set to $radius")
        //updates radius, which is the distance allowed between the user and marker when selected for pickup. Changes the variable based on what bonuses were purchased.
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
                            .title("id: "+coin.getid()+"\ncurrency:"+coin.getcurrency()+"\nvalue:"+(coin.getvalue()+0.5).toInt().toString())))
                    markers[marker] = coin // marker is connected to it's coin by hashmap
                    coinindex[coin.getid()] = i // coin id is connected to index by hashmap

                }} //puts all 50 marker on the map for the 50 coinz connecting them with hashmap
            Log.d(tag,"Markers placed on map.")
            map?.setOnMarkerClickListener(this) //activating markers on click functions
                    }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.map_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when(item.itemId){
            R.id.toWallet->{
                val intent= Intent(this,Depositcoinz::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } // wallet button, takes user to DepositCoinz menu
            R.id.showrates->{
                val exchange="Exchange rates: \n Peny: $penyx \n Quid: $quidx \n Shil: $shilx \n Dolr: $dolrx"
                Toast.makeText(this,exchange,5).show()

            } // rates button, shows user today's rates
            R.id.backtomenu->{
                val intent= Intent(this,MainActivity::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } // back button takes user to MainActivity (main-menu)
        }
        return super.onOptionsItemSelected(item)


    }
    private fun updaterates(){
        val settings=getSharedPreferences(bonusprefs, Context.MODE_PRIVATE)
        penyx= settings.getFloat("penyEX", 10F).toDouble()
        quidx= settings.getFloat("quidEX", 10F).toDouble()
        shilx= settings.getFloat("shilEX", 10F).toDouble()
        dolrx= settings.getFloat("dolrEX", 10F).toDouble()
    } // gets rates from preference file for display



}

