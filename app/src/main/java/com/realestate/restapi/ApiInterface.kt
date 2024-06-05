package com.realestate.restapi

import com.realestate.model.*
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by Chandan on 26/12/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
interface ApiInterface {

    @POST("Auth/Register")
    fun userRegistration(@Body requestBody: RequestBody?): Call<List<RolesModel>>

    @GET("userprofile/{id}")
    fun userProfile(@Path("id") id: Int): Call<ProfileModel>

    @PUT("userprofile/{id}")
    fun profileUpdate(@Path("id") id: Int, @Body requestBody: RequestBody?): Call<UserModel>

    @GET("User")
    fun getAllUser(): Call<List<UserModel>>

    @DELETE("User/{id}")
    fun deleteUser(@Path("id") id: Int): Call<UserModel>

    @PUT("User/{id}")
    fun updateUser(@Path("id") id: Int, @Body requestBody: RequestBody?): Call<UserModel>

    @POST("User")
    fun addUser(@Body requestBody: RequestBody?): Call<UserModel>

    @POST("Auth/login")
    fun loginUser(@Body requestBody: RequestBody?): Call<LoginModel>

    @POST("vehicle")
    fun addVehicle(@Body requestBody: RequestBody?): Call<VehicleModel>

    @PUT("vehicle/{id}")
    fun updateVehicle(@Path("id") id: Int, @Body requestBody: RequestBody?): Call<VehicleModel>

    @DELETE("vehicle/{id}")
    fun deleteVehicle(@Path("id") id: Int): Call<VehicleModel>

    @GET("vehicle")
    fun getAllVehicle(): Call<List<VehicleModel>>

    @GET("VehicleCategory")
    fun getAllVehicleCategory(): Call<List<VehicleCategory>>

    @GET("vehicle/GetVehiclesCount")
    fun vehicleCount(): Call<Int>

    @GET("User/GetUsersCount")
    fun userCount(): Call<Int>

    @GET("Trips/GetTripsCount")
    fun tripCount(): Call<Int>

    @GET("Branch/GetBranchesCount")
    fun branchCount(): Call<Int>

    @GET("Trips")
    fun getAllTrip(): Call<List<TripModel>>

    @POST("Trips")
    fun addTrip(@Body requestBody: RequestBody?): Call<TripModel>

    @PUT("Trips/{id}")
    fun updateTrip(@Path("id") id: Int, @Body requestBody: RequestBody?): Call<TripModel>

    @DELETE("Trips/{id}")
    fun deleteTrip(@Path("id") id: Int): Call<TripModel>

    @GET("User/GetDrivers")
    fun getAllDriver(): Call<List<DriverModel>>

    @GET("Branch")
    fun getAllBranch(): Call<List<BranchModel>>

    @DELETE("Branch/{id}")
    fun deleteBranch(@Path("id") id: Int): Call<BranchModel>

    @PUT("Branch/{id}")
    fun updateBranch(@Path("id") id: Int, @Body requestBody: RequestBody?): Call<BranchModel>

    @POST("Branch")
    fun addBranch(@Body requestBody: RequestBody?): Call<BranchModel>

    @GET("Insurance")
    fun getAllInsurance(): Call<List<InsuranceModel>>

    @POST("Insurance")
    fun addInsurance(@Body requestBody: RequestBody?): Call<InsuranceModel>

    @PUT("Insurance/{id}")
    fun updateInsurance(@Path("id") id: Int, @Body requestBody: RequestBody?): Call<InsuranceModel>

    @DELETE("Insurance/{id}")
    fun deleteInsurance(@Path("id") id: Int): Call<InsuranceModel>

    @GET("Pollution")
    fun getAllPollution(): Call<List<PollutionModel>>

    @POST("Pollution")
    fun addPollution(@Body requestBody: RequestBody?): Call<PollutionModel>

    @PUT("Pollution/{id}")
    fun updatePollution(@Path("id") id: Int, @Body requestBody: RequestBody?): Call<PollutionModel>

    @DELETE("Pollution/{id}")
    fun deletePollution(@Path("id") id: Int): Call<PollutionModel>

    @GET("Permit")
    fun getAllPermit(): Call<List<PermitModel>>

    @POST("Permit")
    fun addPermit(@Body requestBody: RequestBody?): Call<PermitModel>

    @PUT("Permit/{id}")
    fun updatePermit(@Path("id") id: Int, @Body requestBody: RequestBody?): Call<PermitModel>

    @DELETE("Permit/{id}")
    fun deletePermit(@Path("id") id: Int): Call<PermitModel>

    @GET("Company")
    fun getAllCompany(): Call<List<CompanyModel>>

    @POST("Company")
    fun addCompany(@Body requestBody: RequestBody?): Call<CompanyModel>

    @PUT("Company/{id}")
    fun updateCompany(@Path("id") id: Int, @Body requestBody: RequestBody?): Call<CompanyModel>

    @DELETE("Company/{id}")
    fun deleteCompany(@Path("id") id: Int): Call<CompanyModel>

    @GET("Material")
    fun getAllMaterial(): Call<List<MaterialModel>>

    @POST("Material")
    fun addMaterial(@Body requestBody: RequestBody?): Call<MaterialModel>

    @PUT("Material/{id}")
    fun updateMaterial(@Path("id") id: Int, @Body requestBody: RequestBody?): Call<MaterialModel>

    @DELETE("Material/{id}")
    fun deleteMaterial(@Path("id") id: Int): Call<MaterialModel>

    @GET("Role")
    fun getAllRole(): Call<List<RolesModel>>

    @POST("Role")
    fun addRole(@Body requestBody: RequestBody?): Call<RolesModel>

    @PUT("Role/{id}")
    fun updateRole(@Path("id") id: Int, @Body requestBody: RequestBody?): Call<RolesModel>

    @DELETE("Role/{id}")
    fun deleteRole(@Path("id") id: Int): Call<RolesModel>

    @GET("Dealer")
    fun getAllDealer(): Call<List<DealerModel>>

    @POST("Dealer")
    fun addDealer(@Body requestBody: RequestBody?): Call<DealerModel>

    @PUT("Dealer/{id}")
    fun updateDealer(@Path("id") id: Int, @Body requestBody: RequestBody?): Call<DealerModel>

    @DELETE("Dealer/{id}")
    fun deleteDealer(@Path("id") id: Int): Call<DealerModel>

    @GET("Invoice")
    fun getAllInvoice(): Call<List<InvoiceModel>>

    @POST("Invoice")
    fun addInvoice(@Body requestBody: RequestBody?): Call<InvoiceModel>

    @PUT("Invoice/{id}")
    fun updateInvoice(@Path("id") id: Int, @Body requestBody: RequestBody?): Call<InvoiceModel>

    @DELETE("Invoice/{id}")
    fun deleteInvoice(@Path("id") id: Int): Call<InvoiceModel>

    @GET("Device")
    fun getDeviceList(): Call<List<DeviceModel>>

    @GET("DeviceDetails")
    fun getDeviceDetailsList(): Call<List<DeviceDetailsModel>>

    @GET("DeviceDetails/{id}")
    fun getDeviceDetailsList(@Path("id") id: Int): Call<DeviceDetailsModel>

    @GET("DeviceDetails/filterbydate")
    fun getDeviceDetailsByDate(@Query("date") date: String): Call<DeviceFilterModel>

    @GET("DeviceDetails/filterbyvehicleRegNodate")
    fun getDeviceDetailsByVehicleregAndDate(@QueryMap vehicleRegAndDate:Map<String, String>): Call<DeviceFilterModel>

    @GET("DeviceDetails/filterbyreport")
    fun getDeviceDetailsByFromdateAndTodate(@QueryMap fromdateTodate:Map<String, String>): Call<DeviceFilterModel>

    @GET("DeviceDetails/filterbybranchdate")
    fun getDeviceDetailsByBranchAndDate(@QueryMap branchdate:Map<String, String>): Call<DeviceFilterModel>

}