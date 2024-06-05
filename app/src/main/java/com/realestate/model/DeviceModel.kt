package com.realestate.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Chandan on 17/06/21
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */

@Parcelize
data class DeviceModel (
    var vehicleId: Int,
    var id: Int,
    var deviceId: String,
    var lng: Double,
    var lat: Double
):Parcelable