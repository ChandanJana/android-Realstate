package com.realestate.adapter.spinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.realestate.R
import com.realestate.model.MaterialModel

/**
 * Created by Chandan on 30/12/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
class SpinnerMaterialAdapter(context: Context, materialList: List<MaterialModel>) :
    ArrayAdapter<MaterialModel>(context, 0, materialList) {

    private var materialList: List<MaterialModel>
    private var inflater: LayoutInflater
    internal var context: Context? = null

    init {
        this.materialList = materialList
        this.context = context
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {

        return getView(position, convertView, parent!!)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var material = getItem(position) as MaterialModel
        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.simple_text_layout, parent, false)
            vh =
                ItemHolder(
                    view
                )
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }
        vh.simple_txt.text = material.materialName

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


    override fun getItem(position: Int): MaterialModel? {
        return materialList[position];
    }

    override fun getCount(): Int {
        return materialList.size;
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