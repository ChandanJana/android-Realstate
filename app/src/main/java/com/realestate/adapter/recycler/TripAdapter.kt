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
import com.realestate.model.TripModel
import com.realestate.model.UserModel
import com.realestate.utils.Constant
import kotlinx.android.synthetic.main.custom_trip_row1.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Chandan on 29/8/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
private const val VIEW_TYPE_MY_TRIP = 1
private const val VIEW_TYPE_EMPTY_TRIP = 2

class TripAdapter(context: Context, tripList: List<TripModel>, listener: ItemClickListener) :
    RecyclerView.Adapter<TripViewHolder>(), Filterable {
    private var tripList: List<TripModel>? = null
    private var tripListFinal: List<TripModel>? = null
    private var context: Context? = null
    private var listener: ItemClickListener? = null

    init {
        this.tripList = tripList
        this.tripListFinal = tripList
        this.context = context
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return if (tripList?.size!! == 0) 1 else tripList?.size!!
    }

    override fun getItemViewType(position: Int): Int {

        return if (tripList?.size == 0) {
            VIEW_TYPE_EMPTY_TRIP
        } else {
            VIEW_TYPE_MY_TRIP
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        return if (viewType == VIEW_TYPE_MY_TRIP) {
            MyTripViewHolder(
                LayoutInflater.from(context).inflate(R.layout.custom_trip_row1, parent, false)
            )
        } else {
            EmptyViewHolder(
                LayoutInflater.from(context).inflate(R.layout.empty_row, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {

        if (holder is MyTripViewHolder) {
            val trip = tripList?.get(position)
            holder.bind(trip!!)
        } else {
            holder.bind(null)
        }
    }

    inner class MyTripViewHolder(view: View) : TripViewHolder(view), View.OnClickListener {

        override fun bind(message: TripModel?) {
            if (adapterPosition % 2 == 0) {

                val gd = GradientDrawable(
                    GradientDrawable.Orientation.RIGHT_LEFT,
                    intArrayOf(Color.parseColor("#000000"), Color.parseColor("#ffffff"))
                )
                gd.cornerRadius = 20.0f
                gd.setDither(true)
                itemView.setBackground(gd)
            } else {

                val gd = GradientDrawable(
                    GradientDrawable.Orientation.RIGHT_LEFT,
                    intArrayOf(Color.parseColor("#000000"), Color.parseColor("#ffffff"))
                )
                gd.cornerRadius = 20.0f
                gd.setDither(true)
                itemView.setBackground(gd)
            }
            itemView.trip_no.text = message?.tripsNo
            itemView.trip_start_location.text = message?.startLocation.toString()
            itemView.trip_end_location.text = message?.endLocation.toString()
            itemView.trip_start_date.text =
                Constant.convertStartStringToTime(message?.startTime!!, true)
            if (message.endTime != "" && message.endTime != null) {
                itemView.trip_end_date.text = Constant.convertEndStringToTime(message.endTime, true)
                itemView.trip_date_dash.visibility = View.VISIBLE
                itemView.trip_end_date.visibility = View.VISIBLE
            }
            //itemView.material_name.text = message?.company?.material?.materialName
            //itemView.fright.text = message?.weight.toString()
            itemView.trip_cost.text = message.tripCost.toString()
            if (message.tripStatus.equals("Started")) {
                itemView.trip_status.setBackgroundDrawable(context?.resources?.getDrawable(R.drawable.round_button_start))
            } else if (message.tripStatus.equals("Ended")) {
                itemView.trip_status.setBackgroundDrawable(context?.resources?.getDrawable(R.drawable.round_button_end))
            }
            itemView.trip_status.text = message.tripStatus
            //itemView.chasis_no.text = message?.vehicle?.chasisNo
            itemView.setOnClickListener(this)
            itemView.trip_option_img.setOnClickListener(this)
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

    inner class EmptyViewHolder(view: View) : TripViewHolder(view) {
        private var emptyText: TextView = view.findViewById(R.id.empty_txt)
        override fun bind(message: TripModel?) {
            emptyText.text = "No trips available"
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    tripList = tripListFinal
                } else {
                    val filteredList: MutableList<TripModel> = ArrayList()
                    for (trip in tripListFinal!!) {
                        if (trip.tripsNo.toLowerCase(Locale.ROOT).contains(charString.toLowerCase(Locale.ROOT) )
                        ) {
                            filteredList.add(trip)
                        }
                    }
                    tripList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = tripList
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                tripList = filterResults.values as ArrayList<TripModel>
                notifyDataSetChanged()
            }
        }
    }
}

open class TripViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(message: TripModel?) {}
}