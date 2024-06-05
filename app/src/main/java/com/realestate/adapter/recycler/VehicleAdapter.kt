package com.realestate.adapter.recycler

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.realestate.R
import com.realestate.callback.ItemClickListener
import com.realestate.model.UserModel
import com.realestate.model.VehicleModel
import kotlinx.android.synthetic.main.custom_vehicle_row.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Chandan on 29/8/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
private const val VIEW_TYPE_MY_TRIP = 1
private const val VIEW_TYPE_EMPTY_TRIP = 2

class VehicleAdapter(
    context: Context,
    vehicleList: List<VehicleModel>,
    listener: ItemClickListener
) : RecyclerView.Adapter<VehicleViewHolder>(), Filterable {
    private var vehicleList: List<VehicleModel>? = null
    private var vehicleListFinal: List<VehicleModel>? = null
    private var context: Context? = null
    private var listener: ItemClickListener? = null

    init {
        this.vehicleList = vehicleList
        this.vehicleListFinal = vehicleList
        this.context = context
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return if (vehicleList?.size!! == 0) 1 else vehicleList?.size!!
    }

    override fun getItemViewType(position: Int): Int {

        return if (vehicleList?.size == 0) {
            VIEW_TYPE_EMPTY_TRIP
        } else {
            VIEW_TYPE_MY_TRIP
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        return if (viewType == VIEW_TYPE_MY_TRIP) {
            MyVehicleViewHolder(
                LayoutInflater.from(context).inflate(R.layout.custom_vehicle_row, parent, false)
            )
        } else {
            EmptyViewHolder(
                LayoutInflater.from(context).inflate(R.layout.empty_row, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {

        if (holder is MyVehicleViewHolder) {
            val trip = vehicleList?.get(position)
            holder.bind(trip!!)
        } else {
            holder.bind(null)
        }
    }

    inner class MyVehicleViewHolder(view: View) : VehicleViewHolder(view), View.OnClickListener {

        override fun bind(message: VehicleModel?) {
            if (adapterPosition % 2 == 0) {

                val gd = GradientDrawable(
                    GradientDrawable.Orientation.BOTTOM_TOP,
                    intArrayOf(Color.parseColor("#000000"), Color.parseColor("#ffffff"))
                )
                gd.cornerRadius = 20.0f
                gd.setDither(true)
                itemView.setBackground(gd)
            } else {

                val gd = GradientDrawable(
                    GradientDrawable.Orientation.BOTTOM_TOP,
                    intArrayOf(Color.parseColor("#000000"), Color.parseColor("#ffffff"))
                )
                gd.cornerRadius = 20.0f
                gd.setDither(true)
                itemView.setBackground(gd)
            }
            itemView.vehicle_owner_name.setText(message?.owner)
            itemView.vehicles_reg_year.setText(message?.registeredYear?.substring(0, 10))
            itemView.vehicles_reg_no.setText(message?.vehicleRegisteredNo)

            if (message?.insurance?.insuranceStatus.equals("Expired")) {
                itemView.vehicle_insurance_status.setBackgroundDrawable(context?.resources?.getDrawable(R.drawable.round_button_end1))
                itemView.vehicle_insurance.setTextColor(context?.resources?.getColor(R.color.expired_color)!!)
            } else {
                itemView.vehicle_insurance_status.setBackgroundDrawable(context?.resources?.getDrawable(R.drawable.round_button_start1))
                itemView.vehicle_insurance.setTextColor(context?.resources?.getColor(R.color.active_color)!!)
            }
            if (message?.pollution?.pollutionStatus.equals("Expired")) {
                itemView.vehicle_pollution_status.setBackgroundDrawable(context?.resources?.getDrawable(R.drawable.round_button_end1))
                itemView.vehicle_pollution.setTextColor(context?.resources?.getColor(R.color.expired_color)!!)
            } else {
                itemView.vehicle_pollution_status.setBackgroundDrawable(context?.resources?.getDrawable(R.drawable.round_button_start1))
                itemView.vehicle_pollution.setTextColor(context?.resources?.getColor(R.color.active_color)!!)
            }
            if (message?.permit?.permitStatus.equals("Expired")) {
                itemView.vehicle_permit_status.setBackgroundDrawable(context?.resources?.getDrawable(R.drawable.round_button_end1))
                itemView.vehicle_permit.setTextColor(context?.resources?.getColor(R.color.expired_color)!!)
            } else {
                itemView.vehicle_permit_status.setBackgroundDrawable(context?.resources?.getDrawable(R.drawable.round_button_start1))
                itemView.vehicle_permit.setTextColor(context?.resources?.getColor(R.color.active_color)!!)
            }

            itemView.vehicle_insurance.setText(message?.insurance?.insuranceStatus)
            itemView.vehicle_pollution.setText(message?.pollution?.pollutionStatus)
            itemView.vehicle_permit.setText(message?.permit?.permitStatus)
            itemView.setOnClickListener(this)
            itemView.vehicle_option_img.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            if (listener != null)
                listener?.onItemClick(p0!!, adapterPosition)
        }
    }

    fun getDateCurrentTimeZone(timestamp: Long): String? {
        var formatter: SimpleDateFormat = SimpleDateFormat("hh:mm a")
        var dateString: String = formatter.format(Date((timestamp)))

        dateString = dateString.replace("am", "AM").replace("pm", "PM")

        return dateString
    }

    inner class EmptyViewHolder(view: View) : VehicleViewHolder(view) {
        private var emptyText: TextView = view.findViewById(R.id.empty_txt)
        override fun bind(message: VehicleModel?) {
            emptyText.text = "No vehicles available"
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    vehicleList = vehicleListFinal
                } else {
                    val filteredList: MutableList<VehicleModel> = ArrayList()
                    for (vehicle in vehicleListFinal!!) {
                        if (vehicle.vehicleRegisteredNo.toLowerCase(Locale.ROOT).contains(charString.toLowerCase(Locale.ROOT) )
                        ) {
                            filteredList.add(vehicle)
                        }
                    }
                    vehicleList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = vehicleList
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                vehicleList = filterResults.values as ArrayList<VehicleModel>
                notifyDataSetChanged()
            }
        }
    }
}

open class VehicleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(message: VehicleModel?) {}
}