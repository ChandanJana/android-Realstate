package com.realestate.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Chandan on 18/06/21
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */

@Parcelize
data class VehicleCategory (
    var id: String? = null,
    var categoryName: String? = null
):Parcelable