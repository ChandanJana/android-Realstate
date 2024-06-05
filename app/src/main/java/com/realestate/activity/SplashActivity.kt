package com.realestate.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.realestate.R
import com.realestate.storage.SharedPreferenceManager
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.fragment_trip_details.*


class SplashActivity : AppCompatActivity() {
    var animRightIn: Animation? = null
    var animLeftIn: Animation? = null
    var animLeftInq: Animation? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        fullScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        animRightIn = AnimationUtils.loadAnimation(
            this,
            R.anim.right_to_left
        )
        animLeftIn = AnimationUtils.loadAnimation(
            this,
            R.anim.left_to_right
        )
        animLeftInq = AnimationUtils.loadAnimation(
            this,
            R.anim.item_animation_fall_down
        )

       // overridePendingTransition(R.anim.up_to_down, R.anim.down_to_up)

        splash_txt.startAnimation(animRightIn)
        splash_img.startAnimation(animLeftIn)

        Handler().postDelayed(object : Runnable {
            override fun run() {
                if (!SharedPreferenceManager.getInstance(this@SplashActivity)
                        ?.getBoolean(SharedPreferenceManager.Key.IS_LOGGEDIN)!!
                ) {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    this@SplashActivity.finish()
                } else {
                    startActivity(Intent(this@SplashActivity, DashBoardActivity::class.java))
                    this@SplashActivity.finish()
                }
            }
        }, 2000)
    }

    fun fullScreen() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            val v: View = this.window.decorView
            v.setSystemUiVisibility(View.GONE)
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            val decorView: View = window.decorView
            val uiOptions: Int =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            decorView.setSystemUiVisibility(uiOptions)
        }
    }
}