package com.realestate.callback

/**
 * Created by Chandan on 26/12/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
interface DialogCallback {
    fun onOk(ok: Boolean)
    fun onCancel(cancel: Boolean)
}