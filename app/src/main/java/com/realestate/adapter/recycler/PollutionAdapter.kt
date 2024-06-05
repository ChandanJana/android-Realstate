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
import com.realestate.model.PollutionModel
import com.realestate.utils.Constant
import kotlinx.android.synthetic.main.custom_pollution_row.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Chandan on 29/8/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
private const val VIEW_TYPE_MY_TRIP = 1
private const val VIEW_TYPE_EMPTY_TRIP = 2

class PollutionAdapter(
    context: Context,
    pollutionList: List<PollutionModel>,
    listener: ItemClickListener
) : RecyclerView.Adapter<PollutionViewHolder>(), Filterable {
    private var pollutionList: List<PollutionModel>? = null
    private var pollutionListFinal: List<PollutionModel>? = null
    private var context: Context? = null
    private var listener: ItemClickListener? = null

    init {
        this.pollutionList = pollutionList
        this.pollutionListFinal = pollutionList
        this.context = context
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return if (pollutionList?.size!! == 0) 1 else pollutionList?.size!!
    }

    override fun getItemViewType(position: Int): Int {

        return if (pollutionList?.size == 0) {
            VIEW_TYPE_EMPTY_TRIP
        } else {
            VIEW_TYPE_MY_TRIP
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PollutionViewHolder {
        return if (viewType == VIEW_TYPE_MY_TRIP) {
            MyPollutionViewHolder(
                LayoutInflater.from(context).inflate(R.layout.custom_pollution_row, parent, false)
            )
        } else {
            EmptyViewHolder(
                LayoutInflater.from(context).inflate(R.layout.empty_row, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: PollutionViewHolder, position: Int) {

        if (holder is MyPollutionViewHolder) {
            val trip = pollutionList?.get(position)
            holder.bind(trip!!)
        } else {
            holder.bind(null)
        }
    }

    inner class MyPollutionViewHolder(view: View) : PollutionViewHolder(view),
        View.OnClickListener {

        override fun bind(message: PollutionModel?) {
            val gd = GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                intArrayOf(Color.parseColor("#000000"), Color.parseColor("#ffffff"))
            )
            gd.cornerRadius = 20.0f
            gd.setDither(true)
            itemView.setBackground(gd)
            itemView.setOnClickListener(this)
            itemView.pollution_option_img.setOnClickListener(this)
            itemView.pollution_no.setText(message?.pollutionNo)
            itemView.pollution_start_date.setText(
                Constant.convertStartStringToTime(
                    message?.startDate!!,
                    true
                )
            )
            itemView.pollution_end_date.setText(
                Constant.convertStartStringToTime(
                    message.endDate,
                    true
                )
            )
            itemView.pollution_price.setText(message?.price.toString())

            if (message?.pollutionStatus.equals("Expired")) {
                itemView.pollution_status.setBackgroundDrawable(context?.resources?.getDrawable(R.drawable.round_button_end))
            } else {
                itemView.pollution_status.setBackgroundDrawable(context?.resources?.getDrawable(R.drawable.round_button_start))
            }
            itemView.pollution_status.setText(message?.pollutionStatus)
            itemView.pollution_vehicles_reg_year.setText(message?.vehicle?.vehicleRegisteredNo)
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

    inner class EmptyViewHolder(view: View) : PollutionViewHolder(view) {
        private var emptyText: TextView = view.findViewById(R.id.empty_txt)
        override fun bind(message: PollutionModel?) {
            emptyText.text = "No pollutions available"
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    pollutionList = pollutionListFinal
                } else {
                    val filteredList: MutableList<PollutionModel> = ArrayList()
                    for (pollution in pollutionListFinal!!) {
                        if (pollution.pollutionNo.toLowerCase(Locale.ROOT)
                                .contains(charString.toLowerCase(Locale.ROOT))
                        ) {
                            filteredList.add(pollution)
                        }
                    }
                    pollutionList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = pollutionList
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                pollutionList = filterResults.values as ArrayList<PollutionModel>
                notifyDataSetChanged()
            }
        }
    }
}

open class PollutionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(message: PollutionModel?) {}
}