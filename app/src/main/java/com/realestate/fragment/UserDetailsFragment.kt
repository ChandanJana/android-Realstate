package com.realestate.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.realestate.R
import com.realestate.model.UserModel
import kotlinx.android.synthetic.main.fragment_user_details.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * A simple [Fragment] subclass.
 * Use the [UserDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserDetailsFragment : Fragment() {

    var userModel: UserModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = arguments
        userModel = bundle?.getParcelable("user_details")
        Log.d("UserDetails", "userModel: $userModel")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().runOnUiThread(Runnable {
            try {
                requireActivity().txtViewToolbarTitle.text =
                    activity?.resources?.getString(R.string.users_details)
                activity?.imgViewBack?.setImageResource(R.drawable.ic_back)
                activity?.imgViewBack?.tag = "back"
            } catch (e: Exception) {

            }
        })

        activity?.refresh_img?.visibility = View.GONE
        activity?.add_img?.visibility = View.GONE
        activity?.filter_spinner?.visibility = View.GONE

        if (userModel != null) {
            user_id.text = userModel?.id.toString()
            user_name.text = userModel?.userName.toString()
            user_full_name.text = userModel?.firstName + " " + userModel?.lastName
            user_gender.text = userModel?.gender.toString()
            user_contact.text = userModel?.phoneNumber.toString()
            user_email.text = userModel?.email.toString()
            user_alternate_contact.text = userModel?.alternateNumber.toString()
            user_address.text = userModel?.address.toString()
            user_aadhar_no.text = userModel?.aadharNumber.toString()
            user_password.text = userModel?.password.toString()
            user_licence_id.text = ""
            user_role.text = userModel?.role?.roleType.toString()
        }

    }

    companion object {

        @JvmStatic
        fun newInstance() =
            UserDetailsFragment()
    }
}