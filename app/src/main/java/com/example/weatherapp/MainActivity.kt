package com.example.weatherapp

import android.app.DownloadManager.Request
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.graphics.red
import com.android.volley.Request.Method
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.fragments.MainFragment
import org.json.JSONObject

//const val API_KEY = "a5060863b1954de3877222723230601"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //вставляємо фрагмент в основну разметку, commit використовуємо щоб застосувати дії
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.placeHolder, MainFragment.newInstance())
            .commit()
        }
    }


/*private fun getResult(name: String) {
    val url = "https://api.weatherapi.com/v1/current.json" +
            "?key=$API_KEY&q=$name&aqi=no"

    val queue = Volley.newRequestQueue(this)
    val stringRequest = StringRequest(Method.GET,
        url,
        {
                response->
            val obj = JSONObject(response)
            val temp = obj.getJSONObject("current")
            Log.d("MyLog", "Response: ${temp.getString("temp_c")}")
        },
        {
            Log.d("MyLog", "Volley error: ${it.toString()}")
        }

    )
    queue.add(stringRequest)
} */