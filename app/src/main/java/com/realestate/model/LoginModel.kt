package com.realestate.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Chandan on 8/1/21
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
@Parcelize
data class LoginModel(

    val lastName: String,
    val role: String,
    val address: String,
    val userName: String,
    val token: String,
    val firstName: String,
    val password: String,
    val phoneNumber: String,
    val aadharNumber: String,
    val alternateNumber: String,
    val id: Int,
    val email: String,
    val refreshToken: String, ) : Parcelable {
}