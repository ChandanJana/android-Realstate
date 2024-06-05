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
import com.realestate.model.CompanyModel
import com.realestate.model.UserModel
import kotlinx.android.synthetic.main.custom_company_row.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Chandan on 29/8/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
private const val VIEW_TYPE_MY_TRIP = 1
private const val VIEW_TYPE_EMPTY_TRIP = 2

class CompaniesAdapter(
    context: Context,
    recyclerView: RecyclerView,
    companyList: List<CompanyModel>,
    listener: ItemClickListener
) : RecyclerView.Adapter<CompaniesViewHolder>(), Filterable {
    private var recyclerView: RecyclerView? = null
    private var companyList: List<CompanyModel>? = null
    private var companyListFinal: List<CompanyModel>? = null
    private var context: Context? = null
    private var listener: ItemClickListener? = null

    init {
        this.recyclerView = recyclerView
        this.companyList = companyList
        this.companyListFinal = companyList
        this.context = context
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return if (companyList?.size!! == 0) 1 else companyList?.size!!
    }

    override fun getItemViewType(position: Int): Int {

        return if (companyList?.size == 0) {
            VIEW_TYPE_EMPTY_TRIP
        } else {
            VIEW_TYPE_MY_TRIP
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompaniesViewHolder {
        return if (viewType == VIEW_TYPE_MY_TRIP) {
            MyCompaniesViewHolder(
                LayoutInflater.from(context).inflate(R.layout.custom_company_row, parent, false)
            )
        } else {
            EmptyViewHolder(
                LayoutInflater.from(context).inflate(R.layout.empty_row, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: CompaniesViewHolder, position: Int) {

        if (holder is MyCompaniesViewHolder) {
            val trip = companyList?.get(position)
            holder.bind(trip!!)
        } else {
            holder.bind(null)
        }
    }

    inner class MyCompaniesViewHolder(view: View) : CompaniesViewHolder(view),
        View.OnClickListener {

        override fun bind(message: CompanyModel?) {
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
            itemView.company_option_img.setOnClickListener(this)
            itemView.company_name.setText(message?.name)
            itemView.company_address.setText(message?.address)
            itemView.company_material_name.setText(message?.material?.materialName)
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

    inner class EmptyViewHolder(view: View) : CompaniesViewHolder(view) {
        private var emptyText: TextView = view.findViewById(R.id.empty_txt)
        override fun bind(message: CompanyModel?) {
            emptyText.text = "No companies available"
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    companyList = companyListFinal
                } else {
                    val filteredList: MutableList<CompanyModel> = ArrayList()
                    for (company in companyListFinal!!) {
                        if (company.name.toLowerCase(Locale.ROOT).contains(charString.toLowerCase(Locale.ROOT) )
                        ) {
                            filteredList.add(company)
                        }
                    }
                    companyList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = companyList
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                companyList = filterResults.values as ArrayList<CompanyModel>
                if (recyclerView !== null){
                    var ll = recyclerView?.layoutManager
                    if (companyList?.isEmpty()!!)
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

open class CompaniesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(message: CompanyModel?) {}
}