package com.realestate.adapter.recycler

import android.annotation.SuppressLint
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
import com.realestate.model.UserModel
import kotlinx.android.synthetic.main.custom_user_row.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Chandan on 29/8/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
private const val VIEW_TYPE_MY_TRIP = 1
private const val VIEW_TYPE_EMPTY_TRIP = 2

class UserAdapter(context: Context, recyclerView: RecyclerView, userList: List<UserModel>, listener: ItemClickListener) :
    RecyclerView.Adapter<UserViewHolder>(), Filterable {
    private var recyclerView: RecyclerView? = null
    private var userList: List<UserModel>? = null
    private var userListFinal: List<UserModel>? = null
    private var context: Context? = null
    private var listener: ItemClickListener? = null

    init {
        this.recyclerView = recyclerView
        this.userList = userList
        this.userListFinal = userList
        this.context = context
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return if (userList?.size!! == 0) 1 else userList?.size!!
    }

    override fun getItemViewType(position: Int): Int {

        return if (userList?.size == 0) {
            VIEW_TYPE_EMPTY_TRIP
        } else {
            VIEW_TYPE_MY_TRIP
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return if (viewType == VIEW_TYPE_MY_TRIP) {
            MyUserViewHolder(
                LayoutInflater.from(context).inflate(R.layout.custom_user_row, parent, false)
            )
        } else {
            EmptyViewHolder(
                LayoutInflater.from(context).inflate(R.layout.empty_row, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        if (holder is MyUserViewHolder) {
            val trip = userList?.get(position)
            holder.bind(trip)
        } else {
            holder.bind(null)
        }
    }

    inner class MyUserViewHolder(view: View) : UserViewHolder(view), View.OnClickListener {

        @SuppressLint("SetTextI18n")
        override fun bind(message: UserModel?) {

            /*val dd:GradientDrawable = itemView.user_lay.background as GradientDrawable
            dd.orientation = GradientDrawable.Orientation.TL_BR
            dd.colors = intArrayOf(Color.parseColor("#B7000000"), Color.parseColor("#B7ffffff"))*/
            val gd = GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                intArrayOf(Color.parseColor("#000000"), Color.parseColor("#ffffff"))
            )
            gd.cornerRadius = 20.0f
            itemView.setBackground(gd)

            itemView.user_full_name.text = message?.firstName + " " + message?.lastName
            //itemView.user_gender.text = message?.gender
            itemView.user_contact.text = message?.phoneNumber
            itemView.user_role.text = message?.role?.roleType
            itemView.user_aadhar_no.text = message?.aadharNumber
            itemView.setOnClickListener(this)
            itemView.user_option_img.setOnClickListener(this)
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


    inner class EmptyViewHolder(view: View) : UserViewHolder(view) {
        private var emptyText: TextView = view.findViewById(R.id.empty_txt)
        override fun bind(message: UserModel?) {
            emptyText.text = "No users available"
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    userList = userListFinal
                } else {
                    val filteredList: MutableList<UserModel> = ArrayList()
                    for (user in userListFinal!!) {
                        if (user.userName.toLowerCase(Locale.ROOT).contains(charString.toLowerCase(Locale.ROOT) )
                        ) {
                            filteredList.add(user)
                        }
                    }
                    userList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = userList
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                userList = filterResults.values as ArrayList<UserModel>

                if (recyclerView !== null){
                    var ll = recyclerView?.layoutManager
                    if (userList?.isEmpty()!!)
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

open class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(message: UserModel?) {}
}