package com.realestate.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StateModel(

    @field:SerializedName("stateList")
    val stateList: List<String>
) : Parcelable

