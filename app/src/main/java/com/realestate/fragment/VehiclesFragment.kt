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
import com.realestate.activity.DashBoardActivity
import com.realestate.adapter.recycler.VehicleAdapter
import com.realestate.callback.*
import com.realestate.model.VehicleModel
import com.realestate.restapi.ApiClient
import com.realestate.restapi.Payload
import com.realestate.restapi.RetrofitRequest
import com.realestate.utils.Constant
import com.realestate.utils.CustomDialogFragment
import com.realestate.utils.PopupIcon
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.fragment_vehicles.*
import kotlinx.android.synthetic.main.progress_dialog.*
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.RequestBody
import retrofit2.Call

/**
 * A simple [Fragment] subclass.
 * Use the [VehiclesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VehiclesFragment : Fragment(), ItemClickListener, SearchView.OnQueryTextListener {

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var vehicleAdapter: VehicleAdapter
    private var vehicleList = mutableListOf<VehicleModel>()
    private var dialog: Dialog? = null
    private lateinit var countListener: CountUpdateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        countListener = requireActivity() as CountUpdateListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vehicles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Constant.switchMenu) {
            requireActivity().runOnUiThread(Runnable {
                try {
                    requireActivity().txtViewToolbarTitle.text =
                        activity?.resources?.getString(R.string.vehical)
                    activity?.imgViewBack?.setImageResource(R.drawable.ic_menu)
                    activity?.imgViewBack?.tag = "menu"
                } catch (e: Exception) {

                }
            })
        } else {
            requireActivity().runOnUiThread(Runnable {
                try {
                    requireActivity().txtViewToolbarTitle.text =
                        activity?.resources?.getString(R.string.vehical)
                    activity?.imgViewBack?.setImageResource(R.drawable.ic_back)
                    activity?.imgViewBack?.tag = "back"
                } catch (e: Exception) {

                }
            })
        }

        initVariable()
        fetchVehicle()
    }

    private fun initVariable() {
        vehicle_search.setOnQueryTextListener(this)
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        vehicle_recyclerview.layoutManager = layoutManager
        vehicle_recyclerview.setHasFixedSize(false)
        vehicleAdapter = VehicleAdapter(
            requireActivity(),
            vehicleList,
            this
        )
        vehicle_recyclerview.adapter = vehicleAdapter

        add_vehicles_fab.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val dialog = CustomDialogFragment.newInstance(
                    requireActivity(),
                    object : AddUpdateListener {
                        override fun onAddOrUpdate(ok: Boolean, model: Payload?) {

                        }

                        override fun onCancel(cancel: Boolean) {

                        }
                    },
                    null, R.layout.add_vehicle_dialog_fragment, "Add Vehicle", null
                )
                //dialog.isCancelable = false
                dialog.show(requireFragmentManager(), CustomDialogFragment.TAG)
            }

        })

        activity?.refresh_img?.visibility = View.VISIBLE
        activity?.add_img?.visibility = View.VISIBLE
        activity?.filter_spinner?.visibility = View.GONE

        activity?.refresh_img?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                fetchVehicle()
            }
        })

        activity?.add_img?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val dialog = CustomDialogFragment.newInstance(
                    requireActivity(),
                    object : AddUpdateListener {
                        override fun onAddOrUpdate(ok: Boolean, model: Payload?) {
                            if (ok)
                                addVehicle(model!!)
                        }

                        override fun onCancel(cancel: Boolean) {

                        }
                    },
                    null, R.layout.add_vehicle_dialog_fragment, "Add Vehicle", null
                )
                //dialog.isCancelable = false
                dialog.show(requireFragmentManager(), CustomDialogFragment.TAG)
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
                    vehicleList.addAll(response)

                    val controller: LayoutAnimationController

                    try {

                        if (vehicleList.size > 0) {
                            controller = AnimationUtils.loadLayoutAnimation(
                                context,
                                R.anim.layout_animation_right_to_left
                            );

                        } else {
                            controller = AnimationUtils.loadLayoutAnimation(
                                context,
                                R.anim.layout_animation_down_to_up
                            );

                        }

                        vehicle_recyclerview?.setLayoutAnimation(controller);
                    } catch (e: Exception) {

                    }
                    vehicleAdapter.notifyDataSetChanged()
                    vehicle_recyclerview?.scheduleLayoutAnimation()
                    //updateListener.onUpdate(true)
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun addVehicle(payload: Payload) {
        showProgress(requireActivity(), "Adding vehicle...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            payload.toString()
        )
        val call = retrofit?.addVehicle(body)

        RetrofitRequest().enqueue(
            call as Call<VehicleModel>,
            object : ResponseCallback<VehicleModel> {
                override fun onSuccess(response: VehicleModel) {
                    hideProgress()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    fetchVehicle()
                    //countListener.onUpdate(true)
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun updateVehicle(id: Int, payload: Payload) {
        showProgress(requireActivity(), "Updating vehicle...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            payload.toString()
        )
        val call = retrofit?.updateVehicle(id, body)

        RetrofitRequest().enqueue(
            call as Call<VehicleModel>,
            object : ResponseCallback<VehicleModel> {
                override fun onSuccess(response: VehicleModel) {
                    hideProgress()
                    Toast.makeText(requireActivity(), "Vehicle updated successfully", Toast.LENGTH_SHORT)
                        .show()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    fetchVehicle()
                    countListener.onUpdate(true)
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun deleteVehicle(id: Int) {
        showProgress(requireActivity(), "Deleting vehicle...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val call = retrofit?.deleteVehicle(id)

        RetrofitRequest().enqueue(
            call as Call<VehicleModel>,
            object : ResponseCallback<VehicleModel> {
                override fun onSuccess(response: VehicleModel) {
                    hideProgress()
                    Toast.makeText(requireActivity(), "Vehicle deleted successfully", Toast.LENGTH_SHORT)
                        .show()

                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    fetchVehicle()
                    countListener.onUpdate(true)
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }


    companion object {

        @JvmStatic
        fun newInstance() =
            VehiclesFragment()
    }

    override fun onItemClick(view: View, position: Int) {
        if (view.id == R.id.vehicle_option_img) {
            showPopupMenu(view, vehicleList.get(position))
        }
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

    private fun showPopupMenu(view: View, vehicle: VehicleModel) {
        var popupMenu = PopupMenu(requireActivity(), view)
        popupMenu.inflate(R.menu.pop_menu)
        popupMenu.menu.getItem(0).setVisible(false)
        PopupIcon().insertMenuItemIcons(requireActivity(), popupMenu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.details -> {
                    /*var bundle = Bundle()
                    bundle.putParcelable("trip_details", trip)
                    if (Constant.switchMenu) {
                        AppNavigator.navigate(
                            requireActivity(),
                            R.id.frame_container,
                            FragmentTag.TRIP_DETAILS_FRAGMENT, true, bundle
                        )
                    }else{
                        AppNavigator.navigate(
                            requireActivity(),
                            R.id.fragment_container,
                            FragmentTag.TRIP_DETAILS_FRAGMENT, true, bundle
                        )
                    }*/
                }
                R.id.edit -> {
                    var bundle = Bundle()

                    bundle.putParcelable("vehicle_data", vehicle)

                    val dialog = CustomDialogFragment.newInstance(
                        requireActivity(),
                        object : AddUpdateListener {
                            override fun onAddOrUpdate(ok: Boolean, model: Payload?) {
                                Log.d("TAG", "Update model $model")
                                if (ok)
                                    updateVehicle(vehicle.id, model!!)
                            }

                            override fun onCancel(cancel: Boolean) {

                            }
                        }, bundle, R.layout.add_vehicle_dialog_fragment, "Update Vehicle", "Update"
                    )
                    //dialog.isCancelable = false
                    dialog.arguments = bundle
                    dialog.show(requireFragmentManager(), CustomDialogFragment.TAG)
                }
                R.id.delete -> {
                    Constant.showDialog(requireActivity(),
                        "Delete vehicle",
                        "Do you want to delete this vehicle?",
                        object : DialogCallback {
                            override fun onOk(ok: Boolean) {
                                if (ok) {
                                    deleteVehicle(vehicle.id)
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

    override fun onQueryTextSubmit(query: String?): Boolean {
        vehicleAdapter.filter.filter(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        vehicleAdapter.filter.filter(newText)
        return false
    }
}