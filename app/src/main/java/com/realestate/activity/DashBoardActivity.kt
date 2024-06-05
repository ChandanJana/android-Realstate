package com.realestate.activity

import android.Manifest.permission
import android.Manifest.permission_group
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.realestate.R
import com.realestate.app.RealEstateApp
import com.realestate.autologout.ApplockManager
import com.realestate.callback.CountUpdateListener
import com.realestate.callback.DialogCallback
import com.realestate.callback.ResponseCallback
import com.realestate.model.DeviceDetailsModel
import com.realestate.restapi.ApiClient
import com.realestate.restapi.RetrofitRequest
import com.realestate.storage.SharedPreferenceManager
import com.realestate.utils.AppNavigator
import com.realestate.utils.Constant
import com.realestate.utils.FragmentTag
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.content_menu.*
import kotlinx.android.synthetic.main.navigation.*
import kotlinx.android.synthetic.main.navigation.drawer_layout
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import java.io.Serializable
import java.util.*


class DashBoardActivity : AppCompatActivity(), View.OnClickListener, CountUpdateListener {

    private var userCount: Int = 0
    private var tripCount: Int = 0
    private var branchCount: Int = 0
    private var vehicleCount: Int = 0
    private val REQUEST_ID_MULTIPLE_PERMISSIONS = 100
    /*override fun onUserInteraction() {
        super.onUserInteraction()
        RealEstateApp.sInstance.touch()
    }*/

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        ApplockManager.instance?.enableDefaultAppLockIfAvailable(RealEstateApp.sInstance);
        ApplockManager.instance?.startWaitThread(this)
        checkAndRequestPermissions()
        setContentView(R.layout.navigation)
        RealEstateApp.sInstance.touch()
        fetchIDeviceList()
        //val intent = Intent(this, ExitKillAppService::class.java)
        //startService(intent)
        imgViewBack.setImageResource(R.drawable.ic_menu)
        imgViewBack.tag = "menu"
        dashBoard(true)
        imgViewBack.setOnClickListener(this)
        dashboard_txt.setOnClickListener(this)
        user_txt.setOnClickListener(this)
        material_txt.setOnClickListener(this)
        trip_txt.setOnClickListener(this)
        insurance_txt.setOnClickListener(this)
        vehical_txt.setOnClickListener(this)
        invoice_txt.setOnClickListener(this)
        branch_txt.setOnClickListener(this)
        companies_txt.setOnClickListener(this)
        permit_txt.setOnClickListener(this)
        pollution_txt.setOnClickListener(this)
        role_txt.setOnClickListener(this)
        dealer_txt.setOnClickListener(this)
        profile_layout.setOnClickListener(this)
        logout_txt.setOnClickListener(this)
        txtUsername.text = SharedPreferenceManager.getInstance(this)
            ?.getString(SharedPreferenceManager.Key.USER_NAME)
        txtUserEmail.text = SharedPreferenceManager.getInstance(this)
            ?.getString(SharedPreferenceManager.Key.USER_EMAIL)
        map_button.setOnClickListener {
            filter_spinner?.visibility = View.VISIBLE
            val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                val fm: FragmentManager = supportFragmentManager
                for (i in 0 until fm.backStackEntryCount) {
                    fm.popBackStack()
                }
                add_img.visibility = View.GONE
                refresh_img.visibility = View.GONE
                txtViewToolbarTitle.text = resources.getString(R.string.map)
                bottom_navigation1.menu.getItem(0).setChecked(false)
                bottom_navigation1.menu.getItem(1).setChecked(false)
                bottom_navigation1.menu.getItem(3).setChecked(false)
                bottom_navigation1.menu.getItem(4).setChecked(false)
                bottom_navigation1.menu.getItem(2).setChecked(false)
                var bundle = Bundle()
                bundle.putSerializable("device_list", deviceList as Serializable)
                AppNavigator.navigate(
                    this,
                    R.id.fragment_container,
                    FragmentTag.MAP_FRAGMENT, true, bundle
                )
            }else{
                buildAlertMessageNoGps()
            }

        }
        bottom_navigation1?.setOnNavigationItemSelectedListener(object :
            BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
                val id: Int = menuItem.getItemId()

                if (id == R.id.users_menu) {
                    val fm: FragmentManager = supportFragmentManager
                    for (i in 0 until fm.backStackEntryCount) {
                        fm.popBackStack()
                    }
                    AppNavigator.navigate(
                        this@DashBoardActivity,
                        R.id.fragment_container,
                        FragmentTag.USERS_FRAGMENT, true
                    )
                    Constant.switchMenu = true
                    return true
                } else if (id == R.id.trip_menu) {
                    val fm: FragmentManager = supportFragmentManager
                    for (i in 0 until fm.backStackEntryCount) {
                        fm.popBackStack()
                    }
                    AppNavigator.navigate(
                        this@DashBoardActivity,
                        R.id.fragment_container,
                        FragmentTag.TRIP_FRAGMENT, true
                    )
                    Constant.switchMenu = true
                    return true
                } else if (id == R.id.vehicle_menu) {
                    val fm: FragmentManager = supportFragmentManager
                    for (i in 0 until fm.backStackEntryCount) {
                        fm.popBackStack()
                    }
                    AppNavigator.navigate(
                        this@DashBoardActivity,
                        R.id.fragment_container,
                        FragmentTag.VEHICLES_FRAGMENT, true
                    )
                    Constant.switchMenu = true
                    return true
                } else if (id == R.id.branch_menu) {
                    val fm: FragmentManager = supportFragmentManager
                    for (i in 0 until fm.backStackEntryCount) {
                        fm.popBackStack()
                    }
                    AppNavigator.navigate(
                        this@DashBoardActivity,
                        R.id.fragment_container,
                        FragmentTag.BRANCH_FRAGMENT, true
                    )
                    Constant.switchMenu = true
                    return true
                }
                return true
            }
        })
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton(
                "Yes"
            ) { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton(
                "No"
            ) { dialog, id -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }
    private var deviceList = mutableListOf<DeviceDetailsModel>()

    private fun fetchIDeviceList() {
        //showProgress(requireActivity(), "Fetching devices...")
        val retrofit = ApiClient.instance?.getClient(true, this)

        val call = retrofit?.getDeviceDetailsList()

        RetrofitRequest().enqueue(
            call as Call<List<DeviceDetailsModel>>,
            object : ResponseCallback<List<DeviceDetailsModel>> {
                override fun onSuccess(response: List<DeviceDetailsModel>) {
                    //hideProgress()
                    deviceList.clear()

                    deviceList.addAll(response)
                    Log.d("TAGG", "dealerList $deviceList")

                }

                override fun onFailure(error: String?) {
                    Toast.makeText(this@DashBoardActivity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun checkAndRequestPermissions(): Boolean {
        val camerapermission = ContextCompat.checkSelfPermission(this, permission_group.CAMERA)
        val writepermission =
            ContextCompat.checkSelfPermission(this, permission.WRITE_EXTERNAL_STORAGE)
        val permissionLocation =
            ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
        val permissionLocationCoarse =
            ContextCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (camerapermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(permission_group.CAMERA)
        }
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(permission.ACCESS_FINE_LOCATION)
        }
        if (permissionLocationCoarse != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(permission.ACCESS_COARSE_LOCATION)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                val perms: MutableMap<String, Int> = HashMap()
                // Initialize the map with both permissions
                perms[permission_group.CAMERA] = PackageManager.PERMISSION_GRANTED
                perms[permission.WRITE_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                perms[permission.ACCESS_FINE_LOCATION] = PackageManager.PERMISSION_GRANTED
                perms[permission.ACCESS_COARSE_LOCATION] = PackageManager.PERMISSION_GRANTED
                // Fill with actual results from user
                if (grantResults.size > 0) {
                    var i = 0
                    while (i < permissions.size) {
                        perms[permissions[i]] = grantResults[i]
                        i++
                    }
                    // Check for both permissions
                    if (perms[permission_group.CAMERA] == PackageManager.PERMISSION_GRANTED && perms[permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED && perms[permission.ACCESS_FINE_LOCATION] == PackageManager.PERMISSION_GRANTED && perms[permission.ACCESS_COARSE_LOCATION] == PackageManager.PERMISSION_GRANTED) {
                        /*if (changeValue) {
                            startActivity(Intent(this, MapsActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "No internet connection",
                                Toast.LENGTH_SHORT
                            ).show()
                        }*/
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                permission_group.CAMERA
                            )
                            || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                permission.WRITE_EXTERNAL_STORAGE
                            )
                            || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                permission.ACCESS_FINE_LOCATION
                            )
                            || ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                permission.ACCESS_COARSE_LOCATION
                            )
                        ) {
                            showDialogOK("Service Permissions are required for this app",
                                DialogInterface.OnClickListener { dialog, which ->
                                    when (which) {
                                        DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                        DialogInterface.BUTTON_NEGATIVE ->                                                     // proceed with logic by disabling the related features or quit the app.
                                            finish()
                                    }
                                })
                        } else {
                            //startActivity(new Intent(MainActivity.this, MapsActivity.class));
                            //finish();
                            /*if (com.example.gmapapplication.MainActivity.changeValue) {
                                startActivity(Intent(this, MapsActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    "No internet connection",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }*/
                            //explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?");
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }
    }

    fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getSelectedItem(bottomNavigationView: BottomNavigationView) {
        val menu: Menu = bottomNavigationView.menu
        for (i in 0 until bottomNavigationView.menu.size()) {
            val menuItem: MenuItem = menu.getItem(i)
            var badge = bottomNavigationView.getOrCreateBadge(menuItem.itemId)
            if (menuItem.itemId == R.id.users_menu) {
                badge.number = userCount
                badge.isVisible = true
            }
            if (menuItem.itemId == R.id.trip_menu) {
                badge.number = tripCount
                badge.isVisible = true
            }
            if (menuItem.itemId == R.id.branch_menu) {
                badge.number = branchCount
                badge.isVisible = true
            }
            if (menuItem.itemId == R.id.vehicle_menu) {
                badge.number = vehicleCount
                badge.isVisible = true
            }
            if (menuItem.itemId == R.id.map_menu) {
                badge.isVisible = false
            }

            badge.backgroundColor = getColor(R.color.black)
            badge.badgeGravity = BadgeDrawable.TOP_START

        }
    }

    private fun getUserCount() {
        //Constant.showProgress(activity!!, "loading...")
        val retrofit = ApiClient.instance?.getClient(true, RealEstateApp.context)

        val call = retrofit?.userCount()

        RetrofitRequest().enqueue(
            call as Call<Int>,
            object : ResponseCallback<Int> {
                override fun onSuccess(response: Int) {
                    //Constant.hideProgress()
                    userCount = response
                    Log.d("TAGG", "userCount " + response)
                    getBranchCount()

                }

                override fun onFailure(error: String?) {
                    //Constant.hideProgress()
                    Toast.makeText(this@DashBoardActivity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun getTripCount() {
        //Constant.showProgress(activity!!, "loading...")
        val retrofit = ApiClient.instance?.getClient(true, RealEstateApp.context)

        val call = retrofit?.tripCount()

        RetrofitRequest().enqueue(
            call as Call<Int>,
            object : ResponseCallback<Int> {
                override fun onSuccess(response: Int) {
                    //Constant.hideProgress()
                    tripCount = response
                    Log.d("TAGG", "tripCount " + response)
                    getVehicleCount()

                }

                override fun onFailure(error: String?) {
                    //Constant.hideProgress()
                    Toast.makeText(this@DashBoardActivity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun getBranchCount() {
        //Constant.showProgress(activity!!, "loading...")
        val retrofit = ApiClient.instance?.getClient(true, RealEstateApp.context)

        val call = retrofit?.branchCount()

        RetrofitRequest().enqueue(
            call as Call<Int>,
            object : ResponseCallback<Int> {
                override fun onSuccess(response: Int) {
                    //Constant.hideProgress()
                    branchCount = response
                    Log.d("TAGG", "branchCount " + response)
                    getTripCount()

                }

                override fun onFailure(error: String?) {
                    //Constant.hideProgress()
                    Toast.makeText(this@DashBoardActivity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun getVehicleCount() {
        //Constant.showProgress(activity!!, "loading...")
        val retrofit = ApiClient.instance?.getClient(true, RealEstateApp.context)

        val call = retrofit?.vehicleCount()

        RetrofitRequest().enqueue(
            call as Call<Int>,
            object : ResponseCallback<Int> {
                @RequiresApi(Build.VERSION_CODES.M)
                override fun onSuccess(response: Int) {
                    //Constant.hideProgress()
                    vehicleCount = response
                    Log.d("TAGG", "vehicleCount " + response)
                    if (bottom_navigation1 != null) {
                        getSelectedItem(bottom_navigation1)
                    }


                }

                override fun onFailure(error: String?) {
                    //Constant.hideProgress()
                    Toast.makeText(this@DashBoardActivity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    override fun onResume() {
        super.onResume()
        getUserCount()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            //RealEstateApp.sInstance.setStopTrue()
            /*SharedPreferenceManager.getInstance(this@DashBoardActivity)?.putBoolean(
                SharedPreferenceManager.Key.IS_LOGGEDIN, false
            )*/
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun drawerOpenClose() {

        if (imgViewBack.tag.equals("menu")) {

            if (drawer_layout?.isDrawerOpen(GravityCompat.START)!!) {
                drawer_layout?.closeDrawer(GravityCompat.START)
            } else {
                drawer_layout?.openDrawer(GravityCompat.START)

                setProfileImage("")
            }
        } else {
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            val count = supportFragmentManager.backStackEntryCount

            if (count == 0) {
                finish()
            } else if (count >= 2 && Constant.switchMenu) {
                val tag =
                    supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1)
                        .name

                if (tag.equals(FragmentTag.USERS_FRAGMENT) ||
                    tag.equals(FragmentTag.INSURANCE_FRAGMENT) ||
                    tag.equals(FragmentTag.MATERIALS_FRAGMENT) ||
                    tag.equals(FragmentTag.TRIP_FRAGMENT) ||
                    tag.equals(FragmentTag.VEHICLES_FRAGMENT) ||
                    tag.equals(FragmentTag.INVOICE_FRAGMENT) ||
                    tag.equals(FragmentTag.BRANCH_FRAGMENT) ||
                    tag.equals(FragmentTag.COMPANIES_FRAGMENT) ||
                    tag.equals(FragmentTag.DEALER_FRAGMENT) ||
                    tag.equals(FragmentTag.PERMIT_FRAGMENT) ||
                    tag.equals(FragmentTag.POLLUTION_FRAGMENT) ||
                    tag.equals(FragmentTag.ROLES_FRAGMENT) ||
                    tag.equals(FragmentTag.PROFILE_FRAGMENT) ||
                    tag.equals(FragmentTag.USER_DETAILS_FRAGMENT) ||
                    tag.equals(FragmentTag.TRIP_DETAILS_FRAGMENT) ||
                    tag.equals(FragmentTag.INVOICE_DETAILS_FRAGMENT)
                ) {
                    supportFragmentManager.popBackStack()
                    add_img.visibility = View.VISIBLE
                    if (Constant.switchMenu) {
                        bottom_navigation1.visibility = View.VISIBLE
                    } else {
                        dashBoard(true)
                    }
                }

                val tag1 =
                    supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1)
                        .name

                Log.i("FragmentActivity.TAG", "Found check fragment: " + tag1)

                /*if (tag1.equals(FragmentTag.USERS_FRAGMENT)) {
                    if (bottom_navigation1 != null) {
                        bottom_navigation1.visibility = View.VISIBLE
                        bottom_navigation1.menu.getItem(0).setChecked(true)
                        Constant.switchMenu = true
                        Log.i("FragmentActivity.TAG", "Found USERS_FRAGMENT check true ")
                        AppNavigator.navigate(
                            this,
                            R.id.fragment_container,
                            FragmentTag.USERS_FRAGMENT, false
                        )
                    }
                }
                if (tag1.equals(FragmentTag.TRIP_FRAGMENT)) {
                    if (bottom_navigation1 != null) {
                        Constant.switchMenu = true
                        Log.i("FragmentActivity.TAG", "Found TRIP_FRAGMENT check true ")
                        bottom_navigation1.visibility = View.VISIBLE
                        bottom_navigation1.menu.getItem(1).setChecked(true)
                        AppNavigator.navigate(
                            this,
                            R.id.fragment_container,
                            FragmentTag.TRIP_FRAGMENT, false
                        )
                    }

                }
                if (tag1.equals(FragmentTag.VEHICLES_FRAGMENT)) {
                    if (bottom_navigation1 != null) {
                        bottom_navigation1.visibility = View.VISIBLE
                        bottom_navigation1.menu.getItem(3).setChecked(true)
                        AppNavigator.navigate(
                            this,
                            R.id.fragment_container,
                            FragmentTag.VEHICLES_FRAGMENT, false
                        )
                    }
                }
                if (tag1.equals(FragmentTag.BRANCH_FRAGMENT)) {
                    if (bottom_navigation1 != null) {
                        bottom_navigation1.visibility = View.VISIBLE
                        bottom_navigation1.menu.getItem(2).setChecked(true)
                    }
                    AppNavigator.navigate(
                        this,
                        R.id.fragment_container,
                        FragmentTag.BRANCH_FRAGMENT, false
                    )
                }*/

            } else {
                val tag =
                    supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1)
                        .name

                if (tag.equals(FragmentTag.USERS_FRAGMENT) ||
                    tag.equals(FragmentTag.INSURANCE_FRAGMENT) ||
                    tag.equals(FragmentTag.MATERIALS_FRAGMENT) ||
                    tag.equals(FragmentTag.TRIP_FRAGMENT) ||
                    tag.equals(FragmentTag.VEHICLES_FRAGMENT) ||
                    tag.equals(FragmentTag.INVOICE_FRAGMENT) ||
                    tag.equals(FragmentTag.BRANCH_FRAGMENT) ||
                    tag.equals(FragmentTag.COMPANIES_FRAGMENT) ||
                    tag.equals(FragmentTag.DEALER_FRAGMENT) ||
                    tag.equals(FragmentTag.PERMIT_FRAGMENT) ||
                    tag.equals(FragmentTag.POLLUTION_FRAGMENT) ||
                    tag.equals(FragmentTag.ROLES_FRAGMENT) ||
                    tag.equals(FragmentTag.PROFILE_FRAGMENT) ||
                    tag.equals(FragmentTag.USER_DETAILS_FRAGMENT) ||
                    tag.equals(FragmentTag.TRIP_DETAILS_FRAGMENT) ||
                    tag.equals(FragmentTag.INVOICE_DETAILS_FRAGMENT)
                ) {
                    supportFragmentManager.popBackStackImmediate();
                    add_img.visibility = View.VISIBLE
                    //supportFragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                }
                if (count == 1) {
                    dashBoard(true)
                }
            }
        }
    }

    override fun onClick(p0: View?) {
        if (p0?.id != R.id.imgViewBack) {
            val fm: FragmentManager = supportFragmentManager
            for (i in 0 until fm.backStackEntryCount) {
                fm.popBackStack()
            }
        }

        when (p0?.id) {
            R.id.imgViewBack -> {
                drawerOpenClose()
            }
            R.id.dashboard_txt -> {
                dashBoard(false)
            }
            R.id.user_txt -> {
                user()
            }
            R.id.material_txt -> {
                material()
            }
            R.id.trip_txt -> {
                trip()
            }
            R.id.vehical_txt -> {
                vehicles()
            }
            R.id.insurance_txt -> {
                insurance()
            }
            R.id.invoice_txt -> {
                invoice()

            }
            R.id.branch_txt -> {
                branch()

            }
            R.id.companies_txt -> {
                companies()

            }
            R.id.dealer_txt -> {
                dealer()

            }
            R.id.permit_txt -> {
                permit()

            }
            R.id.pollution_txt -> {
                pollution()

            }
            R.id.role_txt -> {
                roles()

            }
            R.id.profile_layout -> {
                profile()

            }
            R.id.logout_txt -> {
                logout()

            }

        }
    }

    private fun logout() {
        Constant.showDialog(this, "Attention", "Do you want to logout ?", object : DialogCallback {
            override fun onOk(ok: Boolean) {
                if (ok) {
                    SharedPreferenceManager.getInstance(this@DashBoardActivity)?.putBoolean(
                        SharedPreferenceManager.Key.IS_LOGGEDIN, false
                    )
                    startActivity(Intent(this@DashBoardActivity, LoginActivity::class.java))
                    this@DashBoardActivity.finish()
                }
            }

            override fun onCancel(cancel: Boolean) {

            }
        })

        /* this.overridePendingTransition(R.anim.slide_left,
        R.anim.slide_left);*/
    }

    override fun onBackPressed() {
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            return
        }
        val count = supportFragmentManager.backStackEntryCount
        Log.i("FragmentActivity.TAG", "Found count fragment: " + count)

        if ((count == 0 || count == 1) && Constant.switchMenu) {
            this.finishAffinity()
        } else if (count >= 2 && Constant.switchMenu) {

            val tag =
                supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1)
                    .name

            Log.i("FragmentActivity.TAG", "Found pop fragment: " + tag)
            if (tag.equals(FragmentTag.USERS_FRAGMENT) ||
                tag.equals(FragmentTag.INSURANCE_FRAGMENT) ||
                tag.equals(FragmentTag.MATERIALS_FRAGMENT) ||
                tag.equals(FragmentTag.TRIP_FRAGMENT) ||
                tag.equals(FragmentTag.VEHICLES_FRAGMENT) ||
                tag.equals(FragmentTag.INVOICE_FRAGMENT) ||
                tag.equals(FragmentTag.BRANCH_FRAGMENT) ||
                tag.equals(FragmentTag.COMPANIES_FRAGMENT) ||
                tag.equals(FragmentTag.DEALER_FRAGMENT) ||
                tag.equals(FragmentTag.PERMIT_FRAGMENT) ||
                tag.equals(FragmentTag.POLLUTION_FRAGMENT) ||
                tag.equals(FragmentTag.ROLES_FRAGMENT) ||
                tag.equals(FragmentTag.PROFILE_FRAGMENT) ||
                tag.equals(FragmentTag.USER_DETAILS_FRAGMENT) ||
                tag.equals(FragmentTag.TRIP_DETAILS_FRAGMENT) ||
                tag.equals(FragmentTag.INVOICE_DETAILS_FRAGMENT)
            ) {
                supportFragmentManager.popBackStackImmediate()
                add_img.visibility = View.VISIBLE
                if (Constant.switchMenu) {
                    bottom_navigation1.visibility = View.VISIBLE
                } else {
                    dashBoard(true)
                }
                //supportFragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            }

            /*val tag1 =
                supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1)
                    .name

            Log.i("FragmentActivity.TAG", "Found check fragment: " + tag1)


            if (tag1.equals(FragmentTag.USERS_FRAGMENT)) {
                if (bottom_navigation1 != null) {
                    bottom_navigation1.visibility = View.VISIBLE
                    bottom_navigation1.menu.getItem(0).setChecked(true)
                    Log.i("FragmentActivity.TAG", "Found USERS_FRAGMENT check true ")
                    AppNavigator.navigate(
                        this,
                        R.id.fragment_container,
                        FragmentTag.USERS_FRAGMENT, false
                    )
                }
            }
            if (tag1.equals(FragmentTag.TRIP_FRAGMENT)) {
                if (bottom_navigation1 != null) {
                    Log.i("FragmentActivity.TAG", "Found TRIP_FRAGMENT check true ")
                    bottom_navigation1.visibility = View.VISIBLE
                    bottom_navigation1.menu.getItem(1).setChecked(true)
                    AppNavigator.navigate(
                        this,
                        R.id.fragment_container,
                        FragmentTag.TRIP_FRAGMENT, false
                    )
                }

            }
            if (tag1.equals(FragmentTag.VEHICLES_FRAGMENT)) {
                if (bottom_navigation1 != null) {
                    bottom_navigation1.visibility = View.VISIBLE
                    bottom_navigation1.menu.getItem(3).setChecked(true)
                    AppNavigator.navigate(
                        this,
                        R.id.fragment_container,
                        FragmentTag.VEHICLES_FRAGMENT, false
                    )
                }
            }
            if (tag1.equals(FragmentTag.BRANCH_FRAGMENT)) {
                if (bottom_navigation1 != null) {
                    bottom_navigation1.visibility = View.VISIBLE
                    bottom_navigation1.menu.getItem(2).setChecked(true)
                }
                AppNavigator.navigate(
                    this,
                    R.id.fragment_container,
                    FragmentTag.BRANCH_FRAGMENT, false
                )
            }*/

        } else {
            val tag =
                supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1)
                    .name

            if (tag.equals(FragmentTag.USERS_FRAGMENT) ||
                tag.equals(FragmentTag.INSURANCE_FRAGMENT) ||
                tag.equals(FragmentTag.MATERIALS_FRAGMENT) ||
                tag.equals(FragmentTag.TRIP_FRAGMENT) ||
                tag.equals(FragmentTag.VEHICLES_FRAGMENT) ||
                tag.equals(FragmentTag.INVOICE_FRAGMENT) ||
                tag.equals(FragmentTag.BRANCH_FRAGMENT) ||
                tag.equals(FragmentTag.COMPANIES_FRAGMENT) ||
                tag.equals(FragmentTag.DEALER_FRAGMENT) ||
                tag.equals(FragmentTag.PERMIT_FRAGMENT) ||
                tag.equals(FragmentTag.POLLUTION_FRAGMENT) ||
                tag.equals(FragmentTag.ROLES_FRAGMENT) ||
                tag.equals(FragmentTag.PROFILE_FRAGMENT) ||
                tag.equals(FragmentTag.USER_DETAILS_FRAGMENT) ||
                tag.equals(FragmentTag.TRIP_DETAILS_FRAGMENT) ||
                tag.equals(FragmentTag.INVOICE_DETAILS_FRAGMENT)
            ) {
                supportFragmentManager.popBackStackImmediate();
                add_img.visibility = View.VISIBLE
                //supportFragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            }
            if (count == 1) {
                dashBoard(true)
            }

        }

    }

    private fun dashBoard(isFirst: Boolean) {
        if (isFirst) {
            AppNavigator.navigate(
                this,
                R.id.fragment_container,
                FragmentTag.USERS_FRAGMENT, true
            )
        } else {
            AppNavigator.navigate(
                this,
                R.id.fragment_container,
                FragmentTag.USERS_FRAGMENT, false
            )
        }
        if (bottom_navigation1 != null) {
            bottom_navigation1.menu.getItem(0).setChecked(true)
        }
        bottom_navigation1.visibility = View.VISIBLE
        Constant.switchMenu = true
        imgViewBack.setImageResource(R.drawable.ic_menu)
        imgViewBack.tag = "menu"
        txtViewToolbarTitle.setText(R.string.dash_board)
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun user() {
        AppNavigator.navigate(
            this,
            R.id.fragment_container,
            FragmentTag.USERS_FRAGMENT, true
        )
        bottom_navigation1.visibility = View.GONE
        Constant.switchMenu = false
        imgViewBack.setImageResource(R.drawable.ic_back)
        imgViewBack.tag = "back"
        txtViewToolbarTitle.setText(R.string.users)
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun trip() {
        AppNavigator.navigate(
            this,
            R.id.fragment_container,
            FragmentTag.TRIP_FRAGMENT, true
        )
        bottom_navigation1.visibility = View.GONE
        Constant.switchMenu = false
        imgViewBack.setImageResource(R.drawable.ic_back)
        imgViewBack.tag = "back"
        txtViewToolbarTitle.setText(R.string.trip)
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun material() {
        AppNavigator.navigate(
            this,
            R.id.fragment_container,
            FragmentTag.MATERIALS_FRAGMENT, true
        )
        bottom_navigation1.visibility = View.GONE
        Constant.switchMenu = false
        imgViewBack.setImageResource(R.drawable.ic_back)
        imgViewBack.tag = "back"
        txtViewToolbarTitle.setText(R.string.material)
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun vehicles() {
        AppNavigator.navigate(
            this,
            R.id.fragment_container,
            FragmentTag.VEHICLES_FRAGMENT, true
        )
        bottom_navigation1.visibility = View.GONE
        Constant.switchMenu = false
        imgViewBack.setImageResource(R.drawable.ic_back)
        imgViewBack.tag = "back"
        txtViewToolbarTitle.setText(R.string.vehical)
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun insurance() {
        AppNavigator.navigate(
            this,
            R.id.fragment_container,
            FragmentTag.INSURANCE_FRAGMENT, true
        )
        bottom_navigation1.visibility = View.GONE
        Constant.switchMenu = false
        imgViewBack.setImageResource(R.drawable.ic_back)
        imgViewBack.tag = "back"
        txtViewToolbarTitle.setText(R.string.insurance)
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun invoice() {
        AppNavigator.navigate(
            this,
            R.id.fragment_container,
            FragmentTag.INVOICE_FRAGMENT, true
        )
        bottom_navigation1.visibility = View.GONE
        Constant.switchMenu = false
        imgViewBack.setImageResource(R.drawable.ic_back)
        imgViewBack.tag = "back"
        txtViewToolbarTitle.setText(R.string.invoice)
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun branch() {
        AppNavigator.navigate(
            this,
            R.id.fragment_container,
            FragmentTag.BRANCH_FRAGMENT, true
        )
        bottom_navigation1.visibility = View.GONE
        Constant.switchMenu = false
        imgViewBack.setImageResource(R.drawable.ic_back)
        imgViewBack.tag = "back"
        txtViewToolbarTitle.setText(R.string.branch)
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

    }

    private fun dealer() {
        AppNavigator.navigate(
            this,
            R.id.fragment_container,
            FragmentTag.DEALER_FRAGMENT, true
        )
        bottom_navigation1.visibility = View.GONE
        Constant.switchMenu = false
        imgViewBack.setImageResource(R.drawable.ic_back)
        imgViewBack.tag = "back"
        txtViewToolbarTitle.setText(R.string.dealer)
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

    }

    private fun permit() {
        AppNavigator.navigate(
            this,
            R.id.fragment_container,
            FragmentTag.PERMIT_FRAGMENT, true
        )
        bottom_navigation1.visibility = View.GONE
        Constant.switchMenu = false
        imgViewBack.setImageResource(R.drawable.ic_back)
        imgViewBack.tag = "back"
        txtViewToolbarTitle.setText(R.string.permit)
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

    }

    private fun pollution() {
        AppNavigator.navigate(
            this,
            R.id.fragment_container,
            FragmentTag.POLLUTION_FRAGMENT, true
        )
        bottom_navigation1.visibility = View.GONE
        Constant.switchMenu = false
        imgViewBack.setImageResource(R.drawable.ic_back)
        imgViewBack.tag = "back"
        txtViewToolbarTitle.setText(R.string.pollution)
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

    }

    private fun companies() {
        AppNavigator.navigate(
            this,
            R.id.fragment_container,
            FragmentTag.COMPANIES_FRAGMENT, true
        )
        bottom_navigation1.visibility = View.GONE
        Constant.switchMenu = false
        imgViewBack.setImageResource(R.drawable.ic_back)
        imgViewBack.tag = "back"
        txtViewToolbarTitle.setText(R.string.companies)
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

    }

    private fun roles() {
        AppNavigator.navigate(
            this,
            R.id.fragment_container,
            FragmentTag.ROLES_FRAGMENT, true
        )
        bottom_navigation1.visibility = View.GONE
        Constant.switchMenu = false
        imgViewBack.setImageResource(R.drawable.ic_back)
        imgViewBack.tag = "back"
        txtViewToolbarTitle.setText(R.string.role)
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun profile() {
        AppNavigator.navigate(
            this,
            R.id.fragment_container,
            FragmentTag.PROFILE_FRAGMENT, true
        )
        bottom_navigation1.visibility = View.GONE
        Constant.switchMenu = false
        imgViewBack.setImageResource(R.drawable.ic_back)
        imgViewBack.tag = "back"
        txtViewToolbarTitle.setText(R.string.profile)
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun setProfileImage(imageCompletePath: String) {
        Glide.with(this).load(imageCompletePath.ifEmpty { R.drawable.user })
            .placeholder(R.drawable.user).into(
                imgProfile
            )
    }

    override fun onUpdate(isUpdate: Boolean) {
        Log.d("TAGG", "isUpdate $isUpdate")
        getUserCount()
    }
}