package com.realestate.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.realestate.R
import com.realestate.adapter.spinner.SpinnerRoleAdapter
import com.realestate.callback.ResponseCallback
import com.realestate.model.RolesModel
import com.realestate.restapi.ApiClient
import com.realestate.restapi.Payload
import com.realestate.restapi.RetrofitRequest
import com.realestate.utils.Constant
import com.realestate.utils.Constant.Companion.hideProgress
import com.realestate.utils.Constant.Companion.showProgress
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.layout_register.*
import okhttp3.RequestBody
import retrofit2.Call


class RegistrationActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var rolesList = mutableListOf<RolesModel>()
    private lateinit var adapter: SpinnerRoleAdapter
    private var role_id: Int = -1
    var animRightIn: Animation? = null
    var animLeftIn: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        //overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        animRightIn = AnimationUtils.loadAnimation(
            this,
            R.anim.right_to_left
        )
        animLeftIn = AnimationUtils.loadAnimation(
            this,
            R.anim.left_to_right
        )
        l_txt.startAnimation(animLeftIn)
        reg_profile_img.startAnimation(animRightIn)
        input_reg_first_name.startAnimation(animRightIn)
        input_reg_last_name.startAnimation(animLeftIn)
        input_reg_phone_no.startAnimation(animRightIn)
        input_reg_email.startAnimation(animLeftIn)
        input_reg_password.startAnimation(animRightIn)
        input_reg_con_password.startAnimation(animLeftIn)
        registration_role_spinner.startAnimation(animRightIn)
        reg_btn.startAnimation(animLeftIn)
        login_txt.setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
                    this@RegistrationActivity.finish()
                }

            }
        )
        adapter = SpinnerRoleAdapter(
            this@RegistrationActivity,
            rolesList
        )
        registration_role_spinner.adapter = adapter
        registration_role_spinner.setOnItemSelectedListener(this)
        getRoles()
        reg_btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                var email = reg_email.text.toString().trim()
                var password = reg_password.text.toString()
                var conPassword = reg_con_password.text.toString()
                var first_name = reg_first_name.text.toString().trim()
                var last_name = reg_last_name.text.toString().trim()
                var phone_no = reg_phone_no.text.toString().trim()

                var emailEmpty: Boolean
                var passwordEmpty: Boolean
                var conPasswordEmpty: Boolean
                var firstNameEmpty: Boolean
                var lastNameEmpty: Boolean
                var phoneEmpty: Boolean

                if (first_name.isEmpty()) {
                    input_reg_first_name.error = "Field must not be empty"
                    firstNameEmpty = true
                } else {
                    input_reg_first_name.error = ""
                    firstNameEmpty = false
                }

                if (last_name.isEmpty()) {
                    input_reg_last_name.error = "Field must not be empty"
                    lastNameEmpty = true
                } else {
                    input_reg_last_name.error = ""
                    lastNameEmpty = false
                }

                if (phone_no.isEmpty()) {
                    input_reg_phone_no.error = "Field must not be empty"
                    phoneEmpty = true
                } else {
                    input_reg_phone_no.error = ""
                    phoneEmpty = false
                }

                if (email.isEmpty()) {
                    input_reg_email.error = "Field must not be empty"
                    emailEmpty = true
                } else {
                    input_reg_email.error = ""
                    emailEmpty = false
                }

                if (password.isEmpty()) {
                    input_reg_password.error = "Field must not be empty"
                    passwordEmpty = true
                } else {
                    input_reg_password.error = ""
                    passwordEmpty = false
                }

                if (conPassword.isEmpty()) {
                    input_reg_con_password.error = "Field must not be empty"
                    conPasswordEmpty = true
                } else {
                    input_reg_con_password.error = ""
                    conPasswordEmpty = false
                }

                if (emailEmpty || passwordEmpty || firstNameEmpty || lastNameEmpty || phoneEmpty || conPasswordEmpty) {

                    return
                }

                if (phone_no.length >= 10) {
                    if (Constant.isValidEmail(email)) {
                        if (password.equals(conPassword)) {
                            if (role_id != -1) {
                                registration(
                                    first_name,
                                    last_name,
                                    phone_no,
                                    email,
                                    password,
                                    role_id
                                )
                            } else {
                                Toast.makeText(
                                    this@RegistrationActivity,
                                    "Please select role",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        } else {
                            input_reg_con_password.error = "Confirm password doesn't matched"
                        }

                    } else {
                        input_reg_email.error = "Invalid email format"
                    }
                } else {
                    input_reg_phone_no.error = "Invalid phone number"
                }


            }

        })
    }

    private fun registration(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        email: String,
        password: String,
        id: Int
    ) {
        showProgress(this, "loading...")
        val retrofit = ApiClient.instance?.getClient(false, this)

        val payload = Payload()
        payload.add("firstName", firstName)
        payload.add("lastName", lastName)
        payload.add("phoneNumber", phoneNumber)
        payload.add("password", password)
        payload.add("email", email)
        payload.add("roleId", id)

        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            payload.toString()
        )

        val call = retrofit?.userRegistration(body)

        RetrofitRequest().enqueue(
            call as Call<List<RolesModel>>,
            object : ResponseCallback<List<RolesModel>> {
                override fun onSuccess(response: List<RolesModel>) {
                    hideProgress()
                    (response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    rolesList.addAll(response)
                    adapter.notifyDataSetChanged()
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(this@RegistrationActivity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    private fun getRoles() {
        showProgress(this, "loading...")
        val retrofit = ApiClient.instance?.getClient(false, this)

        val payload = Payload()
        payload.add("user_id", 3)

        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            payload.toString()
        )

        val call = retrofit?.getAllRole()

        RetrofitRequest().enqueue(
            call as Call<List<RolesModel>>,
            object : ResponseCallback<List<RolesModel>> {
                override fun onSuccess(response: List<RolesModel>) {
                    hideProgress()
                    (response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    rolesList.addAll(response)
                    adapter.notifyDataSetChanged()
                }

                override fun onFailure(error: String?) {
                    hideProgress()
                    Toast.makeText(this@RegistrationActivity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        var roleModel = rolesList.get(p2)
        //Toast.makeText(this, "Click $roleModel", Toast.LENGTH_LONG).show()

        if (roleModel.roleType != "Select role") {
            role_id = roleModel.id
        }
    }


}