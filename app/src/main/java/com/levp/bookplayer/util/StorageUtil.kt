package com.levp.bookplayer.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class StorageUtil(context: Context) {
    private val STORAGE = "com.levp.bookplayer.STORAGE"
    private var preferences: SharedPreferences? = null
    private val context: Context

    fun storeAudio(arrayList: ArrayList<TrackSupport.Track>) {
        Log.e("stored _list : ",arrayList.size.toString())
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        val gson = Gson()
        val json = gson.toJson(arrayList)
        editor.putString("audioArrayList", json)
        editor.apply()
    }

    fun loadAudio(): ArrayList<TrackSupport.Track> {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        //Log.e("Very vazhno", preferences.toString())
        val gson = Gson()
        val json = preferences!!.getString("audioArrayList", null)
        val type = object : TypeToken<ArrayList<TrackSupport.Track>>() {}.type
        return gson.fromJson(json, type)
    }

    fun storeAudioIndex(index: Int) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        //Log.e("srored index : ", index.toString())
        editor.putInt("audioIndex", index)
        editor.apply()
    }

    fun loadAudioIndex(): Int {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        return preferences!!.getInt("audioIndex", -1) //return -1 if no data found
    }

    fun clearCachedAudioPlaylist() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.clear()
        editor.apply()
    }

    init {
        this.context = context
    }
}