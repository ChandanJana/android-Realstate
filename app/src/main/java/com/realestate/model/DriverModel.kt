package com.realestate.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Chandan on 27/12/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
@Parcelize
data class DriverModel(

    @field:SerializedName("lastName")
    val lastName: String? = null,

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("role")
    val role: RolesModel? = null,

    @field:SerializedName("gender")
    val gender: String? = null,

    @field:SerializedName("roleId")
    val roleId: Int? = null,

    @field:SerializedName("userName")
    val userName: String? = null,

    @field:SerializedName("refreshTokenExpiryTime")
    val refreshTokenExpiryTime: String? = null,

    @field:SerializedName("firstName")
    val firstName: String? = null,

    @field:SerializedName("bloodGroup")
    val bloodGroup: String? = null,

    @field:SerializedName("phoneNumber")
    val phoneNumber: String? = null,

    @field:SerializedName("aadharNumber")
    val aadharNumber: String? = null,

    @field:SerializedName("alternateNumber")
    val alternateNumber: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("email")
    val email: String? = null
) : Parcelable {}