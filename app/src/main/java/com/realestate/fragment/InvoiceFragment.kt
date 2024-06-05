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
import com.realestate.adapter.recycler.InvoiceAdapter
import com.realestate.callback.AddUpdateListener
import com.realestate.callback.DialogCallback
import com.realestate.callback.ItemClickListener
import com.realestate.callback.ResponseCallback
import com.realestate.model.InvoiceModel
import com.realestate.model.TripModel
import com.realestate.model.UserModel
import com.realestate.restapi.ApiClient
import com.realestate.restapi.Payload
import com.realestate.restapi.RetrofitRequest
import com.realestate.utils.*
import com.realestate.utils.FragmentTag
import kotlinx.android.synthetic.main.fragment_invoice.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.progress_dialog.*
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.RequestBody
import retrofit2.Call
import java.io.Serializable

/**
 * A simple [Fragment] subclass.
 * Use the [InvoiceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InvoiceFragment : Fragment(), ItemClickListener, SearchView.OnQueryTextListener {

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var invoiceAdapter: InvoiceAdapter
    private var invoiceList = mutableListOf<InvoiceModel>()
    private var tripList = mutableListOf<TripModel>()
    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invoice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().runOnUiThread(Runnable {
            try {
                requireActivity().txtViewToolbarTitle.text =
                    activity?.resources?.getString(R.string.invoice)
                activity?.imgViewBack?.setImageResource(R.drawable.ic_back)
                activity?.imgViewBack?.tag = "back"
            } catch (e: Exception) {

            }
        })
        fetchTrip()
        initVariable()
        fetchInvoice()
    }

    private fun initVariable() {
        invoice_search.setOnQueryTextListener(this)
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        invoice_recyclerview.layoutManager = layoutManager
        invoice_recyclerview.setHasFixedSize(false)
        invoiceAdapter = InvoiceAdapter(
            requireActivity(),
            invoice_recyclerview,
            invoiceList,
            this
        )
        invoice_recyclerview.adapter = invoiceAdapter

        add_invoice_fab.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val dialog = CustomDialogFragment.newInstance(
                    requireActivity(),
                    object : AddUpdateListener {
                        override fun onAddOrUpdate(ok: Boolean, model: Payload?) {

                        }

                        override fun onCancel(cancel: Boolean) {

                        }
                    }, null,
                    R.layout.add_invoice_dialog_fragment,
                    "Add Invoice", null
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
                bundle.putSerializable("trip_list", tripList as Serializable)
                val dialog = CustomDialogFragment.newInstance(
                    requireActivity(),
                    object : AddUpdateListener {
                        override fun onAddOrUpdate(ok: Boolean, model: Payload?) {
                            if (ok)
                                addInvoice(model!!)

                        }

                        override fun onCancel(cancel: Boolean) {

                        }
                    }, null,
                    R.layout.add_invoice_dialog_fragment,
                    "Add Invoice", null
                )
                //dialog.isCancelable = false
                dialog.arguments = bundle
                dialog.show(activity?.supportFragmentManager!!, CustomDialogFragment.TAG)
            }

        })

        activity?.refresh_img?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                fetchInvoice()
            }

        })
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            InvoiceFragment()
    }

    private fun setLayoutManager(list: MutableList<InvoiceModel>){
        if (list.isEmpty())
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        else
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        invoice_recyclerview?.layoutManager = layoutManager
    }

    override fun onItemClick(view: View, position: Int) {
        if (view.id == R.id.invoice_option_img) {
            showPopupMenu(view, invoiceList[position])
        }else{
            var bundle = Bundle()
            bundle.putParcelable("invoice_details", invoiceList[position])
            /*if (Constant.switchMenu) {
                AppNavigator.navigate(
                    requireActivity(),
                    R.id.frame_container,
                    FragmentTag.INVOICE_DETAILS_FRAGMENT, true, bundle
                )
            }else{*/
            AppNavigator.navigate(
                requireActivity(),
                R.id.fragment_container,
                FragmentTag.INVOICE_DETAILS_FRAGMENT, true, bundle
            )
            //}
        }
    }

    private fun fetchTrip() {
        //showProgress(requireActivity(), "Fetching trip...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val call = retrofit?.getAllTrip()

        RetrofitRequest().enqueue(
            call as Call<List<TripModel>>,
            object : ResponseCallback<List<TripModel>> {
                override fun onSuccess(response: List<TripModel>) {
                    hideProgress()
                    (response as MutableList).add(
                        0,
                        TripModel(
                            0.0,
                            0.0,
                            0.0,
                            0.0,
                            null,
                            "",
                            0.0,
                            "Select trip",
                            0.0,
                            "",
                            "",
                            null,
                            -1,
                            -1,
                            0.0,
                            "",
                            0.0,
                            "",
                            0.0,
                            -1,
                            0.0,
                            0.0,
                            -1,
                            0.0,
                            -1,
                            0.0,
                            0.0,
                            null,
                            null,
                            null,
                            ""
                        )
                    )
                    tripList.clear()
                    tripList.addAll(response)

                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun showPopupMenu(view: View, invoice: InvoiceModel) {
        var popupMenu = PopupMenu(requireActivity(), view)
        popupMenu.inflate(R.menu.pop_menu)
        PopupIcon().insertMenuItemIcons(requireActivity(), popupMenu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.details -> {
                    var bundle = Bundle()
                    bundle.putParcelable("invoice_details", invoice)
                    /*if (Constant.switchMenu) {
                        AppNavigator.navigate(
                            requireActivity(),
                            R.id.frame_container,
                            FragmentTag.INVOICE_DETAILS_FRAGMENT, true, bundle
                        )
                    }else{*/
                        AppNavigator.navigate(
                            requireActivity(),
                            R.id.fragment_container,
                            FragmentTag.INVOICE_DETAILS_FRAGMENT, true, bundle
                        )
                    //}
                }
                R.id.edit -> {
                    var bundle = Bundle()
                    bundle.putParcelable("invoice_data", invoice)
                    bundle.putSerializable("trip_list", tripList as Serializable)
                    val dialog = CustomDialogFragment.newInstance(
                        requireActivity(),
                        object : AddUpdateListener {
                            override fun onAddOrUpdate(ok: Boolean, model: Payload?) {
                                Log.d("TAG", "Update model $model")
                                if (ok)
                                    updateInvoice(invoice.id, model!!)
                            }

                            override fun onCancel(cancel: Boolean) {

                            }
                        },
                        bundle,
                        R.layout.add_invoice_dialog_fragment,
                        "Update Invoice",
                        "Update"
                    )
                    //dialog.isCancelable = false
                    dialog.arguments = bundle
                    dialog.show(requireFragmentManager(), CustomDialogFragment.TAG)
                }
                R.id.delete -> {
                    Constant.showDialog(requireActivity(),
                        "Delete invoice",
                        "Do you want to delete this invoice?",
                        object : DialogCallback {
                            override fun onOk(ok: Boolean) {
                                if (ok) {
                                    deleteInvoice(invoice.id)
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

    private fun addInvoice(payload: Payload) {

        showProgress(requireActivity(), "Adding invoice...")
        val retrofit = ApiClient.instance?.getClient(true, activity)
        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            payload.toString()
        )
        val call = retrofit?.addInvoice(body)

        RetrofitRequest().enqueue(
            call as Call<InvoiceModel>,
            object : ResponseCallback<InvoiceModel> {
                override fun onSuccess(response: InvoiceModel) {
                    hideProgress()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    fetchInvoice()

                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun updateInvoice(id: Int, payload: Payload) {

        showProgress(requireActivity(), "Updating invoice...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            payload.toString()
        )
        val call = retrofit?.updateInvoice(id, body)

        RetrofitRequest().enqueue(
            call as Call<InvoiceModel>,
            object : ResponseCallback<InvoiceModel> {
                override fun onSuccess(response: InvoiceModel) {
                    hideProgress()
                    Toast.makeText(requireActivity(), "Invoice updated successfully", Toast.LENGTH_SHORT)
                        .show()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    fetchInvoice()
                    //updateListener.onUpdate(true)
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun deleteInvoice(id: Int) {
        showProgress(requireActivity(), "Deleting invoice...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val call = retrofit?.deleteInvoice(id)

        RetrofitRequest().enqueue(
            call as Call<InvoiceModel>,
            object : ResponseCallback<InvoiceModel> {
                override fun onSuccess(response: InvoiceModel) {
                    hideProgress()
                    Toast.makeText(requireActivity(), "Invoice deleted successfully", Toast.LENGTH_SHORT)
                        .show()

                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    fetchInvoice()
                    //updateListener.onUpdate(true)
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG).show()
                }

            })
    }

    private fun fetchInvoice() {
        showProgress(requireActivity(), "Fetching invoice...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val call = retrofit?.getAllInvoice()

        RetrofitRequest().enqueue(
            call as Call<List<InvoiceModel>>,
            object : ResponseCallback<List<InvoiceModel>> {
                override fun onSuccess(response: List<InvoiceModel>) {
                    hideProgress()
                    invoiceList.clear()

                    invoiceList.addAll(response)
                    Log.d("TAGG", "dealerList $invoiceList")
                    var controller: LayoutAnimationController
                    try {

                        if (invoiceList.size > 0) {
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
                        setLayoutManager(invoiceList)
                        invoice_recyclerview?.setLayoutAnimation(controller);
                    } catch (e: Exception) {

                    }
                    invoiceAdapter.notifyDataSetChanged()
                    invoice_recyclerview?.scheduleLayoutAnimation()

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
        invoiceAdapter.filter.filter(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        invoiceAdapter.filter.filter(newText)
        return false
    }
}