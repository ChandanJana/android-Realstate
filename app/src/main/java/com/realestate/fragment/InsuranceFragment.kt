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
import com.realestate.adapter.recycler.InsuranceAdapter
import com.realestate.callback.AddUpdateListener
import com.realestate.callback.DialogCallback
import com.realestate.callback.ItemClickListener
import com.realestate.callback.ResponseCallback
import com.realestate.model.InsuranceModel
import com.realestate.model.VehicleModel
import com.realestate.restapi.ApiClient
import com.realestate.restapi.Payload
import com.realestate.restapi.RetrofitRequest
import com.realestate.utils.Constant
import com.realestate.utils.CustomDialogFragment
import com.realestate.utils.PopupIcon
import kotlinx.android.synthetic.main.fragment_insurance.*
import kotlinx.android.synthetic.main.progress_dialog.*
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.RequestBody
import retrofit2.Call
import java.io.Serializable


/**
 * A simple [Fragment] subclass.
 * Use the [InsuranceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InsuranceFragment : Fragment(), ItemClickListener, SearchView.OnQueryTextListener {

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var insuranceAdapter: InsuranceAdapter
    private var insuranceList = mutableListOf<InsuranceModel>()
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
        return inflater.inflate(R.layout.fragment_insurance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().runOnUiThread(Runnable {
            try {
                requireActivity().txtViewToolbarTitle.text =
                    activity?.resources?.getString(R.string.insurance)
                activity?.imgViewBack?.setImageResource(R.drawable.ic_back)
                activity?.imgViewBack?.tag = "back"
            } catch (e: Exception) {

            }
        })
        fetchVehicle()
        initVariable()
        fetchInsurance()
    }

    private fun initVariable() {
        insurance_search.setOnQueryTextListener(this)
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        insurance_recyclerview.layoutManager = layoutManager
        insurance_recyclerview.setHasFixedSize(false)
        insuranceAdapter = InsuranceAdapter(
            requireActivity(),
            insuranceList,
            this
        )
        insurance_recyclerview.adapter = insuranceAdapter

        add_insurance_fab.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val dialog = CustomDialogFragment.newInstance(
                    requireActivity(),
                    object : AddUpdateListener {
                        override fun onAddOrUpdate(ok: Boolean, model: Payload?) {

                        }

                        override fun onCancel(cancel: Boolean) {

                        }
                    }, null,
                    R.layout.add_insurance_dialog_fragment,
                    "Add Insurance", null
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
                                addInsurance(model!!)
                        }

                        override fun onCancel(cancel: Boolean) {

                        }
                    }, null,
                    R.layout.add_insurance_dialog_fragment,
                    "Add Insurance", null
                )
                //dialog.isCancelable = false
                dialog.arguments = bundle
                dialog.show(activity?.supportFragmentManager!!, CustomDialogFragment.TAG)
            }

        })

        activity?.refresh_img?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                fetchInsurance()
            }

        })
    }

    companion object {

        @JvmStatic
        fun newInstance() = InsuranceFragment()
    }

    override fun onItemClick(view: View, position: Int) {
        if (view.id == R.id.insurance_option_img) {
            showPopupMenu(view, insuranceList.get(position))
        }
    }

    private fun showPopupMenu(view: View, insurance: InsuranceModel) {
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
                    bundle.putParcelable("insurance_data", insurance)
                    bundle.putSerializable("vehicle_list", vehicleList as Serializable)
                    val dialog = CustomDialogFragment.newInstance(
                        requireActivity(),
                        object : AddUpdateListener {
                            override fun onAddOrUpdate(ok: Boolean, model: Payload?) {
                                Log.d("TAG", "Update model $model")
                                if (ok)
                                    updateInsurance(insurance.id!!, model!!)
                            }

                            override fun onCancel(cancel: Boolean) {

                            }
                        },
                        bundle,
                        R.layout.add_insurance_dialog_fragment,
                        "Update Insurance",
                        "Update"
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
                                    deleteInsurance(insurance.id!!)
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

    private fun updateInsurance(id: Int, payload: Payload) {
        showProgress(requireActivity(), "Updating insurance...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            payload.toString()
        )
        val call = retrofit?.updateInsurance(id, body)

        RetrofitRequest().enqueue(
            call as Call<InsuranceModel>,
            object : ResponseCallback<InsuranceModel> {
                override fun onSuccess(response: InsuranceModel) {
                    hideProgress()
                    Toast.makeText(requireActivity(), "Insurance updated successfully", Toast.LENGTH_SHORT)
                        .show()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    fetchInsurance()
                    //updateListener.onUpdate(true)
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun deleteInsurance(id: Int) {
        showProgress(requireActivity(), "Deleting insurance...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val call = retrofit?.deleteInsurance(id)

        RetrofitRequest().enqueue(
            call as Call<InsuranceModel>,
            object : ResponseCallback<InsuranceModel> {
                override fun onSuccess(response: InsuranceModel) {
                    hideProgress()
                    Toast.makeText(requireActivity(), "Insurance deleted successfully", Toast.LENGTH_SHORT)
                        .show()

                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    fetchInsurance()
                    //updateListener.onUpdate(true)
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
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

    private fun fetchInsurance() {
        showProgress(requireActivity(), "Fetching insurance...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val call = retrofit?.getAllInsurance()

        RetrofitRequest().enqueue(
            call as Call<List<InsuranceModel>>,
            object : ResponseCallback<List<InsuranceModel>> {
                override fun onSuccess(response: List<InsuranceModel>) {
                    hideProgress()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    insuranceList.clear()

                    insuranceList.addAll(response)
                    var controller: LayoutAnimationController
                    try {


                        if (insuranceList.size > 0) {
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


                        insurance_recyclerview?.setLayoutAnimation(controller);
                    } catch (e: Exception) {

                    }
                    insuranceAdapter.notifyDataSetChanged()
                    insurance_recyclerview?.scheduleLayoutAnimation()

                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun addInsurance(payload: Payload) {
        showProgress(requireActivity(), "Adding insurance...")
        val retrofit = ApiClient.instance?.getClient(true, activity)
        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            payload.toString()
        )
        val call = retrofit?.addInsurance(body)

        RetrofitRequest().enqueue(
            call as Call<InsuranceModel>,
            object : ResponseCallback<InsuranceModel> {
                override fun onSuccess(response: InsuranceModel) {
                    hideProgress()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    fetchInsurance()

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
        insuranceAdapter.filter.filter(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        insuranceAdapter.filter.filter(newText)
        return false
    }
}