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
import com.realestate.activity.DashBoardActivity
import com.realestate.adapter.recycler.UserAdapter
import com.realestate.callback.AddUpdateListener
import com.realestate.callback.DialogCallback
import com.realestate.callback.ItemClickListener
import com.realestate.callback.ResponseCallback
import com.realestate.model.RolesModel
import com.realestate.model.UserModel
import com.realestate.restapi.ApiClient
import com.realestate.restapi.Payload
import com.realestate.restapi.RetrofitRequest
import com.realestate.utils.*
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.progress_dialog.*
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.RequestBody
import retrofit2.Call
import java.io.Serializable


/**
 * A simple [Fragment] subclass.
 * Use the [UserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserFragment : Fragment(), ItemClickListener, SearchView.OnQueryTextListener {

    private lateinit var userAdapter: UserAdapter
    private var userList = mutableListOf<UserModel>()
    private var rolesList = mutableListOf<RolesModel>()
    private var dialog: Dialog? = null
    private lateinit var listener: DashBoardActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listener = DashBoardActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Constant.switchMenu) {
            requireActivity().runOnUiThread(Runnable {
                try {
                    requireActivity().txtViewToolbarTitle.text =
                        activity?.resources?.getString(R.string.users)
                    activity?.imgViewBack?.setImageResource(R.drawable.ic_menu)
                    activity?.imgViewBack?.tag = "menu"
                } catch (e: Exception) {

                }
            })
        } else {
            requireActivity().runOnUiThread(Runnable {
                try {
                    requireActivity().txtViewToolbarTitle.text =
                        activity?.resources?.getString(R.string.users)
                    activity?.imgViewBack?.setImageResource(R.drawable.ic_back)
                    activity?.imgViewBack?.tag = "back"
                } catch (e: Exception) {

                }
            })
        }

        getRoles()
        initVariable()
        fetchUser()
    }

    private fun getRoles() {
        //showProgress(requireActivity(), "loading...")
        val retrofit = ApiClient.instance?.getClient(false, requireActivity())

        val call = retrofit?.getAllRole()

        RetrofitRequest().enqueue(
            call as Call<List<RolesModel>>,
            object : ResponseCallback<List<RolesModel>> {
                override fun onSuccess(response: List<RolesModel>) {
                    //hideProgress()
                    (response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    rolesList.addAll(response)
                    //adapter.notifyDataSetChanged()
                }

                override fun onFailure(error: String?) {
                    //hideProgress()
                    Toast.makeText(requireActivity(), "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    override fun onResume() {
        super.onResume()
        activity?.refresh_img?.visibility = View.VISIBLE
        //fetchUser()
    }

    private fun initVariable() {
        user_search.setOnQueryTextListener(this)
        layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        user_recyclerview.layoutManager = layoutManager
        //user_recyclerview.setHasFixedSize(false)
        userAdapter = UserAdapter(
            requireActivity(),
            user_recyclerview,
            userList,
            this
        )
        user_recyclerview.adapter = userAdapter

        activity?.add_img?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

                var bundle = Bundle()
                bundle.putSerializable("role_list", rolesList as Serializable)
                bundle.putParcelable("user_data", null)

                val dialog = CustomDialogFragment.newInstance(
                    requireActivity(),
                    object : AddUpdateListener {
                        override fun onAddOrUpdate(ok: Boolean, model: Payload?) {
                            if (ok) {
                                addUser(model!!)
                            }
                        }

                        override fun onCancel(cancel: Boolean) {

                        }
                    },
                    bundle,
                    R.layout.add_user_dialog_fragment,
                    "Add User",
                    null
                )
                //dialog.isCancelable = false
                dialog.arguments = bundle
                dialog.show(requireFragmentManager(), CustomDialogFragment.TAG)
            }

        })

        add_users_fab.visibility = View.GONE

        activity?.refresh_img?.visibility = View.VISIBLE
        activity?.add_img?.visibility = View.VISIBLE
        activity?.filter_spinner?.visibility = View.GONE

        activity?.refresh_img?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                fetchUser()
                //Toast.makeText(requireActivity(), "User refresh", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun fetchUser() {
        showProgress(requireActivity(), "Fetching user...")
        val retrofit = ApiClient.instance?.getClient(false, activity)

        val call = retrofit?.getAllUser()

        RetrofitRequest().enqueue(
            call as Call<List<UserModel>>,
            object : ResponseCallback<List<UserModel>> {
                override fun onSuccess(response: List<UserModel>) {
                    hideProgress()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    userList.clear()
                    userList.addAll(response)
                    val controller: LayoutAnimationController
                    try {
                        if (userList.size > 0) {
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
                        setLayoutManager(userList)
                        user_recyclerview?.setLayoutAnimation(controller);
                    } catch (e: Exception) {

                    }
                    userAdapter.notifyDataSetChanged()
                    user_recyclerview?.scheduleLayoutAnimation()
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun deleteUser(userId: Int) {
        showProgress(requireActivity(), "Delete user...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val call = retrofit?.deleteUser(userId)

        RetrofitRequest().enqueue(
            call as Call<UserModel>,
            object : ResponseCallback<UserModel> {
                override fun onSuccess(response: UserModel) {
                    hideProgress()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    if (response.id == userId) {
                        Toast.makeText(activity, "User deleted successfully", Toast.LENGTH_LONG)
                            .show()
                        fetchUser()
                        listener?.onUpdate(true)
                    } else {
                        Toast.makeText(activity, "Failed to delete user", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Log.d("TAG", "Error $error")
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun updateUser(userId: Int, payload: Payload) {
        showProgress(requireActivity(), "Update user...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            payload.toString()
        )
        val call = retrofit?.updateUser(userId, body)

        RetrofitRequest().enqueue(
            call as Call<UserModel>,
            object : ResponseCallback<UserModel> {
                override fun onSuccess(response: UserModel) {
                    hideProgress()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    if (response.id == userId) {
                        Toast.makeText(activity, "User updated successfully", Toast.LENGTH_LONG)
                            .show()
                        fetchUser()
                    } else {
                        Toast.makeText(activity, "Failed to update user", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Log.d("TAG", "Error $error")
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun addUser(payload: Payload) {
        showProgress(requireActivity(), "Please wait...")
        val retrofit = ApiClient.instance?.getClient(true, activity)

        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            payload.toString()
        )
        val call = retrofit?.addUser(body)

        RetrofitRequest().enqueue(
            call as Call<UserModel>,
            object : ResponseCallback<UserModel> {
                override fun onSuccess(response: UserModel) {
                    hideProgress()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    Toast.makeText(activity, "User added successfully", Toast.LENGTH_LONG)
                        .show()
                    listener?.onUpdate(true)
                    fetchUser()
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Log.d("TAG", "Error $error")
                    Toast.makeText(activity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            UserFragment()
        private lateinit var layoutManager: RecyclerView.LayoutManager

    }
    private fun setLayoutManager(list: MutableList<UserModel>){
        if (list.isEmpty())
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        else
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        user_recyclerview?.layoutManager = layoutManager
    }


    override fun onItemClick(view: View, position: Int) {
        if (view.id == R.id.user_option_img) {
            showPopupMenu(view, userList.get(position))
        } else {
            var bundle = Bundle()
            bundle.putParcelable("user_details", userList.get(position))
            //Constant.switchMenu = true
            requireActivity().bottom_navigation1.visibility = View.GONE
            if (Constant.switchMenu) {
                AppNavigator.navigate(
                    requireActivity(),
                    R.id.fragment_container,
                    FragmentTag.USER_DETAILS_FRAGMENT, true, bundle
                )
            } else {
                AppNavigator.navigate(
                    requireActivity(),
                    R.id.fragment_container,
                    FragmentTag.USER_DETAILS_FRAGMENT, true, bundle
                )
            }
        }

    }

    private fun showPopupMenu(view: View, user: UserModel) {
        var popupMenu = PopupMenu(requireActivity(), view)
        popupMenu.inflate(R.menu.pop_menu)
        PopupIcon().insertMenuItemIcons(requireActivity(), popupMenu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.details -> {
                    var bundle = Bundle()
                    bundle.putParcelable("user_details", user)
                    //Constant.switchMenu = true
                    requireActivity().bottom_navigation1.visibility = View.GONE
                    if (Constant.switchMenu) {
                        AppNavigator.navigate(
                            requireActivity(),
                            R.id.fragment_container,
                            FragmentTag.USER_DETAILS_FRAGMENT, true, bundle
                        )
                    } else {
                        AppNavigator.navigate(
                            requireActivity(),
                            R.id.fragment_container,
                            FragmentTag.USER_DETAILS_FRAGMENT, true, bundle
                        )
                    }
                }
                R.id.edit -> {
                    var bundle = Bundle()
                    bundle.putSerializable("role_list", rolesList as Serializable)
                    bundle.putParcelable("user_data", user)

                    val dialog = CustomDialogFragment.newInstance(
                        requireActivity(),
                        object : AddUpdateListener {
                            override fun onAddOrUpdate(ok: Boolean, model: Payload?) {
                                updateUser(user.id, model!!)
                            }

                            override fun onCancel(cancel: Boolean) {

                            }
                        }, bundle, R.layout.add_user_dialog_fragment, "Update User", "Update"
                    )
                    //dialog.isCancelable = false
                    dialog.arguments = bundle
                    dialog.show(requireFragmentManager(), CustomDialogFragment.TAG)
                }
                R.id.delete -> {
                    Constant.showDialog(requireActivity(),
                        "Delete user",
                        "Do you want to delete this user?",
                        object : DialogCallback {
                            override fun onOk(ok: Boolean) {
                                if (ok) {
                                    deleteUser(user.id)
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
        userAdapter.filter.filter(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        userAdapter.filter.filter(newText)
        return false
    }

}