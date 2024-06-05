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
import com.realestate.model.BranchModel
import kotlinx.android.synthetic.main.custom_branch_row.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Chandan on 29/8/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
private const val VIEW_TYPE_MY_TRIP = 1
private const val VIEW_TYPE_EMPTY_TRIP = 2

class BranchAdapter(
    context: Context,
    recyclerView: RecyclerView,
    branchList: List<BranchModel>,
    listener: ItemClickListener
) : RecyclerView.Adapter<BranchViewHolder>(), Filterable {
    private var recyclerView: RecyclerView? = null
    private var branchList: List<BranchModel>? = null
    private var branchListFinal: List<BranchModel>? = null
    private var context: Context? = null
    private var listener: ItemClickListener? = null

    init {
        this.recyclerView = recyclerView
        this.branchList = branchList
        this.branchListFinal = branchList
        this.context = context
        this.listener = listener
    }


    override fun getItemCount(): Int {
        return if (branchList?.size!! == 0) 1 else branchList?.size!!
    }

    override fun getItemViewType(position: Int): Int {

        return if (branchList?.size == 0) {
            VIEW_TYPE_EMPTY_TRIP
        } else {
            VIEW_TYPE_MY_TRIP
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BranchViewHolder {
        return if (viewType == VIEW_TYPE_MY_TRIP) {
            MyBranchViewHolder(
                LayoutInflater.from(context).inflate(R.layout.custom_branch_row, parent, false)
            )
        } else {
            EmptyViewHolder(
                LayoutInflater.from(context).inflate(R.layout.empty_row, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: BranchViewHolder, position: Int) {

        if (holder is MyBranchViewHolder) {
            val trip = branchList?.get(position)
            holder.bind(trip!!)
        } else {
            holder.bind(null)
        }
    }

    inner class MyBranchViewHolder(view: View) : BranchViewHolder(view), View.OnClickListener {

        override fun bind(message: BranchModel?) {
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
            itemView.branch_name.text = message?.branchName
            itemView.branch_email.text = message?.emailId
            itemView.branch_address.text = message?.branchAddress
            itemView.branch_role.text = message?.user?.role?.roleType
            itemView.branch_contact.text = message?.phoneNumber
            itemView.branch_user_name.text = message?.user?.userName
            itemView.setOnClickListener(this)
            itemView.branch_option_img.setOnClickListener(this)
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

    inner class EmptyViewHolder(view: View) : BranchViewHolder(view) {
        private var emptyText: TextView = view.findViewById(R.id.empty_txt)
        override fun bind(message: BranchModel?) {
            emptyText.text = "No branches available"
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    branchList = branchListFinal
                } else {
                    val filteredList: MutableList<BranchModel> = ArrayList()
                    for (branch in branchListFinal!!) {
                        if (branch.branchName?.toLowerCase(Locale.ROOT)?.contains(charString.toLowerCase(Locale.ROOT) )!!
                        ) {
                            filteredList.add(branch)
                        }
                    }
                    branchList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = branchList
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                branchList = filterResults.values as ArrayList<BranchModel>
                if (recyclerView !== null){
                    var ll = recyclerView?.layoutManager
                    if (branchList?.isEmpty()!!)
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

open class BranchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(message: BranchModel?) {}
}