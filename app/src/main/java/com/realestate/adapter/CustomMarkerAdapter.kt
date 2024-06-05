package com.realestate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.model.Marker
import com.realestate.R
import com.realestate.model.DeviceDetailsModel
import com.realestate.model.DeviceModel

/**
 * Created by Chandan on 20/1/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
class CustomMarkerAdapter(private val context: Context) : InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? { //2

        // Return null to indicate that the
        // default window (white bubble) should be used
        return null
    }

    override fun getInfoContents(marker: Marker): View? {
        val markerItemView = LayoutInflater.from(context).inflate(
            R.layout.marker_info_window, null
        )

        val device = marker.tag
        if (device == null)
            return markerItemView
        else
            device as DeviceDetailsModel
        val device_id = markerItemView.findViewById<TextView>(R.id.device_id)
        val engineStatus = markerItemView.findViewById<TextView>(R.id.engine_status)
        device_id.text = device.deviceData
        if (device.engineStatus) {
            engineStatus.text = "ON"
            engineStatus.setTextColor(context.resources.getColor(R.color.active_color))
        }else{
            engineStatus.text = "OFF"
            engineStatus.setTextColor(context.resources.getColor(R.color.expired_color))
        }
        return markerItemView
    }
}