package com.realestate.activity

import android.os.Bundle
import android.view.View
import androidx.annotation.Nullable

import androidx.appcompat.app.AppCompatActivity
import com.realestate.app.RealEstateApp


/**
 * Created by Chandan on 2/3/21
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
class BaseActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /*override fun onUserInteraction() {
        super.onUserInteraction()
        RealEstateApp.sInstance.touch()
    }*/

    override fun onClick(p0: View?) {

    }
}