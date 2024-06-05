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
data class PollutionModel(

    @field:SerializedName("pollutionNo")
    val pollutionNo: String,

    @field:SerializedName("endDate")
    val endDate: String,

    @field:SerializedName("price")
    val price: Double,

    @field:SerializedName("vehicleId")
    val vehicleId: Int,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("startDate")
    val startDate: String,

    @field:SerializedName("pollutionStatus")
    val pollutionStatus: String,

    @field:SerializedName("vehicle")
    val vehicle: VehicleModel
) : Parcelable
