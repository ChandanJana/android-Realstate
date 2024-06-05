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
import com.realestate.model.DealerModel
import com.realestate.model.UserModel
import kotlinx.android.synthetic.main.custom_dealer_row.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Chandan on 29/8/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
private const val VIEW_TYPE_MY_TRIP = 1
private const val VIEW_TYPE_EMPTY_TRIP = 2

class DealerAdapter(
    context: Context,
    recyclerView: RecyclerView,
    dealerList: List<DealerModel>,
    listener: ItemClickListener
) : RecyclerView.Adapter<DealerViewHolder>(), Filterable {
    private var recyclerView: RecyclerView? = null
    private var dealerList: List<DealerModel>? = null
    private var dealerListFinal: List<DealerModel>? = null
    private var context: Context? = null
    private var listener: ItemClickListener? = null

    init {
        this.dealerList = dealerList
        this.dealerListFinal = dealerList
        this.context = context
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return if (dealerList?.size!! == 0) 1 else dealerList?.size!!
    }

    override fun getItemViewType(position: Int): Int {

        return if (dealerList?.size == 0) {
            VIEW_TYPE_EMPTY_TRIP
        } else {
            VIEW_TYPE_MY_TRIP
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealerViewHolder {
        return if (viewType == VIEW_TYPE_MY_TRIP) {
            MyDealerViewHolder(
                LayoutInflater.from(context).inflate(R.layout.custom_dealer_row, parent, false)
            )
        } else {
            EmptyViewHolder(
                LayoutInflater.from(context).inflate(R.layout.empty_row, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: DealerViewHolder, position: Int) {

        if (holder is MyDealerViewHolder) {
            val trip = dealerList?.get(position)
            holder.bind(trip!!)
        } else {
            holder.bind(null)
        }
    }

    inner class MyDealerViewHolder(view: View) : DealerViewHolder(view), View.OnClickListener {

        override fun bind(message: DealerModel?) {
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
            itemView.dealer_option_img.setOnClickListener(this)
            itemView.dealer_no.setText(message?.dealerNo)
            itemView.dealer_name.setText(message?.dealerName)
            itemView.dealer_contact.setText(message?.phoneNumber)
            itemView.dealer_address.setText(message?.address)
            itemView.dealer_state.setText(message?.state)
            itemView.dealer_pincode.setText(message?.pincode.toString())
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

    inner class EmptyViewHolder(view: View) : DealerViewHolder(view) {
        private var emptyText: TextView = view.findViewById(R.id.empty_txt)
        override fun bind(message: DealerModel?) {
            emptyText.text = "No dealers available"
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    dealerList = dealerListFinal
                } else {
                    val filteredList: MutableList<DealerModel> = ArrayList()
                    for (dealer in dealerListFinal!!) {
                        if (dealer.dealerName.toLowerCase(Locale.ROOT).contains(charString.toLowerCase(Locale.ROOT) )
                        ) {
                            filteredList.add(dealer)
                        }
                    }
                    dealerList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = dealerList
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                dealerList = filterResults.values as ArrayList<DealerModel>
                if (recyclerView !== null){
                    var ll = recyclerView?.layoutManager
                    if (dealerList?.isEmpty()!!)
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

open class DealerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(message: DealerModel?) {}
}