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
data class InsuranceModel(

    @field:SerializedName("premium")
    val premium: Double? = null,

    @field:SerializedName("endDate")
    val endDate: String? = null,

    @field:SerializedName("provider")
    val provider: String? = null,

    @field:SerializedName("insuranceNo")
    val insuranceNo: String? = null,

    @field:SerializedName("insuranceStatus")
    val insuranceStatus: String? = null,

    @field:SerializedName("vehicleId")
    val vehicleId: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("startDate")
    val startDate: String? = null,

    @field:SerializedName("vehicle")
    val vehicle: VehicleModel? = null
) : Parcelable