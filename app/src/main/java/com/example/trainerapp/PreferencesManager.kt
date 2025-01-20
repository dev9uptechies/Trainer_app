package com.example.trainerapp

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

class PreferencesManager(context: Context) {

    var sharedPreferences: SharedPreferences
    var spEditor: Editor
    var MODE = 0

    fun setUserLogIn(isuserlogin: Boolean) {
        spEditor.putBoolean(IS_USER_LOG_IN, isuserlogin)
        spEditor.apply()
    }

    fun setdata(setdata: Boolean) {
        spEditor.putBoolean(SETDATA, setdata)
        spEditor.apply()
    }

    fun setexercisedata(setdata: Boolean) {
        spEditor.putBoolean(SETEXERCISE, setdata)
        spEditor.apply()
    }

    fun getexercisedata(): Boolean {
        return sharedPreferences.getBoolean(SETEXERCISE, false)
    }



    fun setToken(tokenId: String?) {
        spEditor.putString(TOKEN, tokenId)
        spEditor.apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(
            TOKEN,
            ""
        )
    }

    fun setUserId(userId: String?) {
        spEditor.putString(USERID, userId)
        spEditor.apply()
    }

    fun getUserId(): String? {
        return sharedPreferences.getString(USERID, "")
    }

    fun setselectAthelete(isuserlogin: Boolean) {
        spEditor.putBoolean(SELECT_ATHELETE, isuserlogin)
        spEditor.apply()
    }

    fun getselectAthelete(): Boolean {
        return sharedPreferences.getBoolean(SELECT_ATHELETE, false)
    }

    fun getSelectEvent(): Boolean {
        return sharedPreferences.getBoolean(SELECT_EVENT, false)
    }

    fun setSelectEvent(isuserlogin: Boolean) {
        spEditor.putBoolean(SELECT_EVENT, isuserlogin)
        spEditor.apply()
    }

    fun getSelectTemplate(): Boolean {
        return sharedPreferences.getBoolean(SELECT_TEMPLATE, false)
    }

    fun setSelectTemplate(isuserlogin: Boolean) {
        spEditor.putBoolean(SELECT_TEMPLATE, isuserlogin)
        spEditor.apply()
    }

    fun setselectsport(isuserlogin: Boolean) {
        spEditor.putBoolean(SELECT_SPORT, isuserlogin)
        spEditor.apply()
    }

    fun SetFlage(F_Name: String?) {
        spEditor.putString(F_name, F_Name)
        spEditor.apply()
    }

    fun GetFlage(): String? {
        return sharedPreferences.getString(
            F_name,
            ""
        )
    }

    fun setusername(username: String?) {
        spEditor.putString(USER_NAME, username)
        spEditor.apply()
    }

    fun setemail(email: String?) {
        spEditor.putString(EMAIL, email)
        spEditor.apply()
    }

    fun setpassword(password: String?) {
        spEditor.putString(PASSWORD, password)
        spEditor.apply()
    }

    fun setreffrel(reffral: String?) {
        spEditor.putString(REFFREL, reffral)
        spEditor.apply()
    }

    fun Getusername(): String? {
        return sharedPreferences.getString(
            USER_NAME,
            ""
        )
    }

    fun Getemail(): String? {
        return sharedPreferences.getString(
            EMAIL,
            ""
        )
    }

    fun GetPassword(): String? {
        return sharedPreferences.getString(
            PASSWORD,
            ""
        )
    }

    fun GetReffral(): String? {
        return sharedPreferences.getString(
            REFFREL,
            ""
        )
    }

    fun UserLogIn(): Boolean {
        return sharedPreferences.getBoolean(IS_USER_LOG_IN, false)
    }

    fun getdata(): Boolean {
        return sharedPreferences.getBoolean(SETDATA, false)
    }

    fun getselectsport(): Boolean {
        return sharedPreferences.getBoolean(SELECT_SPORT, false)
    }

    companion object {
        private const val IS_USER_LOG_IN = "firstLaunch"
        private const val SETDATA = "setdata"
        private const val SETEXERCISE = "setexercise"
        private const val SELECT_SPORT = "selectsport"
        private const val PREFERENCE = "gratify"
        private const val TOKEN = "token"
        private const val USERID = "user_id"
        private const val F_name = "Flage"
        private const val SELECT_ATHELETE = "SELECT_ATHELETE"
        private const val SELECT_EVENT = "SELECT_EVENT"
        private const val SELECT_TEMPLATE = "SELECT_TEMPLATE"
        private const val USER_NAME = "username"
        private const val EMAIL = "email"
        private const val PASSWORD = "password"
        private const val REFFREL = "reffral"

    }

    init {
        sharedPreferences = context.getSharedPreferences(PREFERENCE, MODE)
        spEditor = sharedPreferences.edit()
    }
}