package com.realestate.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Chandan on 8/1/21
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
@Parcelize
data class ProfileModel(

    val lastName: String,
    val address: String,
    val gender: String,
    val roleId: Int,
    val userName: String,
    val refreshTokenExpiryTime: String,
    val firstName: String,
    val bloodGroup: String,
    val password: String,
    val phoneNumber: String,
    val aadharNumber: String,
    val alternateNumber: String,
    val id: Int,
    val email: String,
    val refreshToken: String, ) : Parcelable {
}