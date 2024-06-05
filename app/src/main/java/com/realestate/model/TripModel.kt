package com.realestate.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Chandan on 27/12/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
@Parcelize
data class TripModel(
    val tollCharge: Double,
    val advanceAmount: Double,
    val finalCost: Double,
    val fuelQuantity: Double,
    val vehicle: VehicleModel?,
    val startLocation: String,
    val tripCost: Double,
    val tripsNo: String,
    val fuelPrice: Double,
    val startTime: String,
    val endTime: String,
    val company: CompanyModel?,
    val vehicleId: Int,
    val id: Int,
    val totalDistance: Double,
    val endLocation: String,
    val endDistance: Double,
    val totalTime: String,
    val totalMaterialCost: Double,
    val dealerId: Int,
    val operationCost: Double,
    val weight: Double,
    val userId: Int,
    val startDistance: Double,
    val companyId: Int,
    val due: Double,
    val fuelCharge: Double,
    val dealer: DealerModel?,
    val invoice: InvoiceModel?,
    val user: DriverModel?,
    val tripStatus: String
) : Parcelable {
}