package com.blank.firestorefirebase.data.db

import android.content.SharedPreferences
import com.blank.firestorefirebase.AppLoader

/**
 * Created by knalb on 11/07/18.
 */

object SharedPref {
    private val TAG = "SharedPref"
    private var PRIVATE_MODE = 0
    private val PREF_NAME = "framework-blank"
    private val LOGIN = "login"
    private val TOKEN_NOTIF = "tokenNotif"

    private val pref: SharedPreferences
        get() {
            val context = AppLoader.appContext
            return context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        }

    fun setTokenNotif(value: String?) {
        pref.edit()
            .putString(TOKEN_NOTIF, value!!)
            .apply()
    }

    fun getTokenNotif(): String = pref.getString(TOKEN_NOTIF, "").toString()

    fun setLogin(value: Boolean?) {
        pref.edit()
            .putBoolean(LOGIN, value!!)
            .apply()
    }

    fun getLogin(): Boolean = pref.getBoolean(LOGIN, false)

    fun deleteLogin() {
        pref.edit()
            .remove(LOGIN)
            .apply()
    }
}
