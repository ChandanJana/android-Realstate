package com.realestate.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize

/**
 * Created by Chandan on 27/12/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
@Parcelize
data class UserModel(
    val lastName: String,
    val address: String,
    val role: RolesModel?,
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

    private companion object : Parceler<UserModel> {
        override fun UserModel.write(parcel: Parcel, flags: Int) {
            // Custom write implementation
            parcel.writeString(lastName)
            parcel.writeString(address)
            parcel.writeParcelable(role, flags)
            parcel.writeString(gender)
            parcel.writeInt(roleId)
            parcel.writeString(userName)
            parcel.writeString(refreshTokenExpiryTime)
            parcel.writeString(firstName)
            parcel.writeString(bloodGroup)
            parcel.writeString(password)
            parcel.writeString(phoneNumber)
            parcel.writeString(aadharNumber)
            parcel.writeString(alternateNumber)
            parcel.writeInt(id)
            parcel.writeString(email)
            parcel.writeString(refreshToken)

        }

        override fun create(parcel: Parcel): UserModel {
            // Custom read implementation
            return UserModel(
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readParcelable(RolesModel::class.java.classLoader)!!,
                parcel.readString()!!, parcel.readInt(),
                parcel.readString()!!, parcel.readString()!!,
                parcel.readString()!!, parcel.readString()!!,
                parcel.readString()!!, parcel.readString()!!,
                parcel.readString()!!, parcel.readString()!!,
                parcel.readInt(), parcel.readString()!!,
                parcel.readString()!!
            )
        }
    }

}