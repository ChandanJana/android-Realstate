package com.realestate.callback

import com.realestate.restapi.Payload

/**
 * Created by Chandan on 26/12/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
interface AddUpdateListener {
    fun onAddOrUpdate(ok: Boolean, model: Payload?)
    fun onCancel(cancel: Boolean)
}