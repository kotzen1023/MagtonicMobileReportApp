package com.magtonic.magtonicmobilereportapp.api

import android.content.Context
import android.util.Log
import com.magtonic.magtonicmobilereportapp.model.send.HttpUserAuthPara
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

class ApiFunc {
    private val mTAG = ApiFunc::class.java.name

    //http://61.216.114.217/IoT/TonicDashBoardCatchValue.asp?A=[3],[6],[andychin],[abcd1234]
    //http://61.216.114.217/IoT/TonicDashBoardCatchValue.asp?A=[3],[6],[andychin],[abcd1234]
    private val outsideIP = "http://61.216.114.217/IoT/TonicDashBoardCatchValue.asp?A=[3],[6]"

    fun login(para: HttpUserAuthPara, callback: Callback) {
        Log.e("ApiFunc", "login")

        val getURL = outsideIP + ",["+para.username+"],["+para.password+"]"

        val request = Request.Builder()
            .url(getURL)
            .build()

        val client = OkHttpClient().newBuilder()
            .connectTimeout(5000, TimeUnit.MILLISECONDS) //5 secs
            .readTimeout(5000, TimeUnit.MILLISECONDS) //5 secs
            .writeTimeout(5000, TimeUnit.MILLISECONDS) //5 secs
            .retryOnConnectionFailure(false)
            .build()

        try {
            val response = client.newCall(request).enqueue(callback)
            Log.d("updateMaterialSend", "response = $response")

            client.dispatcher.executorService.shutdown()
            client.connectionPool.evictAll()
            client.cache?.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        /*val queue: RequestQueue = Volley.newRequestQueue(context)
        //val url = "http://61.216.114.217/IoT/TonicDashBoardCatchValue.asp?A=[3],[6]"

        val get_url = outsideIP + ",["+para.username+"],["+para.password+"]"

        Log.e(mTAG, "get_url = $get_url")

        val stringRequest = StringRequest(
            Request.Method.GET, get_url,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                Log.e(mTAG,"Response is: $response")
            },
            Response.ErrorListener { Log.e(mTAG,"That didn't work!") })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
        */
    }//login
}