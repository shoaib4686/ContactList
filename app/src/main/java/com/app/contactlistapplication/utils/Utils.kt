package com.app.contactlistapplication.utils

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.contactlistapplication.model.Content
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class Utils {

    companion object {

        const val BASE_URL: String = "https://iie-service-dev.workingllama.com/"
        var SCROLL_DELAY_DURATION: Long = 5000
        var TOAST_TIME: Int = 1000

        fun showToast(context: Context, msg: Int) {
            Toast.makeText(context, msg, TOAST_TIME).show()
        }

        fun showToast(context: Context, msg: String) {
            Toast.makeText(context, msg, TOAST_TIME).show()
        }

        fun LogsTrack(status:String){
            Log.d("ContactList",status)
        }

        fun isInternetAvailable(context: Context): Boolean {
            val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val capabilities =
                        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                        return true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                        return true
                    }
                }
            }
            return false
        }

        fun saveData(contactList: List<Content>, context: Context) {
            val sharedPreferences: SharedPreferences =
                    context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val gson = Gson()
            val json = gson.toJson(contactList)
            editor.putString("contact", json)
            editor.apply()
        }

        fun retrieveOfflineList(context: Context): ArrayList<Content> {
            val contactArrayList: java.util.ArrayList<Content>
            val sharedPreferences: SharedPreferences = context.getSharedPreferences("shared preferences",
                    AppCompatActivity.MODE_PRIVATE
            )
            val gson = Gson()
            val json = sharedPreferences.getString("contact", null)
            val type: Type = object : TypeToken<ArrayList<Content?>?>() {}.type
            contactArrayList = gson.fromJson<Any>(json, type) as java.util.ArrayList<Content>
            if (!contactArrayList.isEmpty()) {
                return contactArrayList
            }
            return contactArrayList
        }

    }

}