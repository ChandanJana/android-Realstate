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
import com.google.gson.Gson
import com.realestate.R
import com.realestate.adapter.recycler.DealerAdapter
import com.realestate.callback.AddUpdateListener
import com.realestate.callback.DialogCallback
import com.realestate.callback.ItemClickListener
import com.realestate.callback.ResponseCallback
import com.realestate.model.DealerModel
import com.realestate.model.StateModel
import com.realestate.model.UserModel
import com.realestate.restapi.ApiClient
import com.realestate.restapi.Payload
import com.realestate.restapi.RetrofitRequest
import com.realestate.utils.Constant
import com.realestate.utils.CustomDialogFragment
import com.realestate.utils.FileReader
import com.realestate.utils.PopupIcon
import kotlinx.android.synthetic.main.fragment_companies.*
import kotlinx.android.synthetic.main.fragment_dealer.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.progress_dialog.*
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.RequestBody
import retrofit2.Call
import java.io.Serializable

/**
 * A simple [Fragment] subclass.
 * Use the [DealerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DealerFragment : Fragment(), ItemClickListener, SearchView.OnQueryTextListener {

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var dealerAdapter: DealerAdapter
    private var dealerList = ArrayList<DealerModel>()
    private var dialog: Dialog? = null
    private var stateList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = FileReader.getAssetFileJson("json/all_state.json")
        val stateModel = Gson().fromJson(data?.toString(), StateModel::class.java)
        stateList.addAll(stateModel.stateList)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dealer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().runOnUiThread(Runnable {
            try {
                requireActivity().txtViewToolbarTitle.text =
                    activity?.resources?.getString(R.string.dealer)
                activity?.imgViewBack?.setImageResource(R.drawable.ic_back)
                activity?.imgViewBack?.tag = "back"
            } catch (e: Exception) {

            }
        })

        initVariable()
        fetchDealer()
    }

    private fun initVariable() {
        dealer_search.setOnQueryTextListener(this)
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        dealer_recyclerview.layoutManager = layoutManager
        dealer_recyclerview.setHasFixedSize(false)
        dealerAdapter = DealerAdapter(
            requireActivity(),
            dealer_recyclerview,
            dealerList,
            this
        )

        dealer_recyclerview.adapter = dealerAdapter

        add_dealer_fab.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val dialog = CustomDialogFragment.newInstance(
                    requireActivity(),
                    object : AddUpdateListener {
                        override fun onAddOrUpdate(ok: Boolean, model: Payload?) {

                        }

                        override fun onCancel(cancel: Boolean) {

                        }
                    }, null,
                    R.layout.add_dealer_dialog_fragment,
                    "Add Dealer", null
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
                bundle.putSerializable("state_list", stateList as Serializable)
                val dialog = CustomDialogFragment.newInstance(
                    requireActivity(),
                    object : AddUpdateListener {
                        override fun onAddOrUpdate(ok: Boolean, model: Payload?) {
                            if (ok)
                                addDealer(model!!)
                        }

                        override fun onCancel(cancel: Boolean) {

                        }
                    }, null,
                    R.layout.add_dealer_dialog_fragment,
                    "Add Dealer", null
                )
                //dialog.isCancelable = false
                dialog.arguments = bundle
                dialog.show(activity?.supportFragmentManager!!, CustomDialogFragment.TAG)
            }

        })

        activity?.refresh_img?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                fetchDealer()
            }

        })
    }

    private fun setLayoutManager(list: MutableList<DealerModel>){
        if (list.isEmpty())
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        else
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        user_recyclerview?.layoutManager = layoutManager
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            DealerFragment()
    }

    override fun onItemClick(view: View, position: Int) {
        if (view.id == R.id.dealer_option_img) {
            showPopupMenu(view, dealerList.get(position))
        }
    }

    private fun showPopupMenu(view: View, dealer: DealerModel) {
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
                    bundle.putParcelable("dealer_data", dealer)
                    bundle.putSerializable("state_list", stateList as Serializable)
                    val dialog = CustomDialogFragment.newInstance(
                        requireActivity(),
                        object : AddUpdateListener {
                            override fun onAddOrUpdate(ok: Boolean, model: Payload?) {
                                Log.d("TAG", "Update model $model")
                                if (ok)
                                    updateDealer(dealer.id, model!!)
                            }

                            override fun onCancel(cancel: Boolean) {

                            }
                        },
                        bundle,
                        R.layout.add_dealer_dialog_fragment,
                        "Update Dealer",
                        "Update"
                    )
                    //dialog.isCancelable = false
                    dialog.arguments = bundle
                    dialog.show(requireFragmentManager(), CustomDialogFragment.TAG)
                }
                R.id.delete -> {
                    Constant.showDialog(requireActivity(),
                        "Delete dealer",
                        "Do you want to delete this dealer?",
                        object : DialogCallback {
                            override fun onOk(ok: Boolean) {
                                if (ok) {
                                    deleteDealer(dealer.id)
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

    private fun addDealer(payload: Payload) {

        showProgress(requireActivity(), "Adding dealer...")
        val retrofit = ApiClient.instance?.getClient(true, activity)
        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            payload.toString()
        )
        val call = retrofit?.addDealer(body)

        RetrofitRequest().enqueue(
            call as Call<DealerModel>,
            object : ResponseCallback<DealerModel> {
                override fun onSuccess(response: DealerModel) {
                    hideProgress()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    fetchDealer()

                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun updateDealer(id: Int, payload: Payload) {

        showProgress(requireActivity(), "Updating dealer...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            payload.toString()
        )
        val call = retrofit?.updateDealer(id, body)

        RetrofitRequest().enqueue(
            call as Call<DealerModel>,
            object : ResponseCallback<DealerModel> {
                override fun onSuccess(response: DealerModel) {
                    hideProgress()
                    Toast.makeText(requireActivity(), "Dealer updated successfully", Toast.LENGTH_SHORT)
                        .show()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    fetchDealer()
                    //updateListener.onUpdate(true)
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun deleteDealer(id: Int) {
        showProgress(requireActivity(), "Deleting dealer...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val call = retrofit?.deleteDealer(id)

        RetrofitRequest().enqueue(
            call as Call<DealerModel>,
            object : ResponseCallback<DealerModel> {
                override fun onSuccess(response: DealerModel) {
                    hideProgress()
                    Toast.makeText(requireActivity(), "Dealer deleted successfully", Toast.LENGTH_SHORT)
                        .show()

                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    fetchDealer()
                    //updateListener.onUpdate(true)
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG).show()
                }

            })
    }

    private fun fetchDealer() {
        showProgress(requireActivity(), "Fetching dealer...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val call = retrofit?.getAllDealer()

        RetrofitRequest().enqueue(
            call as Call<List<DealerModel>>,
            object : ResponseCallback<List<DealerModel>> {
                override fun onSuccess(response: List<DealerModel>) {
                    hideProgress()
                    dealerList.clear()

                    dealerList.addAll(response)

                    Log.d("TAGG", "dealerList $dealerList")
                    var controller: LayoutAnimationController
                    try {


                        if (dealerList.size > 0) {

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
                                )
                        }
                        setLayoutManager(dealerList)
                        dealer_recyclerview?.setLayoutAnimation(controller);
                    } catch (e: Exception) {

                    }
                    dealerAdapter.notifyDataSetChanged()
                    dealer_recyclerview?.scheduleLayoutAnimation()

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
        dealerAdapter.filter.filter(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        dealerAdapter.filter.filter(newText)
        return false
    }
}