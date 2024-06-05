package com.realestate.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.realestate.R
import com.realestate.adapter.recycler.TripAdapter
import com.realestate.callback.AddUpdateListener
import com.realestate.callback.DialogCallback
import com.realestate.callback.ItemClickListener
import com.realestate.callback.ResponseCallback
import com.realestate.model.*
import com.realestate.restapi.ApiClient
import com.realestate.restapi.Payload
import com.realestate.restapi.RetrofitRequest
import com.realestate.utils.*
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.fragment_trip.*
import kotlinx.android.synthetic.main.progress_dialog.*
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.RequestBody
import retrofit2.Call
import java.io.Serializable

/**
 * A simple [Fragment] subclass.
 * Use the [TripFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TripFragment : Fragment(), ItemClickListener, SearchView.OnQueryTextListener {

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var tripAdapter: TripAdapter
    private var tripList = mutableListOf<TripModel>()
    private var dealerList = mutableListOf<DealerModel>()
    private var vehicleList = mutableListOf<VehicleModel>()
    private var companyList = mutableListOf<CompanyModel>()
    private var driverList = mutableListOf<DriverModel>()
    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Constant.switchMenu) {
            requireActivity().runOnUiThread(Runnable {
                try {
                    requireActivity().txtViewToolbarTitle.text =
                        activity?.resources?.getString(R.string.trip)
                    activity?.imgViewBack?.setImageResource(R.drawable.ic_menu)
                    activity?.imgViewBack?.tag = "menu"
                } catch (e: Exception) {

                }
            })
        } else {
            requireActivity().runOnUiThread(Runnable {
                try {
                    requireActivity().txtViewToolbarTitle.text =
                        activity?.resources?.getString(R.string.trip)
                    activity?.imgViewBack?.setImageResource(R.drawable.ic_back)
                    activity?.imgViewBack?.tag = "back"
                } catch (e: Exception) {

                }
            })
        }

        initVariable()
        fetchCompany()

        fetchTrip()
    }

    private fun initVariable() {
        trip_search.setOnQueryTextListener(this)
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        trip_recyclerview.layoutManager = layoutManager
        trip_recyclerview.setHasFixedSize(false)
        tripAdapter = TripAdapter(
            requireActivity(),
            tripList,
            this
        )
        trip_recyclerview.adapter = tripAdapter

        activity?.add_img?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var bundle = Bundle()
                bundle.putSerializable("driver_list", driverList as Serializable)
                bundle.putSerializable("vehicle_list", vehicleList as Serializable)
                bundle.putSerializable("company_list", companyList as Serializable)
                bundle.putSerializable("dealer_list", dealerList as Serializable)
                val dialog = CustomDialogFragment.newInstance(
                    requireActivity(),
                    object : AddUpdateListener {
                        override fun onAddOrUpdate(ok: Boolean, model: Payload?) {
                            if (ok) {
                                addTrip(model!!)
                                Log.d("TAGG", "Payload $model")
                            }
                        }

                        override fun onCancel(cancel: Boolean) {

                        }
                    }, bundle,
                    R.layout.add_trip_dialog_fragment, "Add Trip", null
                )
                //dialog.isCancelable = false
                dialog.arguments = bundle
                dialog.show(activity?.supportFragmentManager!!, CustomDialogFragment.TAG)
            }

        })

        activity?.refresh_img?.visibility = View.VISIBLE
        activity?.add_img?.visibility = View.VISIBLE
        activity?.filter_spinner?.visibility = View.GONE

        activity?.refresh_img?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                fetchCompany()
                fetchDealer()
                fetchDriver()
                fetchVehicle()
                fetchTrip()
            }

        })
    }

    override fun onResume() {
        super.onResume()
        activity?.refresh_img?.visibility = View.VISIBLE
        activity?.add_img?.visibility = View.VISIBLE
    }


    override fun onItemClick(view: View, position: Int) {
        if (view.id == R.id.trip_option_img) {
            showPopupMenu(view, tripList.get(position))
        } else {
            var bundle = Bundle()
            bundle.putParcelable("trip_details", tripList.get(position))
            //Constant.switchMenu = true
            requireActivity().bottom_navigation1.visibility = View.GONE
            if (Constant.switchMenu) {
                AppNavigator.navigate(
                    requireActivity(),
                    R.id.fragment_container,
                    FragmentTag.TRIP_DETAILS_FRAGMENT, true, bundle
                )
            } else {
                AppNavigator.navigate(
                    requireActivity(),
                    R.id.fragment_container,
                    FragmentTag.TRIP_DETAILS_FRAGMENT, true, bundle
                )
            }
        }
    }

    private fun showPopupMenu(view: View, trip: TripModel) {
        var popupMenu = PopupMenu(requireActivity(), view)
        popupMenu.inflate(R.menu.pop_menu)
        PopupIcon().insertMenuItemIcons(requireActivity(), popupMenu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.details -> {
                    requireActivity().bottom_navigation1.visibility = View.GONE
                    var bundle = Bundle()
                    bundle.putParcelable("trip_details", trip)
                    if (Constant.switchMenu) {
                        AppNavigator.navigate(
                            requireActivity(),
                            R.id.fragment_container,
                            FragmentTag.TRIP_DETAILS_FRAGMENT, true, bundle
                        )
                    } else {
                        AppNavigator.navigate(
                            requireActivity(),
                            R.id.fragment_container,
                            FragmentTag.TRIP_DETAILS_FRAGMENT, true, bundle
                        )
                    }
                }
                R.id.edit -> {
                    var bundle = Bundle()
                    bundle.putSerializable("driver_list", driverList as Serializable)
                    bundle.putSerializable("vehicle_list", vehicleList as Serializable)
                    bundle.putSerializable("company_list", companyList as Serializable)
                    bundle.putSerializable("dealer_list", dealerList as Serializable)
                    bundle.putParcelable("trip_data", trip)

                    val dialog = CustomDialogFragment.newInstance(
                        requireActivity(),
                        object : AddUpdateListener {
                            override fun onAddOrUpdate(ok: Boolean, model: Payload?) {
                                Log.d("TAG", "Update model $model")
                                if (ok)
                                    updateTrip(trip.id, model!!)
                            }

                            override fun onCancel(cancel: Boolean) {

                            }
                        }, bundle, R.layout.add_trip_dialog_fragment, "Update Trip", "Update"
                    )
                    //dialog.isCancelable = false
                    dialog.arguments = bundle
                    dialog.show(requireFragmentManager(), CustomDialogFragment.TAG)
                }
                R.id.delete -> {
                    Constant.showDialog(requireActivity(),
                        "Delete trip",
                        "Do you want to delete this trip?",
                        object : DialogCallback {
                            override fun onOk(ok: Boolean) {
                                if (ok) {
                                    deleteTrip(tripId = trip.id)
                                }
                            }

                            override fun onCancel(cancel: Boolean) {

                            }
                        })
                }
            }
            false
        })
        popupMenu.show()
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            TripFragment()
    }

    private fun deleteTrip(tripId: Int) {
        showProgress(requireActivity(), "Deleting trip...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        /*val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            payload.toString()
        )*/
        val call = retrofit?.deleteTrip(tripId)

        RetrofitRequest().enqueue(
            call as Call<TripModel>,
            object : ResponseCallback<TripModel> {
                override fun onSuccess(response: TripModel) {
                    hideProgress()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
//                    if (response.id == userId) {
                    Toast.makeText(activity, "Trip delete successfully", Toast.LENGTH_LONG)
                        .show()
                    fetchTrip()
                    /*} else {
                        Toast.makeText(activity, "Failed to update trip", Toast.LENGTH_LONG)
                            .show()
                    }*/
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Log.d("TAG", "Error $error")
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun updateTrip(id: Int, payload: Payload) {
        showProgress(requireActivity(), "Update trip...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            payload.toString()
        )
        val call = retrofit?.updateTrip(id, body)

        RetrofitRequest().enqueue(
            call as Call<TripModel>,
            object : ResponseCallback<TripModel> {
                override fun onSuccess(response: TripModel) {
                    hideProgress()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
//                    if (response.id == userId) {
                    Toast.makeText(activity, "Trip updated successfully", Toast.LENGTH_LONG)
                        .show()
                    fetchTrip()
                    /*} else {
                        Toast.makeText(activity, "Failed to update trip", Toast.LENGTH_LONG)
                            .show()
                    }*/
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Log.d("TAG", "Error $error")
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun addTrip(payload: Payload) {
        showProgress(requireActivity(), "Adding trip...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            payload.toString()
        )
        val call = retrofit?.addTrip(body)

        RetrofitRequest().enqueue(
            call as Call<TripModel>,
            object : ResponseCallback<TripModel> {
                override fun onSuccess(response: TripModel) {
                    hideProgress()
                    Toast.makeText(activity, "Trip added successfully", Toast.LENGTH_LONG)
                        .show()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
//                    tripList.clear()
//                    tripList.addAll(response)
//                    tripAdapter.notifyDataSetChanged()
//                    updateListener.onUpdate(true)
                    fetchTrip()
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun fetchTrip() {
        showProgress(requireActivity(), "Fetching trip...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val call = retrofit?.getAllTrip()

        RetrofitRequest().enqueue(
            call as Call<List<TripModel>>,
            object : ResponseCallback<List<TripModel>> {
                override fun onSuccess(response: List<TripModel>) {
                    hideProgress()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    tripList.clear()
                    tripList.addAll(response)
                    val controller: LayoutAnimationController
                    try {
                        if (tripList.size > 0) {
                            controller =
                                AnimationUtils.loadLayoutAnimation(
                                    context,
                                    R.anim.layout_animation_right_to_left
                                );

                        } else {
                            controller =
                                AnimationUtils.loadLayoutAnimation(
                                    context,
                                    R.anim.layout_animation_down_to_up
                                );

                        }

                        trip_recyclerview?.setLayoutAnimation(controller);
                    } catch (e: Exception) {

                    }

                    tripAdapter.notifyDataSetChanged()
                    trip_recyclerview?.scheduleLayoutAnimation()
                    //updateListener.onUpdate(true)
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun fetchDealer() {
        //showProgress(requireActivity(), "Fetching trip...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val call = retrofit?.getAllDealer()

        RetrofitRequest().enqueue(
            call as Call<List<DealerModel>>,
            object : ResponseCallback<List<DealerModel>> {
                override fun onSuccess(response: List<DealerModel>) {
                    hideProgress()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    dealerList.clear()
                    (response as MutableList).add(
                        0,
                        DealerModel(1234, "Select dealer no", "", "", "Select dealer no", "", -1)
                    )

                    dealerList.addAll(response)
                    Log.d("TAGG", "dealerList $dealerList")

                    fetchDriver()

                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun fetchCompany() {
        //showProgress(requireActivity(), "Fetching trip...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val call = retrofit?.getAllCompany()

        RetrofitRequest().enqueue(
            call as Call<List<CompanyModel>>,
            object : ResponseCallback<List<CompanyModel>> {
                override fun onSuccess(response: List<CompanyModel>) {
                    hideProgress()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    companyList.clear()
                    (response as MutableList).add(
                        0,
                        CompanyModel("", null, "Select company name", -1, -1)
                    )

                    companyList.addAll(response)
                    fetchDealer()
                    Log.d("TAGG", "companyList $companyList")

                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun fetchVehicle() {
        //showProgress(requireActivity(), "Fetching trip...")
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
                        VehicleModel("", -1, "Select Registration no", "", "", "", null, null, null,null, null, null)
                    )

                    vehicleList.addAll(response)
                    Log.d("TAGG", "vehicleList $vehicleList")

                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun fetchDriver() {
        //showProgress(requireActivity(), "Fetching trip...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val call = retrofit?.getAllDriver()

        RetrofitRequest().enqueue(
            call as Call<List<DriverModel>>,
            object : ResponseCallback<List<DriverModel>> {
                override fun onSuccess(response: List<DriverModel>) {
                    hideProgress()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    driverList.clear()
                    (response as MutableList).add(
                        0,
                        DriverModel(
                            "name",
                            "",
                            null,
                            null,
                            -1,
                            "",
                            "",
                            "Select driver",
                            "",
                            "",
                            "",
                            "",
                            -1,
                            ""
                        )
                    )
                    driverList.addAll(response)
                    Log.d("TAGG", "driverList $driverList")
                    fetchVehicle()
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
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

    override fun onQueryTextSubmit(query: String?): Boolean {
        tripAdapter.filter.filter(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        tripAdapter.filter.filter(newText)
        return false
    }

}