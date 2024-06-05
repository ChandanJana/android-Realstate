package com.realestate.storage

import android.content.Context
import android.content.SharedPreferences


/**
 * Created by Chandan on 26/12/20
 * Company: Endue Technologies Pvt. LTD
 * Email: chandanjana@enduetechnologies.com
 */
class SharedPreferenceManager {

    private val FILE_NAME = "realestate"
    private var mBulkUpdate = false

    companion object {
        var sSharedPrefs: SharedPreferenceManager? = null

        internal fun getInstance(context: Context): SharedPreferenceManager? {
            if (sSharedPrefs == null) {
                sSharedPrefs = SharedPreferenceManager(context)
            }
            return sSharedPrefs
        }
    }

    private var mPref: SharedPreferences? = null
    private var mEditor: SharedPreferences.Editor? = null

    /**
     * Enum representing your setting names or key for your setting.
     */
    enum class Key {
        /* Recommended naming convention:
         * ints, floats, doubles, longs:
         * SAMPLE_NUM or SAMPLE_COUNT or SAMPLE_INT, SAMPLE_LONG etc.
         *
         * boolean: IS_SAMPLE, HAS_SAMPLE, CONTAINS_SAMPLE
         *
         * String: SAMPLE_KEY, SAMPLE_STR or just SAMPLE
         */
        IS_LOGGEDIN, AUTH_TOKEN, USER_ID, USER_NAME, USER_EMAIL,
    }

    constructor(context: Context) {
        mPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    fun putString(key: Key, value: String?) {
        doEdit()
        mEditor?.putString(key.name, value)
        doCommit()
    }

    fun putInt(key: Key, value: Int) {
        doEdit()
        mEditor?.putInt(key.name, value)
        doCommit()
    }

    fun putBoolean(key: Key, value: Boolean) {
        doEdit()
        mEditor?.putBoolean(key.name, value)
        doCommit()
    }

    fun putFloat(key: Key, value: Float) {
        doEdit()
        mEditor?.putFloat(key.name, value)
        doCommit()
    }

    fun putDouble(key: Key, value: Double) {
        doEdit()
        mEditor?.putString(key.name, value.toString())
        doCommit()
    }

    fun putLong(key: Key, value: Long) {
        doEdit()
        mEditor?.putLong(key.name, value)
        doCommit()
    }

    fun getString(key: Key, defaultValue: String?): String? {
        return mPref?.getString(key.name, defaultValue)
    }

    fun getString(key: Key): String? {
        return mPref?.getString(key.name, null)
    }

    fun getInt(key: Key): Int {
        return mPref?.getInt(key.name, 0)!!
    }

    fun getInt(key: Key, defaultValue: Int): Int {
        return mPref?.getInt(key.name, defaultValue)!!
    }

    fun getLong(key: Key): Long {
        return mPref?.getLong(key.name, 0)!!
    }

    fun getLong(key: Key, defaultValue: Long): Long {
        return mPref?.getLong(key.name, defaultValue)!!
    }

    fun getFloat(key: Key): Float {
        return mPref?.getFloat(key.name, 0f)!!
    }

    fun getFloat(key: Key, defaultValue: Float): Float {
        return mPref?.getFloat(key.name, defaultValue)!!
    }

    /**
     * Convenience method for retrieving doubles.
     *
     *
     * There may be instances where the accuracy of a double is desired.
     * SharedPreferences does not handle doubles so they have to
     * cast to and from Key.
     *
     * @param key The enum of the preference to fetch.
     */
    fun getDouble(key: Key): Double {
        return getDouble(key, 0.0)
    }

    /**
     * Convenience method for retrieving doubles.
     *
     *
     * There may be instances where the accuracy of a double is desired.
     * SharedPreferences does not handle doubles so they have to
     * cast to and from Key.
     *
     * @param key The enum of the preference to fetch.
     */
    fun getDouble(key: Key, defaultValue: Double): Double {
        return try {
            java.lang.Double.valueOf(mPref?.getString(key.name, defaultValue.toString()))
        } catch (nfe: NumberFormatException) {
            defaultValue
        }
    }

    fun getBoolean(key: Key, defaultValue: Boolean): Boolean {
        return mPref?.getBoolean(key.name, defaultValue)!!
    }

    fun getBoolean(key: Key): Boolean {
        return mPref?.getBoolean(key.name, false)!!
    }

    /**
     * Remove keys from SharedPreferences.
     *
     * @param keys The enum of the key(s) to be removed.
     */
    fun remove(vararg keys: Key) {
        doEdit()
        for (key in keys) {
            mEditor?.remove(key.name)
        }
        doCommit()
    }

    /**
     * Remove all keys from SharedPreferences.
     */
    fun clear() {
        doEdit()
        mEditor?.clear()
        doCommit()
    }

    fun edit() {
        mBulkUpdate = true
        mEditor = mPref?.edit()
    }

    fun commit() {
        mBulkUpdate = false
        mEditor?.commit()
        mEditor = null
    }

    private fun doEdit() {
        if (!mBulkUpdate && mEditor == null) {
            mEditor = mPref?.edit()
        }
    }

    private fun doCommit() {
        if (!mBulkUpdate && mEditor != null) {
            mEditor?.commit()
            mEditor = null
        }
    }

}