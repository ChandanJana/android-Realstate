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
data class PermitModel(

    @field:SerializedName("premium")
    val premium: Double,

    @field:SerializedName("permitType")
    val permitType: String,

    @field:SerializedName("endDate")
    val endDate: String,

    @field:SerializedName("permitStatus")
    val permitStatus: String,

    @field:SerializedName("vehicleId")
    val vehicleId: Int,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("permitNo")
    val permitNo: String,

    @field:SerializedName("startDate")
    val startDate: String,

    @field:SerializedName("vehicle")
    val vehicle: VehicleModel

) : Parcelable