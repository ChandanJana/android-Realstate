package com.realestate.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Chandan on 27/12/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
@Parcelize
data class DealerModel(
    val pincode: Long,
    val dealerName: String,
    val phoneNumber: String,
    val address: String,
    val dealerNo: String,
    val state: String,
    val id: Int, ) : Parcelable {
}