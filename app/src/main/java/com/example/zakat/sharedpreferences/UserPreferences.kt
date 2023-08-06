package com.example.zakat.sharedpreferences

import android.content.Context

internal class UserPreferences (context:Context) {
    companion object {
        private const val PREFS_NAME = "user_pref"
        private const val ID = "id"
        private const val NAME ="nama"
    }
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val editor = preferences.edit()

    fun setId(value:Int){
        editor.putInt(ID,value)
        editor.apply()
    }

    fun getId():Int{
        val result =  preferences.getInt(ID,0)
        return if(result == 0){
            0
        }else {
            result
        }

    }

    fun setNama(value:String){
        editor.putString(NAME,value)
        editor.apply()
    }

    fun getName():String{
        val result =  preferences.getString(NAME,"")
        return result!!
    }
}