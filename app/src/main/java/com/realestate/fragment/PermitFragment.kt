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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.realestate.R
import com.realestate.adapter.recycler.PermitAdapter
import com.realestate.callback.AddUpdateListener
import com.realestate.callback.DialogCallback
import com.realestate.callback.ItemClickListener
import com.realestate.callback.ResponseCallback
import com.realestate.model.PermitModel
import com.realestate.model.VehicleModel
import com.realestate.restapi.ApiClient
import com.realestate.restapi.Payload
import com.realestate.restapi.RetrofitRequest
import com.realestate.utils.Constant
import com.realestate.utils.CustomDialogFragment
import com.realestate.utils.PopupIcon
import kotlinx.android.synthetic.main.fragment_companies.*
import kotlinx.android.synthetic.main.fragment_permit.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.progress_dialog.*
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.RequestBody
import retrofit2.Call
import java.io.Serializable


/**
 * A simple [Fragment] subclass.
 * Use the [PermitFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PermitFragment : Fragment(), ItemClickListener, SearchView.OnQueryTextListener {

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var permitAdapter: PermitAdapter
    private var permitList = mutableListOf<PermitModel>()
    private var vehicleList = mutableListOf<VehicleModel>()
    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_permit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().runOnUiThread(Runnable {
            try {
                requireActivity().txtViewToolbarTitle.text =
                    activity?.resources?.getString(R.string.permit)
                activity?.imgViewBack?.setImageResource(R.drawable.ic_back)
                activity?.imgViewBack?.tag = "back"
            } catch (e: Exception) {

            }
        })
        fetchVehicle()
        initVariable()
        fetchPermit()
    }

    private fun initVariable() {
        permit_search.setOnQueryTextListener(this)
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        permit_recyclerview.layoutManager = layoutManager
        permit_recyclerview.setHasFixedSize(false)
        permitAdapter = PermitAdapter(
            requireActivity(),
            permitList,
            this
        )
        permit_recyclerview.adapter = permitAdapter

        add_permit_fab.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val dialog = CustomDialogFragment.newInstance(
                    requireActivity(),
                    object : AddUpdateListener {
                        override fun onAddOrUpdate(ok: Boolean, model: Payload?) {

                        }

                        override fun onCancel(cancel: Boolean) {

                        }
                    }, null,
                    R.layout.add_permit_dialog_fragment,
                    "Add Permit", null
                )
                //dialog.isCancelable = false
                dialog.show(activity?.supportFragmentManager!!, CustomDialogFragment.TAG)
            }

        })

        activity?.refresh_img?.visibility = View.VISIBLE
        activity?.add_img?.visibility = View.VISIBLE
        activity?.filter_spinner?.visibility = View.GONE

        activity?.add_img?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var bundle = Bundle()
                bundle.putSerializable("vehicle_list", vehicleList as Serializable)
                val dialog = CustomDialogFragment.newInstance(
                    requireActivity(),
                    object : AddUpdateListener {
                        override fun onAddOrUpdate(ok: Boolean, model: Payload?) {
                            if (ok)
                                addPermit(model!!)
                        }

                        override fun onCancel(cancel: Boolean) {

                        }
                    }, null,
                    R.layout.add_permit_dialog_fragment,
                    "Add Permit", null
                )
                //dialog.isCancelable = false
                dialog.arguments = bundle
                dialog.show(activity?.supportFragmentManager!!, CustomDialogFragment.TAG)
            }

        })

        activity?.refresh_img?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                fetchPermit()
            }

        })
    }

    private fun fetchPermit() {
        showProgress(requireActivity(), "Fetching permit...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val call = retrofit?.getAllPermit()

        RetrofitRequest().enqueue(
            call as Call<List<PermitModel>>,
            object : ResponseCallback<List<PermitModel>> {
                override fun onSuccess(response: List<PermitModel>) {
                    hideProgress()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    permitList.clear()

                    permitList.addAll(response)
                    var controller: LayoutAnimationController
                    try {
                        if (permitList.size > 0) {
                            /*permit_recyclerview?.layoutManager = StaggeredGridLayoutManager(
                                2,
                                StaggeredGridLayoutManager.VERTICAL
                            )*/
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


                        permit_recyclerview?.setLayoutAnimation(controller);
                    } catch (e: Exception) {
                    }
                    permitAdapter.notifyDataSetChanged()
                    permit_recyclerview?.scheduleLayoutAnimation()

                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun addPermit(payload: Payload) {

        showProgress(requireActivity(), "Adding permit...")
        val retrofit = ApiClient.instance?.getClient(true, activity)
        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            payload.toString()
        )
        val call = retrofit?.addPermit(body)

        RetrofitRequest().enqueue(
            call as Call<PermitModel>,
            object : ResponseCallback<PermitModel> {
                override fun onSuccess(response: PermitModel) {
                    hideProgress()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    fetchPermit()

                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun updatePermit(id: Int, payload: Payload) {

        showProgress(requireActivity(), "Updating permit...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            payload.toString()
        )
        val call = retrofit?.updatePermit(id, body)

        RetrofitRequest().enqueue(
            call as Call<PermitModel>,
            object : ResponseCallback<PermitModel> {
                override fun onSuccess(response: PermitModel) {
                    hideProgress()
                    Toast.makeText(requireActivity(), "Permit updated successfully", Toast.LENGTH_SHORT)
                        .show()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    fetchPermit()
                    //updateListener.onUpdate(true)
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun deletePermit(id: Int) {
        showProgress(requireActivity(), "Deleting permit...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val call = retrofit?.deletePermit(id)

        RetrofitRequest().enqueue(
            call as Call<PermitModel>,
            object : ResponseCallback<PermitModel> {
                override fun onSuccess(response: PermitModel) {
                    hideProgress()
                    Toast.makeText(requireActivity(), "Permit deleted successfully", Toast.LENGTH_SHORT)
                        .show()

                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    fetchPermit()
                    //updateListener.onUpdate(true)
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG).show()
                }

            })
    }

    private fun fetchVehicle() {
        //showProgress(requireActivity(), "Fetching vehicle...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val call = retrofit?.getAllVehicle()

        RetrofitRequest().enqueue(
            call as Call<List<VehicleModel>>,
            object : ResponseCallback<List<VehicleModel>> {
                override fun onSuccess(response: List<VehicleModel>) {
                    hideProgress()
                    (response as MutableList).add(
                        0,
                        VehicleModel("", -1, "Select Vehicle reg. no", "", "", "", null, null, null, null, null, null)
                    )
                    vehicleList.clear()
                    vehicleList.addAll(response)
                    //updateListener.onUpdate(true)
                }

                override fun onFailure(error: String?) {
                    //hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            PermitFragment()
    }

    override fun onItemClick(view: View, position: Int) {
        if (view.id == R.id.permit_option_img) {
            showPopupMenu(view, permitList.get(position))
        }
    }

    private fun showPopupMenu(view: View, permit: PermitModel) {
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
                    bundle.putParcelable("permit_data", permit)
                    bundle.putSerializable("vehicle_list", vehicleList as Serializable)
                    val dialog = CustomDialogFragment.newInstance(
                        requireActivity(),
                        object : AddUpdateListener {
                            override fun onAddOrUpdate(ok: Boolean, model: Payload?) {
                                Log.d("TAG", "Update model $model")
                                if (ok)
                                    updatePermit(permit.id, model!!)
                            }

                            override fun onCancel(cancel: Boolean) {

                            }
                        },
                        bundle,
                        R.layout.add_permit_dialog_fragment,
                        "Update Permit",
                        "Update"
                    )
                    //dialog.isCancelable = false
                    dialog.arguments = bundle
                    dialog.show(requireFragmentManager(), CustomDialogFragment.TAG)
                }
                R.id.delete -> {
                    Constant.showDialog(requireActivity(),
                        "Delete permit",
                        "Do you want to delete this permit?",
                        object : DialogCallback {
                            override fun onOk(ok: Boolean) {
                                if (ok) {
                                    deletePermit(permit.id)
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
        permitAdapter.filter.filter(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        permitAdapter.filter.filter(newText)
        return false
    }
}