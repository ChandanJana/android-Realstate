package com.realestate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.viewpager.widget.PagerAdapter


/**
 * Created by Chandan on 11/3/21
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
/*
class OnBoardViewPagerAdapter() : PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater =
            getSystemService<Any>(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val view: View = layoutInflater.inflate(layouts.get(position), container, false)
        container.addView(view)
        return view
    }

    override fun getCount(): Int {
        return layouts.length
    }

    fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any?
    ) {
        val view: View? = `object` as View?
        container.removeView(view)
    }
}*/
