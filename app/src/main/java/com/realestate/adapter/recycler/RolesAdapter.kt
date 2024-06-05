package com.realestate.adapter.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.realestate.R
import com.realestate.callback.ItemClickListener
import com.realestate.model.RolesModel
import kotlinx.android.synthetic.main.custom_roles_row.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Chandan on 29/8/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
private const val VIEW_TYPE_MY_TRIP = 1
private const val VIEW_TYPE_EMPTY_TRIP = 2

class RolesAdapter(
    context: Context,
    rolesList: ArrayList<RolesModel>,
    listener: ItemClickListener
) : RecyclerView.Adapter<RolesViewHolder>() {

    private var rolesList: ArrayList<RolesModel>? = null
    private var context: Context? = null
    private var listener: ItemClickListener? = null

    init {
        this.rolesList = rolesList
        this.context = context
        this.listener = listener
    }

    fun addRoles(roles: RolesModel) {
        rolesList?.add(roles)
        notifyDataSetChanged()
    }

    fun addAllRoles(roles: ArrayList<RolesModel>) {
        rolesList?.clear()
        rolesList?.addAll(roles)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (rolesList?.size!! == 0) 1 else rolesList?.size!!
    }

    override fun getItemViewType(position: Int): Int {

        return if (rolesList?.size == 0) {
            VIEW_TYPE_EMPTY_TRIP
        } else {
            VIEW_TYPE_MY_TRIP
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RolesViewHolder {
        return if (viewType == VIEW_TYPE_MY_TRIP) {
            MyRolesViewHolder(
                LayoutInflater.from(context).inflate(R.layout.custom_roles_row, parent, false)
            )
        } else {
            EmptyViewHolder(
                LayoutInflater.from(context).inflate(R.layout.empty_row, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RolesViewHolder, position: Int) {

        if (holder is MyRolesViewHolder) {
            val trip = rolesList?.get(position)
            holder.bind(trip!!)
        } else {
            holder.bind(null)
        }
    }

    inner class MyRolesViewHolder(view: View) : RolesViewHolder(view), View.OnClickListener {

        override fun bind(message: RolesModel?) {
            itemView.setOnClickListener(this)
            itemView.role_option_img.setOnClickListener(this)
            itemView.role_type.setText(message?.roleType)
            itemView.role_normal_name.setText(message?.normalizedName)
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

    inner class EmptyViewHolder(view: View) : RolesViewHolder(view) {
        private var emptyText: TextView = view.findViewById(R.id.empty_txt)
        override fun bind(message: RolesModel?) {
            emptyText.text = "No Roles available"
        }
    }
}

open class RolesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(message: RolesModel?) {}
}