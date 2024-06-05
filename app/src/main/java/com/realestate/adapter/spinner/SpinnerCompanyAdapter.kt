package com.realestate.adapter.spinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.realestate.R
import com.realestate.callback.ItemClickListener
import com.realestate.model.CompanyModel

/**
 * Created by Chandan on 30/12/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
class SpinnerCompanyAdapter(context: Context, companyList: List<CompanyModel>) :
    ArrayAdapter<CompanyModel>(context, 0, companyList) {
    private var companyList: List<CompanyModel>
    private var inflater: LayoutInflater
    internal var context: Context? = null

    init {
        this.companyList = companyList
        this.context = context
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {

        return getView(position, convertView, parent!!)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var company = getItem(position) as CompanyModel
        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.simple_text_layout, parent, false)
            vh = ItemHolder(
                view
            )
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }
        vh.simple_txt.text = company.name
        /*if (position == 0) {
            vh.simple_txt.setTextColor(context.resources?.getColor(R.color.gray)!!)
        }*/

        applyColor(view, position)
        return view
    }

    private fun applyColor(view: View, position: Int) {

        //if (position != 0) {
            if (position % 2 == 0) {
                view.setBackgroundColor(context?.resources?.getColor(R.color.whiteTextColor)!!)
            } else {
                view.setBackgroundColor(context?.resources?.getColor(R.color.gray)!!)
            }
        //}


    }

    fun setItemClickListener(listener: ItemClickListener) {

    }

    override fun getItem(position: Int): CompanyModel? {
        return companyList[position]
    }

    override fun getCount(): Int {
        return companyList.size;
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    private class ItemHolder(row: View?) {
        val simple_txt: TextView

        init {
            simple_txt = row?.findViewById(R.id.simple_txt) as TextView
        }
    }

}