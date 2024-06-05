package com.realestate.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Chandan on 27/12/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
@Parcelize
data class CompanyModel(
    val address: String,
    val material: MaterialModel?,
    val name: String,
    val id: Int,
    val materialId: Int
) : Parcelable {
}