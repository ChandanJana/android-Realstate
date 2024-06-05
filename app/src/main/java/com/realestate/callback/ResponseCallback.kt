package com.realestate.callback

/**
 * Created by Chandan on 26/12/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
interface ResponseCallback<T> {
    fun onSuccess(response: T)
    fun onFailure(error: String?)
}