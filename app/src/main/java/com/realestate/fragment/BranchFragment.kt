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
import com.realestate.adapter.recycler.BranchAdapter
import com.realestate.callback.AddUpdateListener
import com.realestate.callback.DialogCallback
import com.realestate.callback.ItemClickListener
import com.realestate.callback.ResponseCallback
import com.realestate.model.BranchModel
import com.realestate.model.UserModel
import com.realestate.restapi.ApiClient
import com.realestate.restapi.Payload
import com.realestate.restapi.RetrofitRequest
import com.realestate.utils.Constant
import com.realestate.utils.CustomDialogFragment
import com.realestate.utils.PopupIcon
import kotlinx.android.synthetic.main.fragment_branch.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.progress_dialog.*
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.RequestBody
import retrofit2.Call
import java.io.Serializable
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [BranchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BranchFragment : Fragment(), ItemClickListener, SearchView.OnQueryTextListener {

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var branchAdapter: BranchAdapter
    private var branchList = mutableListOf<BranchModel>()
    private var userList = mutableListOf<UserModel>()
    private var dialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_branch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Constant.switchMenu) {
            requireActivity().runOnUiThread(Runnable {
                try {
                    requireActivity().txtViewToolbarTitle.text =
                        activity?.resources?.getString(R.string.branch)
                    activity?.imgViewBack?.setImageResource(R.drawable.ic_menu)
                    activity?.imgViewBack?.tag = "menu"
                } catch (e: Exception) {

                }
            })
        } else {
            requireActivity().runOnUiThread(Runnable {
                try {
                    requireActivity().txtViewToolbarTitle.text =
                        activity?.resources?.getString(R.string.branch)
                    activity?.imgViewBack?.setImageResource(R.drawable.ic_back)
                    activity?.imgViewBack?.tag = "back"
                } catch (e: Exception) {

                }
            })
        }

        fetchUser()

        initVariable()

        fetchBranch()
    }

    private fun initVariable() {
        branch_search.setOnQueryTextListener(this)
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        branch_recyclerview.layoutManager = layoutManager
        //branch_recyclerview.setHasFixedSize(false)
        branchAdapter = BranchAdapter(
            requireActivity(),
            branch_recyclerview,
            branchList,
            this
        )
        branch_recyclerview.adapter = branchAdapter

        add_branch_fab.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val dialog = CustomDialogFragment.newInstance(
                    requireActivity(), object : AddUpdateListener {
                        override fun onAddOrUpdate(ok: Boolean, model: Payload?) {

                        }

                        override fun onCancel(cancel: Boolean) {

                        }
                    }, null,
                    R.layout.add_branch_dialog_fragment,
                    "Add Branch", null
                )
                //dialog.isCancelable = false
                dialog.show(activity?.supportFragmentManager!!, CustomDialogFragment.TAG)
            }

        })

        activity?.refresh_img?.visibility = View.VISIBLE
        activity?.add_img?.visibility = View.VISIBLE
        activity?.filter_spinner?.visibility = View.GONE

        activity?.refresh_img?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                fetchBranch()
            }

        })

        activity?.add_img?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var bundle = Bundle()
                bundle.putSerializable("user_list", userList as Serializable)
                val dialog = CustomDialogFragment.newInstance(
                    requireActivity(), object : AddUpdateListener {
                        override fun onAddOrUpdate(ok: Boolean, model: Payload?) {
                            if (ok)
                                addBranch(model!!)
                        }

                        override fun onCancel(cancel: Boolean) {

                        }
                    }, bundle,
                    R.layout.add_branch_dialog_fragment,
                    "Add Branch", null
                )
                //dialog.isCancelable = false
                dialog.arguments = bundle
                dialog.show(activity?.supportFragmentManager!!, CustomDialogFragment.TAG)
            }

        })
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            BranchFragment()
    }

    override fun onItemClick(view: View, position: Int) {
        when (view.id) {
            R.id.branch_option_img -> {
                showPopupMenu(view, branchList.get(position))
            }
        }
    }

    private fun setLayoutManager(list: MutableList<BranchModel>){
        if (list.isEmpty())
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        else
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        branch_recyclerview?.layoutManager = layoutManager
    }

    private fun showPopupMenu(view: View, branch: BranchModel) {
        var popupMenu = PopupMenu(requireActivity(), view)
        popupMenu.inflate(R.menu.pop_menu)
        var item = popupMenu.menu
        item.getItem(0).setVisible(false)
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

                    bundle.putSerializable("user_list", userList as Serializable)
                    bundle.putParcelable("branch_data", branch)

                    val dialog = CustomDialogFragment.newInstance(
                        requireActivity(),
                        object : AddUpdateListener {
                            override fun onAddOrUpdate(ok: Boolean, model: Payload?) {
                                Log.d("TAG", "Update model $model")
                                if (ok)
                                    updateBranch(branch.id!!, model!!)
                            }

                            override fun onCancel(cancel: Boolean) {

                            }
                        }, bundle, R.layout.add_branch_dialog_fragment, "Update Branch", "Update"
                    )
                    //dialog.isCancelable = false
                    dialog.arguments = bundle
                    dialog.show(requireFragmentManager(), CustomDialogFragment.TAG)
                }
                R.id.delete -> {
                    Constant.showDialog(requireActivity(),
                        "Delete branch",
                        "Do you want to delete this branch?",
                        object : DialogCallback {
                            override fun onOk(ok: Boolean) {
                                if (ok) {
                                    deleteBranch(branch.id!!)
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

    private fun fetchBranch() {
        showProgress(requireActivity(), "Fetching branch...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val call = retrofit?.getAllBranch()

        RetrofitRequest().enqueue(
            call as Call<List<BranchModel>>,
            object : ResponseCallback<List<BranchModel>> {
                override fun onSuccess(response: List<BranchModel>) {
                    hideProgress()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    branchList.clear()
                    branchList.addAll(response)
                    var controller: LayoutAnimationController
                    try {
                        if (branchList.size > 0) {
                            controller =
                                AnimationUtils.loadLayoutAnimation(
                                    context,
                                    R.anim.layout_animation_down_to_up
                                );

                        } else {
                            controller =
                                AnimationUtils.loadLayoutAnimation(
                                    context,
                                    R.anim.layout_animation_down_to_up
                                );

                        }

                        setLayoutManager(branchList)
                        branch_recyclerview?.setLayoutAnimation(controller)
                    } catch (e: Exception) {

                    }
                    branchAdapter.notifyDataSetChanged()
                    branch_recyclerview?.scheduleLayoutAnimation()

                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun deleteBranch(id: Int) {
        showProgress(requireActivity(), "Deleting branch...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val call = retrofit?.deleteBranch(id)

        RetrofitRequest().enqueue(
            call as Call<BranchModel>,
            object : ResponseCallback<BranchModel> {
                override fun onSuccess(response: BranchModel) {
                    hideProgress()
                    Toast.makeText(activity, "Branch deleted successfully", Toast.LENGTH_LONG)
                        .show()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    fetchBranch()
                    fetchUser()

                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun updateBranch(id: Int, payload: Payload) {
        showProgress(requireActivity(), "Updating branch...")
        val retrofit = ApiClient.instance?.getClient(true, activity)
        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            payload.toString()
        )
        val call = retrofit?.updateBranch(id, body)

        RetrofitRequest().enqueue(
            call as Call<BranchModel>,
            object : ResponseCallback<BranchModel> {
                override fun onSuccess(response: BranchModel) {
                    hideProgress()
                    Toast.makeText(activity, "Branch updated successfully", Toast.LENGTH_LONG)
                        .show()
                    //(response as MutableList).add(0, RolesModel(Trip-1, "Select role", "SELECT ROLE"))
                    fetchBranch()
                    fetchUser()
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }


    private fun addBranch(payload: Payload) {
        showProgress(requireActivity(), "Adding branch...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            payload.toString()
        )
        val call = retrofit?.addBranch(body)

        RetrofitRequest().enqueue(
            call as Call<BranchModel>,
            object : ResponseCallback<BranchModel> {
                override fun onSuccess(response: BranchModel) {
                    hideProgress()
                    Toast.makeText(activity, "Branch added successfully", Toast.LENGTH_LONG)
                        .show()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
//                    tripList.clear()
//                    tripList.addAll(response)
//                    tripAdapter.notifyDataSetChanged()
//                    updateListener.onUpdate(true)
                    fetchBranch()
                    fetchUser()
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun fetchUser() {
        //showProgress(requireActivity(), "Fetching user...")
        val retrofit = ApiClient.instance?.getClient(false, activity)

        val call = retrofit?.getAllUser()

        RetrofitRequest().enqueue(
            call as Call<List<UserModel>>,
            object : ResponseCallback<List<UserModel>> {
                override fun onSuccess(response: List<UserModel>) {
                    hideProgress()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    userList.clear()
                    (response as MutableList).add(
                        0,
                        UserModel(
                            "",
                            "",
                            null,
                            "",
                            -1,
                            "Select user name",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            -1,
                            "",
                            ""
                        )
                    )

                    userList.addAll(response)
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
        branchAdapter.filter.filter(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        branchAdapter.filter.filter(newText)
        return false
    }


}