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
data class RolesModel(
    val id: Int,
    val roleType: String,
    val normalizedName: String?
) : Parcelable {

    companion object : Parceler<RolesModel> {
        override fun RolesModel.write(parcel: Parcel, p1: Int) {
            parcel.writeInt(id)
            parcel.writeString(roleType)
            parcel.writeString(normalizedName)
        }

        override fun create(parcel: Parcel): RolesModel =
            RolesModel(parcel.readInt(), parcel.readString()!!, parcel.readString())
    }
}