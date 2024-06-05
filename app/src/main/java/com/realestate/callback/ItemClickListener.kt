package com.realestate.callback

import android.view.View

/**
 * Created by Chandan on 27/12/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
interface ItemClickListener {
    fun onItemClick(view: View, position: Int)
}