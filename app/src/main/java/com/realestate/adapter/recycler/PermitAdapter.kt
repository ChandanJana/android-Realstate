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
import com.realestate.model.PermitModel
import com.realestate.model.UserModel
import com.realestate.utils.Constant
import kotlinx.android.synthetic.main.custom_permit_row.view.*
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

class PermitAdapter(
    context: Context,
    permitList: List<PermitModel>,
    listener: ItemClickListener
) : RecyclerView.Adapter<PermitViewHolder>(), Filterable {
    private var permitList: List<PermitModel>? = null
    private var permitListFinal: List<PermitModel>? = null
    private var context: Context? = null
    private var listener: ItemClickListener? = null

    init {
        this.permitList = permitList
        this.permitListFinal = permitList
        this.context = context
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return if (permitList?.size!! == 0) 1 else permitList?.size!!
    }

    override fun getItemViewType(position: Int): Int {

        return if (permitList?.size == 0) {
            VIEW_TYPE_EMPTY_TRIP
        } else {
            VIEW_TYPE_MY_TRIP
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PermitViewHolder {
        return if (viewType == VIEW_TYPE_MY_TRIP) {
            MyPermitViewHolder(
                LayoutInflater.from(context).inflate(R.layout.custom_permit_row, parent, false)
            )
        } else {
            EmptyViewHolder(
                LayoutInflater.from(context).inflate(R.layout.empty_row, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: PermitViewHolder, position: Int) {

        if (holder is MyPermitViewHolder) {
            val trip = permitList?.get(position)
            holder.bind(trip!!)
        } else {
            holder.bind(null)
        }
    }

    inner class MyPermitViewHolder(view: View) : PermitViewHolder(view), View.OnClickListener {

        override fun bind(message: PermitModel?) {
            if (adapterPosition % 2 == 0) {

                //val dd:GradientDrawable = itemView.user_lay.background as GradientDrawable
                //dd.orientation = GradientDrawable.Orientation.BL_TR
                //dd.colors = intArrayOf(Color.parseColor("#B7000000"), Color.parseColor("#B7ffffff"))
                val gd = GradientDrawable(
                    GradientDrawable.Orientation.BOTTOM_TOP,
                    intArrayOf(Color.parseColor("#000000"), Color.parseColor("#ffffff"))
                )
                gd.cornerRadius = 20.0f
                gd.setDither(true)
                itemView.setBackground(gd)
            } else {

                /*val dd:GradientDrawable = itemView.user_lay.background as GradientDrawable
                dd.orientation = GradientDrawable.Orientation.TL_BR
                dd.colors = intArrayOf(Color.parseColor("#B7000000"), Color.parseColor("#B7ffffff"))*/
                val gd = GradientDrawable(
                    GradientDrawable.Orientation.BOTTOM_TOP,
                    intArrayOf(Color.parseColor("#000000"), Color.parseColor("#ffffff"))
                )
                gd.cornerRadius = 20.0f
                itemView.setBackground(gd)
            }
            itemView.setOnClickListener(this)
            itemView.permit_option_img.setOnClickListener(this)
            itemView.permit_vehicles_reg_year.setText(message?.vehicle?.vehicleRegisteredNo)
            itemView.permit_start_date.setText(Constant.convertStartStringToTime(message?.startDate!!, true))
            itemView.permit_end_date.setText(Constant.convertStartStringToTime(message.endDate, true))
            itemView.permit_premium.setText(message.premium.toString())
            if (message.permitStatus.equals("Active")) {
                itemView.permit_status.setBackgroundDrawable(context?.resources?.getDrawable(R.drawable.round_button_start))
            } else  {
                itemView.permit_status.setBackgroundDrawable(context?.resources?.getDrawable(R.drawable.round_button_end))
            }
            itemView.permit_status.setText(message.permitStatus)
            itemView.permit_type.setText(message.permitType)
            itemView.permit_no.setText(message.permitNo)
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

    inner class EmptyViewHolder(view: View) : PermitViewHolder(view) {
        private var emptyText: TextView = view.findViewById(R.id.empty_txt)
        override fun bind(message: PermitModel?) {
            emptyText.text = "No permits available"
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    permitList = permitListFinal
                } else {
                    val filteredList: MutableList<PermitModel> = ArrayList()
                    for (permit in permitListFinal!!) {
                        if (permit.permitNo.toLowerCase(Locale.ROOT).contains(charString.toLowerCase(Locale.ROOT) )
                        ) {
                            filteredList.add(permit)
                        }
                    }
                    permitList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = permitList
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                permitList = filterResults.values as ArrayList<PermitModel>
                notifyDataSetChanged()
            }
        }
    }
}

open class PermitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(message: PermitModel?) {}
}