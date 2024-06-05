package com.realestate.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Chandan on 27/12/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
@Parcelize
data class VehicleModel(
    val owner: String,
    val id: Int,
    val vehicleRegisteredNo: String,
    val registeredOffice: String,
    val registeredYear: String,
    val chasisNo: String,
    val insurance: InsuranceModel? = null,
    val permit: PermitModel? = null,
    val pollution: PollutionModel? = null,
    val vehicleCategory: VehicleCategory? = null,
    val device: DeviceModel? = null,
    val branch: BranchModel? = null,
) : Parcelable {

}