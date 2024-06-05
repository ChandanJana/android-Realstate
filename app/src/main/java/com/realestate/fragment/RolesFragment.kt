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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.realestate.R
import com.realestate.adapter.recycler.RolesAdapter
import com.realestate.callback.AddUpdateListener
import com.realestate.callback.DialogCallback
import com.realestate.callback.ItemClickListener
import com.realestate.callback.ResponseCallback
import com.realestate.model.RolesModel
import com.realestate.restapi.ApiClient
import com.realestate.restapi.Payload
import com.realestate.restapi.RetrofitRequest
import com.realestate.utils.Constant
import com.realestate.utils.CustomDialogFragment
import com.realestate.utils.PopupIcon
import kotlinx.android.synthetic.main.fragment_roles.*
import kotlinx.android.synthetic.main.progress_dialog.*
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.RequestBody
import retrofit2.Call

/**
 * A simple [Fragment] subclass.
 * Use the [RolesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RolesFragment : Fragment(), ItemClickListener {

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var rolesAdapter: RolesAdapter
    private var rolesList = ArrayList<RolesModel>()
    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_roles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().runOnUiThread(Runnable {
            try {
                requireActivity().txtViewToolbarTitle.text = activity?.resources?.getString(R.string.role)
                activity?.imgViewBack?.setImageResource(R.drawable.ic_back)
                activity?.imgViewBack?.tag = "back"
            } catch (e: Exception) {

            }
        })
        initVariable()
        fetchRoles()
    }

    private fun initVariable() {
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        role_recyclerview.layoutManager = layoutManager
        role_recyclerview.setHasFixedSize(false)
        rolesAdapter = RolesAdapter(
            requireActivity(),
            rolesList,
            this
        )
        role_recyclerview.adapter = rolesAdapter

        add_roles_fab.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val dialog = CustomDialogFragment.newInstance(
                    requireActivity(),
                    object : AddUpdateListener {
                        override fun onAddOrUpdate(ok: Boolean, model: Payload?) {

                        }

                        override fun onCancel(cancel: Boolean) {

                        }
                    }, null,
                    R.layout.add_role_dialog_fragment,
                    "Add Role", null
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
                val dialog = CustomDialogFragment.newInstance(
                    requireActivity(),
                    object : AddUpdateListener {
                        override fun onAddOrUpdate(ok: Boolean, model: Payload?) {
                            if (ok)
                                addRole(model!!)
                        }

                        override fun onCancel(cancel: Boolean) {

                        }
                    }, null,
                    R.layout.add_role_dialog_fragment,
                    "Add Role", null
                )
                //dialog.isCancelable = false
                dialog.show(activity?.supportFragmentManager!!, CustomDialogFragment.TAG)
            }

        })

        activity?.refresh_img?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                fetchRoles()
            }

        })
    }

    private fun fetchRoles() {
        showProgress(requireActivity(), "Fetch role...")
        val retrofit = ApiClient.instance?.getClient(false, requireActivity())

        val call = retrofit?.getAllRole()

        RetrofitRequest().enqueue(
            call as Call<List<RolesModel>>,
            object : ResponseCallback<List<RolesModel>> {
                override fun onSuccess(response: List<RolesModel>) {
                    hideProgress()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    rolesList.clear()
                    rolesList.addAll(response)
                    val controller: LayoutAnimationController
                    try {
                        if (rolesList.size > 0) {
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

                        role_recyclerview?.setLayoutAnimation(controller);
                    } catch (e: Exception) {
                    }
                    rolesAdapter.notifyDataSetChanged()
                    role_recyclerview?.scheduleLayoutAnimation()
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(requireActivity(), "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            RolesFragment()
    }

    override fun onItemClick(view: View, position: Int) {
        if (view.id == R.id.role_option_img) {
            showPopupMenu(view, rolesList.get(position))
        }
    }

    private fun showPopupMenu(view: View, role: RolesModel) {
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
                    bundle.putParcelable("role_data", role)
                    //bundle.putSerializable("material_list", rolesList as Serializable)
                    val dialog = CustomDialogFragment.newInstance(
                        requireActivity(),
                        object : AddUpdateListener {
                            override fun onAddOrUpdate(ok: Boolean, model: Payload?) {
                                Log.d("TAG", "Update model $model")
                                if (ok)
                                    updateRole(role.id, model!!)
                            }

                            override fun onCancel(cancel: Boolean) {

                            }
                        },
                        bundle,
                        R.layout.add_role_dialog_fragment,
                        "Update Role",
                        "Update"
                    )
                    //dialog.isCancelable = false
                    dialog.arguments = bundle
                    dialog.show(requireFragmentManager(), CustomDialogFragment.TAG)
                }
                R.id.delete -> {
                    Constant.showDialog(requireActivity(),
                        "Delete role",
                        "Do you want to delete this role?",
                        object : DialogCallback {
                            override fun onOk(ok: Boolean) {
                                if (ok) {
                                    deleteRole(role.id)
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

    private fun addRole(payload: Payload) {

        showProgress(requireActivity(), "Adding role...")
        val retrofit = ApiClient.instance?.getClient(true, activity)
        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            payload.toString()
        )
        val call = retrofit?.addRole(body)

        RetrofitRequest().enqueue(
            call as Call<RolesModel>,
            object : ResponseCallback<RolesModel> {
                override fun onSuccess(response: RolesModel) {
                    hideProgress()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    fetchRoles()

                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun updateRole(id: Int, payload: Payload) {

        showProgress(requireActivity(), "Updating roles...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            payload.toString()
        )
        val call = retrofit?.updateRole(id, body)

        RetrofitRequest().enqueue(
            call as Call<RolesModel>,
            object : ResponseCallback<RolesModel> {
                override fun onSuccess(response: RolesModel) {
                    hideProgress()
                    Toast.makeText(requireActivity(), "Roles updated successfully", Toast.LENGTH_SHORT)
                        .show()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    fetchRoles()
                    //updateListener.onUpdate(true)
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun deleteRole(id: Int) {
        showProgress(requireActivity(), "Deleting roles...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val call = retrofit?.deleteRole(id)

        RetrofitRequest().enqueue(
            call as Call<RolesModel>,
            object : ResponseCallback<RolesModel> {
                override fun onSuccess(response: RolesModel) {
                    hideProgress()
                    Toast.makeText(requireActivity(), "Roles deleted successfully", Toast.LENGTH_SHORT)
                        .show()

                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    fetchRoles()
                    //updateListener.onUpdate(true)
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG).show()
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