package com.example.zakat.sharedpreferences

import android.content.Context

internal class UserPreferences (context:Context) {
    companion object {
        private const val PREFS_NAME = "user_pref"
        private const val ID = "id"
    }
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setId(value:Int){
        val editor = preferences.edit()
        editor.putInt(ID,value)
        editor.apply()
    }

    fun getId():Int{
        val result =  preferences.getInt(ID,0)
        return result!!
    }
}