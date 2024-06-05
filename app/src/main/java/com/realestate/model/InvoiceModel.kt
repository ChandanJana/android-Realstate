package com.realestate.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Chandan on 27/12/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
@Parcelize
data class InvoiceModel(
    val invoiceNo: String,
    val invoiceDate: String,
    val orderId: String,
    val tripId: Int,
    val id: Int,
    val trips: TripModel
) : Parcelable {
}