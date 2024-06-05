package com.realestate.restapi

import android.os.Bundle
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Just a wrapper around JSONObject.
 *
 *
 * Kept around to avoid code rewrite
 * and the convenience of chaining and Exception handling while adding data
 *
 *
 * Created by Chandan on 26/12/20.
 */
class Payload : JSONObject {
    constructor() : super() {}
    constructor(json: String?) : super(json) {}

    fun add(key: String?, value: Any?): Payload {
        try {
            super.put(key, value)
        } catch (ignore: JSONException) {
        }
        return this
    }

    fun addAll(params: HashMap<String?, String?>): Payload {
        for ((key, value) in params) {
            add(key, value)
        }
        return this
    }

    fun addAll(params: Bundle): Payload {
        for (key in params.keySet()) {
            add(key, params[key].toString()) //To Implement
        }
        //        for (Map.Entry<String, String> keyValPair : params.entrySet()) {
//            add(keyValPair.getKey(), keyValPair.getValue());
//        }
        return this
    }

    /**
     * add all keys of JsonObject to payload
     *
     * @param jsonObject
     * @return
     */
    fun addAll(jsonObject: JSONObject?): Payload {
        if (jsonObject != null) {
            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                try {
                    add(key, jsonObject[key])
                } catch (e: Exception) {
//                    Log.e(TAG, "JSONException", e);
                }
            }
        }
        return this
    }

    fun toBundle(): Bundle {
        val map = Bundle()
        val keysItr = keys()
        while (keysItr.hasNext()) {
            val key = keysItr.next()
            try {
                val value = getString(key)
                map.putString(key, value)
            } catch (e: JSONException) {
//                Log.e(TAG, "JSONException", e);
            }
        }
        return map
    }

    fun toMap(): HashMap<String, String> {
        val map =
            HashMap<String, String>()
        val keysItr = keys()
        while (keysItr.hasNext()) {
            val key = keysItr.next()
            try {
                val value = getString(key)
                map[key] = value
            } catch (e: JSONException) {
//                Log.e(TAG, "JSONException", e);
            }
        }
        return map
    }

    companion object {
        private val TAG = Payload::class.java.simpleName
        fun getJsonString(payload: Payload?): String {
            return if (payload == null || payload.length() == 0) "" else payload.toString()
        }
    }
}