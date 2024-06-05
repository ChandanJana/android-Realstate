package com.realestate.restapi

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.realestate.callback.ResponseCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

/**
 * Created by Chandan on 26/12/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
class RetrofitRequest {

    fun <T> enqueue(call: Call<T>, listener: ResponseCallback<T>) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val data = response.body()

                if (response.isSuccessful) {
                    if (data != null) {
                        println("SUCCESS")
                        listener.onSuccess(data)
                    }
                } else {
                    // error response, no access to resource?
                    listener.onFailure(response.message())
                }
            }

            override fun onFailure(call: Call<T>, error: Throwable) {
                var error1: Error? = null
                if (error is HttpException) {
                    val body = error.response()?.errorBody()
                    val gson = Gson()
                    val adapter: TypeAdapter<Error> = gson.getAdapter(Error::class.java)
                    try {
                        error1 = adapter.fromJson(body?.string())
                    } catch (e: IOException) {
                    }
                }
                if (error1 != null)
                    listener.onFailure(error1.message)
                else
                    listener.onFailure(error.message)

                println("FAIL: Call Failed")
                println(error.message)
            }
        })
    }

    fun <T> execute(call: Call<T>): T? {
        var data: T? = null

        try {
            val result = call.execute()
            println("SUCCESS")
            if (result.body() != null)
                data = result.body()!!
        } catch (e: Exception) {
            println("FAIL: Call Failed")
            println(e.message)
        }

        return data
    }
}