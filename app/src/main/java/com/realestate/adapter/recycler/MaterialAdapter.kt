package com.realestate.adapter.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.realestate.R
import com.realestate.callback.ItemClickListener
import com.realestate.model.MaterialModel
import com.realestate.model.UserModel
import kotlinx.android.synthetic.main.custom_material_row.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Chandan on 29/8/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
private const val VIEW_TYPE_MY_TRIP = 1
private const val VIEW_TYPE_EMPTY_TRIP = 2

class MaterialAdapter(
    context: Context,
    materialList: List<MaterialModel>,
    listener: ItemClickListener
) : RecyclerView.Adapter<MaterialViewHolder>(), Filterable {

    private var materialList: List<MaterialModel>? = null
    private var materialListFinal: List<MaterialModel>? = null
    private var context: Context? = null
    private var listener: ItemClickListener? = null

    init {
        this.materialList = materialList
        this.materialListFinal = materialList
        this.context = context
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return if (materialList?.size!! == 0) 1 else materialList?.size!!
    }

    override fun getItemViewType(position: Int): Int {

        return if (materialList?.size == 0) {
            VIEW_TYPE_EMPTY_TRIP
        } else {
            VIEW_TYPE_MY_TRIP
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        return if (viewType == VIEW_TYPE_MY_TRIP) {
            MyCompaniesViewHolder(
                LayoutInflater.from(context).inflate(R.layout.custom_material_row, parent, false)
            )
        } else {
            EmptyViewHolder(
                LayoutInflater.from(context).inflate(R.layout.empty_row, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {

        if (holder is MyCompaniesViewHolder) {
            val trip = materialList?.get(position)
            holder.bind(trip!!)
        } else {
            holder.bind(null)
        }
    }

    inner class MyCompaniesViewHolder(view: View) : MaterialViewHolder(view), View.OnClickListener {

        override fun bind(message: MaterialModel?) {
            itemView.setOnClickListener(this)
            itemView.material_option_img.setOnClickListener(this)
            itemView.material_name.setText(message?.materialName)
            itemView.material_unit_price.setText(message?.price.toString())
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

    inner class EmptyViewHolder(view: View) : MaterialViewHolder(view) {
        private var emptyText: TextView = view.findViewById(R.id.empty_txt)
        override fun bind(message: MaterialModel?) {
            emptyText.text = "No materials available"
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    materialList = materialListFinal
                } else {
                    val filteredList: MutableList<MaterialModel> = ArrayList()
                    for (material in materialListFinal!!) {
                        if (material.materialName.toLowerCase(Locale.ROOT).contains(charString.toLowerCase(Locale.ROOT) )
                        ) {
                            filteredList.add(material)
                        }
                    }
                    materialList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = materialList
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                materialList = filterResults.values as ArrayList<MaterialModel>
                notifyDataSetChanged()
            }
        }
    }
}

open class MaterialViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(message: MaterialModel?) {}
}