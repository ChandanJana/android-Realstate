package com.realestate.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.realestate.R
import com.realestate.callback.ResponseCallback
import com.realestate.model.LoginModel
import com.realestate.restapi.ApiClient
import com.realestate.restapi.Payload
import com.realestate.restapi.RetrofitRequest
import com.realestate.storage.SharedPreferenceManager
import com.realestate.utils.Constant
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.layout_login.*
import okhttp3.RequestBody
import retrofit2.Call

class LoginActivity : AppCompatActivity() {
    var animRightIn: Animation? = null
    var animLeftIn: Animation? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        animRightIn = AnimationUtils.loadAnimation(
            this,
            R.anim.right_to_left
        )
        animLeftIn = AnimationUtils.loadAnimation(
            this,
            R.anim.left_to_right
        )
        welcome?.startAnimation(animRightIn)
        sign_in.startAnimation(animLeftIn)
        input_user_login_phone_no.startAnimation(animRightIn)
        input_user_login_password.startAnimation(animLeftIn)
        forgot_password_txt.startAnimation(animRightIn)
        login_btn.startAnimation(animLeftIn)
        registration_txt.setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))
                    this@LoginActivity.finish()
                }

            }
        )

        login_btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var email = user_login_phone_no.text.toString().trim()
                var password = user_login_password.text.toString()

                var emailEmpty = false
                var passwordEmpty = false

                if (email.isEmpty()) {
                    input_user_login_phone_no.error = "Field must not be empty"
                    emailEmpty = true
                } else {
                    input_user_login_phone_no.error = ""
                    emailEmpty = false
                }
                if (password.isEmpty()) {
                    input_user_login_password.error = "Field must not be empty"
                    passwordEmpty = true
                } else {
                    input_user_login_password.error = ""
                    passwordEmpty = false
                }

                if (emailEmpty || passwordEmpty) {

                    return
                }

                if (email.length < 10) {
                    input_user_login_phone_no.error = "Invalid number"
                    return
                } else {
                    input_user_login_phone_no.error = ""
                }

                login(email, password)
                /*if (email.equals("Admin@gmail.com") && password.equals("admin")) {
                    SharedPreferenceManager.getInstance(this@LoginActivity)?.putBoolean(SharedPreferenceManager.Key.IS_LOGGEDIN, true)
                    startActivity(Intent(this@LoginActivity, DashBoardActivity::class.java))
                    this@LoginActivity.finish()
                }else{
                    input_user_login_email.error = ""
                    input_user_login_password.error = ""
                    Toast.makeText(this@LoginActivity, "Email or password is wrong", Toast.LENGTH_LONG).show()
                }*/
            }

        })

        forgot_password_txt.setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
                    //this@LoginActivity.finish()
                }

            }
        )

    }

    private fun login(email: String, password: String) {
        Constant.showProgress(this, "login...")
        val retrofit = ApiClient.instance?.getClient(false, this)
        val payload = Payload()

        payload.add("password", password)
        payload.add("phoneNumber", email)

        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            payload.toString()
        )
        val call = retrofit?.loginUser(body)

        RetrofitRequest().enqueue(
            call as Call<LoginModel>,
            object : ResponseCallback<LoginModel> {
                override fun onSuccess(response: LoginModel) {
                    Constant.hideProgress()
                    //(response as MutableList).add(0, RolesModel(-1, "Select role", "SELECT ROLE"))
                    if (response != null) {
                        SharedPreferenceManager.getInstance(this@LoginActivity)
                            ?.putString(
                                SharedPreferenceManager.Key.AUTH_TOKEN,
                                "Bearer ${response.token}"
                            )
                        SharedPreferenceManager.getInstance(this@LoginActivity)
                            ?.putBoolean(SharedPreferenceManager.Key.IS_LOGGEDIN, true)
                        SharedPreferenceManager.getInstance(this@LoginActivity)
                            ?.putInt(SharedPreferenceManager.Key.USER_ID, response.id)
                        SharedPreferenceManager.getInstance(this@LoginActivity)
                            ?.putString(
                                SharedPreferenceManager.Key.USER_NAME,
                                (response.firstName + " " + response.lastName)
                            )
                        SharedPreferenceManager.getInstance(this@LoginActivity)
                            ?.putString(SharedPreferenceManager.Key.USER_EMAIL, response.email)
                        startActivity(Intent(this@LoginActivity, DashBoardActivity::class.java))
                        this@LoginActivity.finish()
                    }
                }

                override fun onFailure(error: String?) {
                    Constant.hideProgress()
                    Toast.makeText(this@LoginActivity, "Error $error", Toast.LENGTH_LONG)
                        .show()
                }

            })
    }
}