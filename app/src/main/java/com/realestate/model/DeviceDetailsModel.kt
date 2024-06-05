package com.realestate.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Chandan on 17/06/21
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */

@Parcelize
data class DeviceDetailsModel (
    var date: String,
    var lng: Double,
    var engineStatus: Boolean,
    var deviceData: String,
    var time: String,
    var vehicleId: Int,
    var id: Int,
    var deviceId: Int,
    var lat: Double,
    var speed: Double,
    var vehicleStatus: String,
    var vehicle: VehicleModel? = null,

):Parcelable