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
import com.realestate.model.InsuranceModel
import com.realestate.model.UserModel
import com.realestate.utils.Constant
import kotlinx.android.synthetic.main.custom_insurance_row.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Chandan on 29/8/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
private const val VIEW_TYPE_MY_TRIP = 1
private const val VIEW_TYPE_EMPTY_TRIP = 2

class InsuranceAdapter(
    context: Context,
    insuranceList: List<InsuranceModel>,
    listener: ItemClickListener
) : RecyclerView.Adapter<InsuranceViewHolder>(), Filterable {
    private var insuranceList: List<InsuranceModel>? = null
    private var insuranceListFinal: List<InsuranceModel>? = null
    private var context: Context? = null
    private var listener: ItemClickListener? = null

    init {
        this.insuranceList = insuranceList
        this.insuranceListFinal = insuranceList
        this.context = context
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return if (insuranceList?.size!! == 0) 1 else insuranceList?.size!!
    }

    override fun getItemViewType(position: Int): Int {

        return if (insuranceList?.size == 0) {
            VIEW_TYPE_EMPTY_TRIP
        } else {
            VIEW_TYPE_MY_TRIP
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsuranceViewHolder {
        return if (viewType == VIEW_TYPE_MY_TRIP) {
            MyInsuranceViewHolder(
                LayoutInflater.from(context).inflate(R.layout.custom_insurance_row, parent, false)
            )
        } else {
            EmptyViewHolder(
                LayoutInflater.from(context).inflate(R.layout.empty_row, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: InsuranceViewHolder, position: Int) {

        if (holder is MyInsuranceViewHolder) {
            val trip = insuranceList?.get(position)
            holder.bind(trip!!)
        } else {
            holder.bind(null)
        }
    }

    inner class MyInsuranceViewHolder(view: View) : InsuranceViewHolder(view),
        View.OnClickListener {

        override fun bind(message: InsuranceModel?) {
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
            itemView.insurance_option_img.setOnClickListener(this)
            itemView.insurance_no.setText(message?.insuranceNo)
            itemView.insurance_start_date.setText(Constant.convertStartStringToTime(message?.startDate!!, true))
            itemView.insurance_end_date.setText(Constant.convertStartStringToTime(message.endDate!!, true))
            itemView.insurance_premium.setText(message.premium.toString())
            if (message.insuranceStatus.equals("Active")) {
                itemView.insurance_status.setBackgroundDrawable(context?.resources?.getDrawable(R.drawable.round_button_start))
            } else  {
                itemView.insurance_status.setBackgroundDrawable(context?.resources?.getDrawable(R.drawable.round_button_end))
            }

            itemView.insurance_status.setText(message.insuranceStatus)
            itemView.insurance_vehicles_reg_no.setText(message.vehicle?.vehicleRegisteredNo)
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

    inner class EmptyViewHolder(view: View) : InsuranceViewHolder(view) {
        private var emptyText: TextView = view.findViewById(R.id.empty_txt)
        override fun bind(message: InsuranceModel?) {
            emptyText.text = "No insurances available"
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    insuranceList = insuranceListFinal
                } else {
                    val filteredList: MutableList<InsuranceModel> = ArrayList()
                    for (insurance in insuranceListFinal!!) {
                        if (insurance.insuranceNo?.toLowerCase(Locale.ROOT)?.contains(charString.toLowerCase(Locale.ROOT) )!!
                        ) {
                            filteredList.add(insurance)
                        }
                    }
                    insuranceList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = insuranceList
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                insuranceList = filterResults.values as ArrayList<InsuranceModel>
                notifyDataSetChanged()
            }
        }
    }
}

open class InsuranceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(message: InsuranceModel?) {}
}