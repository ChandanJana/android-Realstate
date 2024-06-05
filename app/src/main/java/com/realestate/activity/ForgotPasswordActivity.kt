package com.realestate.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.realestate.R
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.layout_login.*
import kotlinx.android.synthetic.main.toolbar.*

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        //overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
        //tool_layout?.setBackgroundColor(resources.getColor(R.color.colorAccent))
        //toolbar_top_layout?.setBackgroundColor(resources.getColor(R.color.colorAccent))
        txtViewToolbarTitle.text = "Forgot Password"
        add_img.visibility = View.GONE
        refresh_img.visibility = View.GONE
        imgViewBack.setImageResource(R.drawable.ic_back)
        //imgViewBack.setBackgroundColor(resources.getColor(R.color.colorAccent))
        imgViewBack.setOnClickListener {
            onBackPressed()
        }
        send_btn.setOnClickListener {
            sendMail()
        }
    }

    private fun sendMail() {
        var email = forgot_email.text.toString().trim()

        var emailEmpty: Boolean

        if (email.isEmpty()) {
            input_forgot_email.error = "Field must not be empty"
            emailEmpty = true
        } else {
            input_user_login_phone_no.error = ""
            emailEmpty = false
        }

        if (emailEmpty ) {

            return
        }

    }
}