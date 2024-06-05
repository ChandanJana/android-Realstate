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
data class BranchModel(
    @field:SerializedName("phoneNumber")
    val phoneNumber: String? = null,

    @field:SerializedName("branchName")
    val branchName: String? = null,

    @field:SerializedName("branchAddress")
    val branchAddress: String? = null,

    @field:SerializedName("emailId")
    val emailId: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("user")
    val user: UserModel? = null,

    @field:SerializedName("userId")
    val userId: Int? = null
) : Parcelable {
}