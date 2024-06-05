package com.realestate.fragment

import android.Manifest.permission
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorSpace
import android.graphics.drawable.ColorDrawable
import android.location.*
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.maps.android.ui.IconGenerator
import com.realestate.R
import com.realestate.activity.MapsActivity
import com.realestate.adapter.CustomMarkerAdapter
import com.realestate.adapter.recycler.BranchAdapter
import com.realestate.adapter.spinner.SpinnerBranchAdapter
import com.realestate.adapter.spinner.SpinnerVehicleAdapter
import com.realestate.callback.ResponseCallback
import com.realestate.model.BranchModel
import com.realestate.model.DeviceDetailsModel
import com.realestate.model.DeviceFilterModel
import com.realestate.model.VehicleModel
import com.realestate.restapi.ApiClient
import com.realestate.restapi.RetrofitRequest
import com.realestate.utils.CustomDialogFragment
import kotlinx.android.synthetic.main.add_insurance_dialog_fragment.*
import kotlinx.android.synthetic.main.add_insurance_dialog_fragment.view.*
import kotlinx.android.synthetic.main.add_trip_dialog_fragment.*
import kotlinx.android.synthetic.main.add_trip_dialog_fragment.view.*
import kotlinx.android.synthetic.main.add_user_dialog_fragment.view.*
import kotlinx.android.synthetic.main.fragment_branch.*
import kotlinx.android.synthetic.main.fragment_maps.*
import kotlinx.android.synthetic.main.fragment_vehicles.*
import kotlinx.android.synthetic.main.progress_dialog.*
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class MapsFragment : Fragment(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener,
    com.google.android.gms.location.LocationListener {
    private val CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000
    private lateinit var mLocationManager: LocationManager
    private var mGoogleApiClient: GoogleApiClient? = null
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLastLocation: Location
    private lateinit var mCurrentLocation: Location
    private var currentZoom = 10f
    private lateinit var mMap: GoogleMap
    private lateinit var mCurrLocationMarker: Marker
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var markerAdapter: CustomMarkerAdapter
    private var dialog: Dialog? = null
    private lateinit var deviceList: MutableList<DeviceDetailsModel>
    private var filterDeviceList = mutableListOf<DeviceDetailsModel>()
    private lateinit var mapFragment:SupportMapFragment
    private var spinnerSelected:Int = -1
    private var vehicle_id:String = ""
    private var branchName:String = ""
    private var branchList = mutableListOf<BranchModel>()
    private var vehicleList = mutableListOf<VehicleModel>()
    private lateinit var vehicleAdapter: SpinnerVehicleAdapter
    private lateinit var branchAdapter: SpinnerBranchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deviceList = arguments?.getSerializable("device_list") as MutableList<DeviceDetailsModel>
        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                buildGoogleApiClient()
            }
        } else {
            buildGoogleApiClient()
        }
        mLocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        //fetchIDeviceList()

    }

    @Synchronized
    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(requireActivity())
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient?.connect()

    }

    override fun onStart() {
        mGoogleApiClient?.connect()
        super.onStart()
    }

    override fun onStop() {
        mGoogleApiClient?.disconnect()
        super.onStop()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //fetchBranch()

        activity?.filter_spinner?.visibility = View.VISIBLE

        var list = arrayListOf<String>("Filter", "Single Date", "From and To Date", "Branch and Date", "Vehicle and Date")
        var adapter =  ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, list)

        requireActivity().filter_spinner.setAdapter(adapter)

        requireActivity().filter_spinner.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val text = list.get(p2)
                if (text.equals("Single Date", ignoreCase = true)){
                    spinnerSelected = 1
                    all_filter_layout.visibility = View.VISIBLE
                    single_date_filter_layout.visibility = View.VISIBLE
                    from_to_date_filter_layout.visibility = View.GONE
                    branch_date_filter_layout.visibility = View.GONE
                    vehicle_date_filter_layout.visibility = View.GONE
                    map_vehicle_reg_spinner.setSelection(0)
                    map_branch_spinner.setSelection(0)
                    vehicle_id = ""
                    branchName = ""
                    map_date.setText("")
                    map_from_date.setText("")
                    map_to_date.setText("")
                    branch_date.setText("")
                    vehicle_date.setText("")
                }else if (text.equals("From and To Date", ignoreCase = true)){
                    spinnerSelected = 2
                    all_filter_layout.visibility = View.VISIBLE
                    from_to_date_filter_layout.visibility = View.VISIBLE
                    branch_date_filter_layout.visibility = View.GONE
                    vehicle_date_filter_layout.visibility = View.GONE
                    single_date_filter_layout.visibility = View.GONE
                    map_vehicle_reg_spinner.setSelection(0)
                    map_branch_spinner.setSelection(0)
                    vehicle_id = ""
                    branchName = ""
                    map_date.setText("")
                    map_from_date.setText("")
                    map_to_date.setText("")
                    branch_date.setText("")
                    vehicle_date.setText("")
                } else if (text.equals("Branch and Date", ignoreCase = true)){
                    spinnerSelected = 3
                    fetchBranch()
                    all_filter_layout.visibility = View.VISIBLE
                    branch_date_filter_layout.visibility = View.VISIBLE
                    vehicle_date_filter_layout.visibility = View.GONE
                    single_date_filter_layout.visibility = View.GONE
                    from_to_date_filter_layout.visibility = View.GONE
                    map_vehicle_reg_spinner.setSelection(0)
                    map_branch_spinner.setSelection(0)
                    vehicle_id = ""
                    branchName = ""
                    map_date.setText("")
                    map_from_date.setText("")
                    map_to_date.setText("")
                    branch_date.setText("")
                    vehicle_date.setText("")
                }else if (text.equals("Vehicle and Date", ignoreCase = true)){
                    spinnerSelected = 4
                    fetchVehicle()
                    all_filter_layout.visibility = View.VISIBLE
                    vehicle_date_filter_layout.visibility = View.VISIBLE
                    single_date_filter_layout.visibility = View.GONE
                    from_to_date_filter_layout.visibility = View.GONE
                    branch_date_filter_layout.visibility = View.GONE
                    map_vehicle_reg_spinner.setSelection(0)
                    map_branch_spinner.setSelection(0)
                    vehicle_id = ""
                    branchName = ""
                    map_date.setText("")
                    map_from_date.setText("")
                    map_to_date.setText("")
                    branch_date.setText("")
                    vehicle_date.setText("")
                }else{
                    updateUI(-1)
                    spinnerSelected = -1
                    all_filter_layout.visibility = View.GONE
                    map_vehicle_reg_spinner.setSelection(0)
                    map_branch_spinner.setSelection(0)
                    vehicle_id = ""
                    branchName = ""
                    map_date.setText("")
                    map_from_date.setText("")
                    map_to_date.setText("")
                    branch_date.setText("")
                    vehicle_date.setText("")
                }
            }

        })

        radio_map_filter.setOnCheckedChangeListener(object :
            RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                if (p1 == R.id.radio_single_date) {
                    all_filter_layout.visibility = View.VISIBLE
                    single_date_filter_layout.visibility = View.VISIBLE
                    from_to_date_filter_layout.visibility = View.GONE
                    branch_date_filter_layout.visibility = View.GONE
                    vehicle_date_filter_layout.visibility = View.GONE
                } else if (p1 == R.id.radio_from_to_date) {
                    all_filter_layout.visibility = View.VISIBLE
                    from_to_date_filter_layout.visibility = View.VISIBLE
                    branch_date_filter_layout.visibility = View.GONE
                    vehicle_date_filter_layout.visibility = View.GONE
                    single_date_filter_layout.visibility = View.GONE
                } else if (p1 == R.id.radio_branch_date) {
                    all_filter_layout.visibility = View.VISIBLE
                    branch_date_filter_layout.visibility = View.VISIBLE
                    vehicle_date_filter_layout.visibility = View.GONE
                    single_date_filter_layout.visibility = View.GONE
                    from_to_date_filter_layout.visibility = View.GONE
                }else{
                    all_filter_layout.visibility = View.VISIBLE
                    vehicle_date_filter_layout.visibility = View.VISIBLE
                    single_date_filter_layout.visibility = View.GONE
                    from_to_date_filter_layout.visibility = View.GONE
                    branch_date_filter_layout.visibility = View.GONE
                }
            }

        })


        filter_txt.setOnClickListener {
            if (spinnerSelected == 1){
                val date = map_date.text.toString()
                if (date.isNotEmpty()){
                    fetchDeviceListByDate(date)
                }else{
                    Toast.makeText(requireActivity(), "Please select date", Toast.LENGTH_LONG).show()
                }

            }else if(spinnerSelected == 2){
                val fromDate = map_from_date.text.toString()
                val toDate = map_to_date.text.toString()
                if (fromDate.isEmpty()){
                    Toast.makeText(requireActivity(), "Please select From date", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                if (toDate.isEmpty()){
                    Toast.makeText(requireActivity(), "Please select To date", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                fetchDeviceListByFromToDate(fromDate, toDate)

            }else if(spinnerSelected == 3){
                val date = branch_date.text.toString()
                if (branchName.isEmpty()){
                    Toast.makeText(requireActivity(), "Please select branch name", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                if (date.isEmpty()){
                    Toast.makeText(requireActivity(), "Please select date", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                fetchDeviceListByBranchDate(branchName, date)

            }else if(spinnerSelected == 4){
                val date = vehicle_date.text.toString()
                if (vehicle_id.isEmpty()){
                    Toast.makeText(requireActivity(), "Please select registaion ID", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                if (date.isEmpty()){
                    Toast.makeText(requireActivity(), "Please select date", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                fetchDeviceListByVehicleDate(vehicle_id, date)
            }else{
                Toast.makeText(requireActivity(), "Please select required information", Toast.LENGTH_LONG).show()
            }
        }

        map_vehicle_reg_spinner.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var roleModel = vehicleList.get(p2)

                if (roleModel.vehicleRegisteredNo != "Registration no") {
                    vehicle_id = roleModel.vehicleRegisteredNo
                } else {
                    vehicle_id = ""
                }
            }

        })

        map_branch_spinner.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                var roleModel = branchList.get(p2)

                if (roleModel.branchName != "Branch name") {
                    branchName = roleModel.branchName!!
                } else {
                    branchName = ""
                }
            }

        })

        map_date.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                when (event?.getAction()) {
                    MotionEvent.ACTION_DOWN -> {
                        val myCalendar = Calendar.getInstance()

                        val date =
                            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                myCalendar[Calendar.YEAR] = year
                                myCalendar[Calendar.MONTH] = monthOfYear
                                myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                                val myFormat = "MM/dd/yyyy" //In which you need put here
                                val sdf = SimpleDateFormat(myFormat, Locale.US)
                                val startDate = sdf.format(myCalendar.time)
                                map_date.setText(startDate)
                            }
                        var datePicker = DatePickerDialog(
                            context!!, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)
                        )

                        datePicker.show()
                    }
                }

                return false
            }

        })

        map_from_date.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                when (event?.getAction()) {
                    MotionEvent.ACTION_DOWN -> {
                        val myCalendar = Calendar.getInstance()

                        val date =
                            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                myCalendar[Calendar.YEAR] = year
                                myCalendar[Calendar.MONTH] = monthOfYear
                                myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                                val myFormat = "MM/dd/yyyy" //In which you need put here
                                val sdf = SimpleDateFormat(myFormat, Locale.US)
                                val startDate = sdf.format(myCalendar.time)
                                map_from_date.setText(startDate)
                            }
                        var datePicker = DatePickerDialog(
                            context!!, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)
                        )

                        datePicker.show()
                    }
                }

                return false
            }

        })

        map_to_date.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                when (event?.getAction()) {
                    MotionEvent.ACTION_DOWN -> {
                        val myCalendar = Calendar.getInstance()

                        val date =
                            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                myCalendar[Calendar.YEAR] = year
                                myCalendar[Calendar.MONTH] = monthOfYear
                                myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                                val myFormat = "MM/dd/yyyy" //In which you need put here
                                val sdf = SimpleDateFormat(myFormat, Locale.US)
                                val startDate = sdf.format(myCalendar.time)
                                map_to_date.setText(startDate)
                            }
                        var datePicker = DatePickerDialog(
                            context!!, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)
                        )

                        datePicker.show()
                    }
                }

                return false
            }

        })

        branch_date.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                when (event?.getAction()) {
                    MotionEvent.ACTION_DOWN -> {
                        val myCalendar = Calendar.getInstance()

                        val date =
                            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                myCalendar[Calendar.YEAR] = year
                                myCalendar[Calendar.MONTH] = monthOfYear
                                myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                                val myFormat = "MM/dd/yyyy" //In which you need put here
                                val sdf = SimpleDateFormat(myFormat, Locale.US)
                                val startDate = sdf.format(myCalendar.time)
                                branch_date.setText(startDate)
                            }
                        var datePicker = DatePickerDialog(
                            context!!, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)
                        )

                        datePicker.show()
                    }
                }

                return false
            }

        })

        vehicle_date.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                when (event?.getAction()) {
                    MotionEvent.ACTION_DOWN -> {
                        val myCalendar = Calendar.getInstance()

                        val date =
                            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                myCalendar[Calendar.YEAR] = year
                                myCalendar[Calendar.MONTH] = monthOfYear
                                myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                                val myFormat = "MM/dd/yyyy" //In which you need put here
                                val sdf = SimpleDateFormat(myFormat, Locale.US)
                                val startDate = sdf.format(myCalendar.time)
                                vehicle_date.setText(startDate)
                            }
                        val datePicker = DatePickerDialog(
                            context!!, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)
                        )

                        datePicker.show()
                    }
                }

                return false
            }

        })

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onConnected(p0: Bundle?) {
        Log.d("TAG", "onConnected: true")
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = (5 * 1000).toLong()
        mLocationRequest.fastestInterval = (2 * 1000).toLong()
        mLocationRequest.priority = LocationRequest.PRIORITY_LOW_POWER
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
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
        updateUI(-1)
    }

    private fun updateUI(selectionValue: Int) {
        mMap.clear()
        if (selectionValue == -1){
            if (deviceList.isNotEmpty()) {
                val builder = LatLngBounds.Builder()
                deviceList.forEach {
                    val latLng = LatLng(it.lat, it.lng)
                    val markerOptions = MarkerOptions()
                    /*val text = TextView(context)
                    text.text = "Car"
                    val generator = IconGenerator(context)
                    generator.setBackground(requireActivity().resources.getDrawable(R.drawable.ic_directions_car))
                    generator.setContentView(text)
                    val icon = generator.makeIcon()
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon))*/

                    if (it.engineStatus)
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    else
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    markerOptions.position(latLng)
                    val m = mMap.addMarker(markerOptions)
                    m.tag = it
                    builder.include(markerOptions.position)
                    //mMap.projection.visibleRegion.latLngBounds.contains(latLng)
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                    //mMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoom))
                }
                val bounds = builder.build()
                val width = resources.displayMetrics.widthPixels
                val height = resources.displayMetrics.heightPixels
                val padding = (width * 0.10).toInt() // offset from edges of the map 10% of screen

                val camPos = CameraPosition.Builder()
                    .target(bounds.center)
                    .zoom(10f)
                    .build()

                //val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
                //mMap.moveCamera(cu)
                val cu = CameraUpdateFactory.newCameraPosition(camPos);
                mMap.animateCamera(cu)

            } else {
                //Showing Current Location Marker on Map
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
                val status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(requireActivity())
                if (status == ConnectionResult.SUCCESS) {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    /*markerOptions.icon(
                        bitmapDescriptorFromVector(
                            requireActivity(),
                            R.drawable.ic_navigation
                        )
                    )*/

                    mCurrLocationMarker = mMap.addMarker(markerOptions)

                    if (mCurrLocationMarker != null) {
                        //MarkerAnimation.move(mMap, mCurrLocationMarker, mCurrentLocation)
                    }

                } else {
                    GooglePlayServicesUtil.getErrorDialog(status, requireActivity(), status)
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoom))
                /*if (mGoogleApiClient != null) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                }*/
            }
        }else{
            if (filterDeviceList.isNotEmpty()) {
                val builder = LatLngBounds.Builder()
                filterDeviceList.forEach {
                    val latLng = LatLng(it.lat, it.lng)
                    val markerOptions = MarkerOptions()
                    /*val text = TextView(context)
                    text.text = "Car"
                    val generator = IconGenerator(context)
                    generator.setBackground(requireActivity().resources.getDrawable(R.drawable.ic_directions_car))
                    generator.setContentView(text)
                    val icon = generator.makeIcon()
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon))*/

                    if (it.engineStatus)
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    else
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    markerOptions.position(latLng)
                    val m = mMap.addMarker(markerOptions)
                    m.tag = it
                    builder.include(markerOptions.position)
                    //mMap.projection.visibleRegion.latLngBounds.contains(latLng)
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                    //mMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoom))
                }
                val bounds = builder.build()
                val width = resources.displayMetrics.widthPixels
                val height = resources.displayMetrics.heightPixels
                val padding = (width * 0.10).toInt() // offset from edges of the map 10% of screen

                val camPos = CameraPosition.Builder()
                    .target(bounds.center)
                    .zoom(10f)
                    .build()

                //val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
                //mMap.moveCamera(cu)
                val cu = CameraUpdateFactory.newCameraPosition(camPos);
                mMap.animateCamera(cu)

            } else {
                //Showing Current Location Marker on Map
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
                val status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(requireActivity())
                if (status == ConnectionResult.SUCCESS) {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    /*markerOptions.icon(
                        bitmapDescriptorFromVector(
                            requireActivity(),
                            R.drawable.ic_navigation
                        )
                    )*/

                    mCurrLocationMarker = mMap.addMarker(markerOptions)

                    if (mCurrLocationMarker != null) {
                        //MarkerAnimation.move(mMap, mCurrLocationMarker, mCurrentLocation)
                    }

                } else {
                    GooglePlayServicesUtil.getErrorDialog(status, requireActivity(), status)
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoom))
                /*if (mGoogleApiClient != null) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                }*/
            }
        }
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
                    requireActivity(),
                    CONNECTION_FAILURE_RESOLUTION_REQUEST
                )

            } catch (e: SendIntentException) {
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
                requireActivity(),
                permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
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
        fusedLocationClient.lastLocation
            .addOnSuccessListener(requireActivity(),
                OnSuccessListener<Location> { location -> // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        mLastLocation = location
                        Log.d(
                            "TAG",
                            "onLocationChanged: mLastLocation " + mLastLocation.latitude + " " + mLastLocation.longitude
                        )
                    }
                })
            .addOnFailureListener(requireActivity(),
                OnFailureListener { e ->
                    Log.e(
                        "TAG",
                        "onLocationChanged: mLastLocation error " + e.message
                    )
                })
    }

    private fun getAddress(): String? {
        val provider = mLocationManager.getBestProvider(Criteria(), true)
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireActivity(), permission.ACCESS_COARSE_LOCATION)
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
            val geocoder = Geocoder(requireActivity(), Locale.getDefault())
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

    override fun onLocationChanged(location: Location) {
        //getLastLocation()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    requireActivity(), permission.ACCESS_COARSE_LOCATION
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

    override fun onMapReady(googleMap: GoogleMap) {
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        mMap = googleMap
        markerAdapter = CustomMarkerAdapter(requireActivity())
        mMap.setInfoWindowAdapter(markerAdapter)
        val gps = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
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

        }
        //googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Kolkata"))
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10f))
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.uiSettings.isZoomControlsEnabled = false
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.isBuildingsEnabled = true
        /*val builder = LatLngBounds.Builder()
        mMap.setOnMapLoadedCallback {
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    builder.build(),
                    15
                )
            )
        }*/
        mMap.setOnInfoWindowClickListener {
            if (it.tag != null) {
                val device = it.tag as DeviceDetailsModel
                fetchDeviceList(device.id)
            }else {
                Toast.makeText(requireActivity(), "It's you", Toast.LENGTH_LONG).show()
            }
        }
        //mMap.isMyLocationEnabled = true
        mMap.setOnMyLocationChangeListener(OnMyLocationChangeListener { arg0 ->
            googleMap.clear()
            googleMap.addMarker(
                MarkerOptions().position(LatLng(arg0.latitude, arg0.longitude)).title("It's Me!")
            )
        })
        mMap.setOnMarkerClickListener { marker ->
            if (marker.isInfoWindowShown) {
                marker.hideInfoWindow()
            } else {
                marker.showInfoWindow()
            }

            true
        }
    }

    private fun fetchBranch() {
        showProgress(requireActivity(), "Fetching branch...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val call = retrofit?.getAllBranch()

        RetrofitRequest().enqueue(
            call as Call<List<BranchModel>>,
            object : ResponseCallback<List<BranchModel>> {
                override fun onSuccess(response: List<BranchModel>) {
                    hideProgress()
                    //fetchVehicle()
                    branchList.clear()
                    (response as MutableList).add(
                        0,
                        BranchModel("", "Branch name", "", "", -1, null, -1)
                    )
                    branchList.addAll(response)

                    branchAdapter = SpinnerBranchAdapter(
                        context!!,
                        branchList
                    )
                    if (map_branch_spinner != null)
                        map_branch_spinner.adapter = branchAdapter
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun fetchVehicle() {
        showProgress(requireActivity(), "Fetching vehicle...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val call = retrofit?.getAllVehicle()

        RetrofitRequest().enqueue(
            call as Call<List<VehicleModel>>,
            object : ResponseCallback<List<VehicleModel>> {
                override fun onSuccess(response: List<VehicleModel>) {
                    hideProgress()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    vehicleList.clear()
                    (response as MutableList).add(
                        0,
                        VehicleModel("", -1, "Registration no", "", "", "", null, null, null,null, null, null)
                    )
                    vehicleList.addAll(response)
                    vehicleAdapter =
                        SpinnerVehicleAdapter(
                            context!!,
                            vehicleList
                        )
                    if (map_vehicle_reg_spinner != null)
                        map_vehicle_reg_spinner.adapter = vehicleAdapter
                    //updateListener.onUpdate(true)
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun fetchDeviceList(id: Int) {
        showProgress(requireActivity(), "Fetching devices...")
        val retrofit = ApiClient.instance?.getClient(true, requireActivity())

        val call = retrofit?.getDeviceDetailsList(id)

        RetrofitRequest().enqueue(
            call as Call<DeviceDetailsModel>,
            object : ResponseCallback<DeviceDetailsModel> {
                override fun onSuccess(response: DeviceDetailsModel) {
                    hideProgress()
                    Log.d("TAGG", "dealerList $response")
                    var intent = Intent(requireActivity(), MapsActivity::class.java)
                    val bundle = Bundle()
                    bundle.putParcelable("device_details_list", response as Parcelable)
                    intent.putExtras(bundle)
                    requireActivity().startActivity(intent)

                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(requireActivity(), "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun fetchDeviceListByDate(date: String) {
        showProgress(requireActivity(), "Fetching devices...")
        val retrofit = ApiClient.instance?.getClient(true, requireActivity())

        val call = retrofit?.getDeviceDetailsByDate(date)

        RetrofitRequest().enqueue(
            call as Call<DeviceFilterModel>,
            object : ResponseCallback<DeviceFilterModel> {
                override fun onSuccess(response: DeviceFilterModel) {
                    hideProgress()
                    filterDeviceList.clear()
                    filterDeviceList.addAll(response.details)
                    Log.d("Filter", "fetchDeviceListByDate $response")
                    updateUI(1)

                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(requireActivity(), "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun fetchDeviceListByFromToDate(fromDate: String, toDate: String) {
        showProgress(requireActivity(), "Fetching devices...")
        val retrofit = ApiClient.instance?.getClient(true, requireActivity())
        val map = HashMap<String, String>()
        map.put("FromDate", fromDate)
        map.put("ToDate", toDate)

        val call = retrofit?.getDeviceDetailsByFromdateAndTodate(map)

        RetrofitRequest().enqueue(
            call as Call<DeviceFilterModel>,
            object : ResponseCallback<DeviceFilterModel> {
                override fun onSuccess(response: DeviceFilterModel) {
                    hideProgress()
                    filterDeviceList.clear()
                    filterDeviceList.addAll(response.details)
                    Log.d("Filter", "fetchDeviceListByFromToDate $response")
                    updateUI(2)

                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(requireActivity(), "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun fetchDeviceListByBranchDate(branchName: String, date: String) {
        showProgress(requireActivity(), "Fetching devices...")
        val retrofit = ApiClient.instance?.getClient(true, requireActivity())
        val map = HashMap<String, String>()
        map.put("Branch", branchName)
        map.put("date", date)

        val call = retrofit?.getDeviceDetailsByBranchAndDate(map)

        RetrofitRequest().enqueue(
            call as Call<DeviceFilterModel>,
            object : ResponseCallback<DeviceFilterModel> {
                override fun onSuccess(response: DeviceFilterModel) {
                    hideProgress()
                    filterDeviceList.clear()
                    filterDeviceList.addAll(response.details)
                    Log.d("Filter", "fetchDeviceListByBranchDate $response")
                    updateUI(3)

                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(requireActivity(), "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun fetchDeviceListByVehicleDate(vehicleReg: String, date: String) {
        showProgress(requireActivity(), "Fetching devices...")
        val retrofit = ApiClient.instance?.getClient(true, requireActivity())
        val map = HashMap<String, String>()
        map.put("vehicleregno", vehicleReg)
        map.put("date", date)

        val call = retrofit?.getDeviceDetailsByVehicleregAndDate(map)

        RetrofitRequest().enqueue(
            call as Call<DeviceFilterModel>,
            object : ResponseCallback<DeviceFilterModel> {
                override fun onSuccess(response: DeviceFilterModel) {
                    hideProgress()
                    filterDeviceList.clear()
                    filterDeviceList.addAll(response.details)
                    updateUI(4)
                    Log.d("Filter", "fetchDeviceListByVehicleDate $response")

                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(requireActivity(), "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun showProgress(context: Context, message: String) {
        dialog = Dialog(context)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setContentView(R.layout.progress_dialog)
        dialog?.progress_ok_logout?.setOnClickListener(View.OnClickListener {
            dialog?.dismiss()
        })

        dialog?.progress_cancel_logout?.setOnClickListener(View.OnClickListener {
            dialog?.dismiss()
        })


        //dialog?.txtHeader?.text = "Attention"

        dialog?.progress_load_message?.text = message
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
        dialog?.show()
        /* this.overridePendingTransition(R.anim.slide_left,
        R.anim.slide_left);*/
    }

    private fun hideProgress() {
        if (dialog != null)
            dialog?.dismiss()
    }

}