package com.realestate.activity

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.realestate.R
import com.realestate.model.DeviceDetailsModel
import java.io.IOException
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener,
    com.google.android.gms.location.LocationListener {

    private lateinit var mMap: GoogleMap
    private lateinit var deviceList: DeviceDetailsModel
    private val CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000
    private lateinit var mLocationManager: LocationManager
    private var mGoogleApiClient: GoogleApiClient? = null
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mLastLocation: Location
    private lateinit var mCurrentLocation: Location
    private lateinit var mCurrLocationMarker: Marker
    private var currentZoom = 10f

    internal class MyBroadcastReceiver : BroadcastReceiver() {
        //    MediaPlayer mp;
        override fun onReceive(context: Context, intent: Intent) {
//        mp=MediaPlayer.create(context, R.raw.alarm);
//        mp.start();
            Toast.makeText(context, "Alarm updated", Toast.LENGTH_LONG).show()
        }
    }

    private fun startAlarm(context: Context) {
        //val timeInSec = 2
        val intent = Intent(this, MyBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 1, pendingIntent) // Millisec * Second * Minute
        Toast.makeText(this, "Alarm started", Toast.LENGTH_LONG).show()
    }

    private fun cancelAlarm(context: Context) {
        val intent = Intent(context, MyBroadcastReceiver::class.java)
        val sender = PendingIntent.getBroadcast(this, 0, intent, 0)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
        Toast.makeText(this, "Alarm cancelled", Toast.LENGTH_LONG).show()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_maps)
        deviceList = intent.extras?.getParcelable<DeviceDetailsModel>("device_details_list") as DeviceDetailsModel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                buildGoogleApiClient()
            }
        } else {
            buildGoogleApiClient()
        }
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @Synchronized
    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient?.connect()

    }

    override fun onResume() {
        super.onResume()
        startAlarm(this)
    }

    override fun onStart() {
        mGoogleApiClient?.connect()
        super.onStart()
    }

    override fun onStop() {
        mGoogleApiClient?.disconnect()
        super.onStop()
        cancelAlarm(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelAlarm(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.uiSettings.isZoomControlsEnabled = false
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.isBuildingsEnabled = false

        /*val apiRequest = DirectionsApi.newRequest(context)
        apiRequest.origin(com.google.maps.model.LatLng(latitude, longitude))
        apiRequest.destination(com.google.maps.model.LatLng(latitude, longitude))
        apiRequest.mode(TravelMode.DRIVING) //set travelling mode


        apiRequest.setCallback(object : PendingResult.Callback<DirectionsResult> {
            override fun onResult(result: DirectionsResult) {
                val routes: Array<DirectionsRoute> = result.routes
            }

            override fun onFailure(e: Throwable) {}
        })*/

    }

    override fun onConnected(p0: Bundle?) {
        Log.d("TAG", "onConnected: true")
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = (5 * 1000).toLong()
        mLocationRequest.fastestInterval = (2 * 1000).toLong()
        mLocationRequest.priority = LocationRequest.PRIORITY_LOW_POWER
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        val location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
        while (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
            )
        }
        mLastLocation = location
        //updateUI(mLastLocation)
        updateUI()
    }

    private fun updateUI() {
        mMap.clear()
        val latLng = LatLng(deviceList.lat, deviceList.lng)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng).title(deviceList.deviceData)
        if (deviceList.engineStatus)
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        else
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        val m = mMap.addMarker(markerOptions)
        m.tag = deviceList
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoom))

        /*val barcelona = LatLng(12.890096, 77.123969)
        mMap.addMarker(MarkerOptions().position(barcelona).title("Marker in Barcelona"))

        val madrid = LatLng(12.6890096, 77.1623969)
        mMap.addMarker(MarkerOptions().position(madrid).title("Marker in Madrid"))

        val zaragoza = LatLng(41.648823, -0.889085)

        //Define list to get all latlng for the route
        val path = mutableListOf<LatLng>()
        //Execute Directions API request
        val context: GeoApiContext = GeoApiContext.Builder().apiKey("AIzaSyAgdEOc4WctuGyHpvUlkumrgMaofts1-UM").build()

        val req: DirectionsApiRequest =
            DirectionsApi.getDirections(context, "12.890096, 77.123969", "12.6890096, 77.1623969")
        try {
            val res: DirectionsResult = req.await()

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.size > 0) {
                val route: DirectionsRoute = res.routes.get(0)
                if (route.legs != null) {
                    for (i in route.legs.indices) {
                        val leg = route.legs[i]
                        if (leg.steps != null) {
                            for (j in leg.steps.indices) {
                                val step = leg.steps[j]
                                if (step.steps != null && step.steps.size > 0) {
                                    for (k in step.steps.indices) {
                                        val step1 = step.steps[k]
                                        val points1 = step1.polyline
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            val coords1 = points1.decodePath()
                                            for (coord1 in coords1) {
                                                path.add(LatLng(coord1.lat, coord1.lng))
                                            }
                                        }
                                    }
                                } else {
                                    val points = step.polyline
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        val coords = points.decodePath()
                                        for (coord in coords) {
                                            path.add(LatLng(coord.lat, coord.lng))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (ex: Exception) {
            Log.d("Error", "error ${ex.message}")
        }

        //Draw the polyline
        if (path.size > 0) {
            val opts = PolylineOptions().addAll(path).color(Color.BLUE).width(5f)
            mMap.addPolyline(opts)
        }*/
        /*if (deviceList.isNotEmpty()) {
            deviceList.forEach {
                val latLng = LatLng(it.lat, it.lng)
                val markerOptions = MarkerOptions()
                markerOptions.position(latLng)
                val m = mMap.addMarker(markerOptions)
                m.tag = it
                if (it.engineStatus)
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                else
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoom))
            }
        } else {
            //Showing Current Location Marker on Map
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            val location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
            while (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mLocationRequest,
                    this
                )
            }
            val latLng = LatLng(location.latitude, location.longitude)
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            //mLastLocation.bearingTo(location);
            val address: String? = getAddress()
            if (address != null) markerOptions.title(address)
            val status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)
            if (status == ConnectionResult.SUCCESS) {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                *//*markerOptions.icon(
                    bitmapDescriptorFromVector(
                        requireActivity(),
                        R.drawable.ic_navigation
                    )
                )*//*

                mCurrLocationMarker = mMap.addMarker(markerOptions)

                if (mCurrLocationMarker != null) {
                    //MarkerAnimation.move(mMap, mCurrLocationMarker, mCurrentLocation)
                }

            } else {
                GooglePlayServicesUtil.getErrorDialog(status, this, status)
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoom))
        }*/
    }

    fun bitmapDescriptorFromVector(
        context: Context,
        @DrawableRes vectorDrawableResourceId: Int
    ): BitmapDescriptor? {
        val vectorDrawable =
            ResourcesCompat.getDrawable(context.resources, vectorDrawableResourceId, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable?.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        //DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun getAddress(): String? {
        val provider = mLocationManager.getBestProvider(Criteria(), true)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }
        val locations = mLocationManager.getLastKnownLocation(provider!!)
        val providerList = mLocationManager.allProviders
        if (null != locations && providerList.size > 0) {
            val longitude = locations.longitude
            val latitude = locations.latitude
            Log.d("TAG", "updateUI: new longitude $longitude latitude $latitude")
            val geocoder = Geocoder(this, Locale.getDefault())
            try {
                val listAddresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (null != listAddresses && listAddresses.size > 0) {
                    val state = listAddresses[0].adminArea
                    val country = listAddresses[0].countryName
                    val subLocality = listAddresses[0].subLocality
                    return "$subLocality,$state,$country"
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    override fun onConnectionSuspended(i: Int) {
        if (i == 1) {
            mGoogleApiClient?.connect()
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        mGoogleApiClient?.connect()

        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST
                )

            } catch (e: IntentSender.SendIntentException) {
                // Log the error
                e.printStackTrace()
            }
        } else {
            Log.d(
                "TAG",
                "Location services connection failed with code " + connectionResult.errorCode
            )
        }
    }

    private fun getLastLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener(this,
                OnSuccessListener<Location> { location -> // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        mLastLocation = location
                        Log.d(
                            "TAG",
                            "onLocationChanged: mLastLocation " + mLastLocation.latitude + " " + mLastLocation.longitude
                        )
                    }
                })
            .addOnFailureListener(this,
                OnFailureListener { e ->
                    Log.e(
                        "TAG",
                        "onLocationChanged: mLastLocation error " + e.message
                    )
                })
    }

    override fun onLocationChanged(location: Location) {
        getLastLocation()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return
            }
        }
        mCurrentLocation = location

        if (mCurrentLocation != null) {
            Log.d(
                "TAG",
                "onLocationChanged: mLastLocation " + mLastLocation.latitude + " " + mLastLocation.longitude
            )
            Log.d(
                "TAG",
                "onLocationChanged: mCurrentLocation " + mCurrentLocation.latitude + " " + mCurrentLocation.longitude
            )
            //mCurrLocationMarker.remove()
        }

        //updateUI(mCurrentLocation)
    }

    override fun onProviderDisabled(provider: String) {
        Log.d("TAG", "onProviderDisabled $provider")
        mLocationManager.removeUpdates(this)
    }

    override fun onProviderEnabled(provider: String) {
        Log.d("TAG", "onProviderEnabled $provider")
        /*val gps = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val network = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (mLocationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                if (gps) {
                    val criteria = Criteria()
                    criteria.accuracy = Criteria.ACCURACY_FINE
                    //buildAlertMessageNoGps();
                    mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0,
                        0f,
                        this
                    )
                } else if (network) {
                    val criteria = Criteria()
                    criteria.accuracy = Criteria.ACCURACY_COARSE
                    mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        0,
                        0f,
                        this
                    )
                } else {
                    LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient,
                        mLocationRequest,
                        this
                    )

                }
            }

        }*/

    }

}