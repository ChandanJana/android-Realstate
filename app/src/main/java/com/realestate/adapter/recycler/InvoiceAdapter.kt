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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.realestate.R
import com.realestate.callback.ItemClickListener
import com.realestate.model.InvoiceModel
import com.realestate.model.UserModel
import com.realestate.utils.Constant
import kotlinx.android.synthetic.main.custom_invoice_row.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Chandan on 29/8/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
private const val VIEW_TYPE_MY_TRIP = 1
private const val VIEW_TYPE_EMPTY_TRIP = 2

class InvoiceAdapter(
    context: Context,
    recyclerView: RecyclerView,
    invoiceList: List<InvoiceModel>,
    listener: ItemClickListener
) : RecyclerView.Adapter<InvoiceViewHolder>(), Filterable {
    private var recyclerView: RecyclerView? = null
    private var invoiceList: List<InvoiceModel>? = null
    private var invoiceListFinal: List<InvoiceModel>? = null
    private var context: Context? = null
    private var listener: ItemClickListener? = null

    init {
        this.recyclerView = recyclerView
        this.invoiceList = invoiceList
        this.invoiceListFinal = invoiceList
        this.context = context
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return if (invoiceList?.size!! == 0) 1 else invoiceList?.size!!
    }

    override fun getItemViewType(position: Int): Int {

        return if (invoiceList?.size == 0) {
            VIEW_TYPE_EMPTY_TRIP
        } else {
            VIEW_TYPE_MY_TRIP
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        return if (viewType == VIEW_TYPE_MY_TRIP) {
            MyInvoiceViewHolder(
                LayoutInflater.from(context).inflate(R.layout.custom_invoice_row, parent, false)
            )
        } else {
            EmptyViewHolder(
                LayoutInflater.from(context).inflate(R.layout.empty_row, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {

        if (holder is MyInvoiceViewHolder) {
            val trip = invoiceList?.get(position)
            holder.bind(trip!!)
        } else {
            holder.bind(null)
        }
    }

    inner class MyInvoiceViewHolder(view: View) : InvoiceViewHolder(view), View.OnClickListener {

        override fun bind(message: InvoiceModel?) {
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
            itemView.invoice_option_img.setOnClickListener(this)
            itemView.invoice_no.setText(message?.invoiceNo)
            itemView.invoice_date.setText(Constant.convertStartStringToTime(message?.invoiceDate!!, true))
            itemView.invoice_order_id.setText(message.orderId)
            itemView.invoice_trip_cost.setText(message.trips.tripCost.toString())
            itemView.invoice_company_name.setText(message.trips.company?.name)
            itemView.invoice_trip_no.setText(message.trips.tripsNo)

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

    inner class EmptyViewHolder(view: View) : InvoiceViewHolder(view) {
        private var emptyText: TextView = view.findViewById(R.id.empty_txt)
        override fun bind(message: InvoiceModel?) {
            emptyText.text = "No invoices available"
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    invoiceList = invoiceListFinal
                } else {
                    val filteredList: MutableList<InvoiceModel> = ArrayList()
                    for (invoice in invoiceListFinal!!) {
                        if (invoice.invoiceNo.toLowerCase(Locale.ROOT).contains(charString.toLowerCase(Locale.ROOT) )
                        ) {
                            filteredList.add(invoice)
                        }
                    }
                    invoiceList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = invoiceList
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                invoiceList = filterResults.values as ArrayList<InvoiceModel>
                if (recyclerView !== null){
                    var ll = recyclerView?.layoutManager
                    if (invoiceList?.isEmpty()!!)
                        ll =  LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    else
                        ll = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    recyclerView?.layoutManager = ll

                }
                notifyDataSetChanged()
            }
        }
    }
}

open class InvoiceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(message: InvoiceModel?) {}
}