package com.magtonic.magtonicmobilereportapp.fragment


import android.content.BroadcastReceiver
import android.content.Context

import android.os.Bundle
//import android.support.v4.app.Fragment
import androidx.fragment.app.Fragment
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleObserver

import com.magtonic.magtonicmobilereportapp.R


class FragmentSetting : Fragment(), LifecycleObserver {
    private val mTag = FragmentSetting::class.java.name

    private var settingContext: Context? = null

    companion object {
        //private val TAG = LoginFragment::class.java.name

        private var mReceiver: BroadcastReceiver? = null
        private var isRegister = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(mTag, "onCreate")

        super.onCreate(savedInstanceState)

        settingContext = context

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(mTag, "onCreateView")



        //val view = inflater.inflate(R.layout.fragment_setting, container, false)






        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onDestroyView() {
        Log.i(mTag, "onDestroy")

        if (isRegister && mReceiver != null) {
            try {
                settingContext!!.unregisterReceiver(mReceiver)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }

            isRegister = false
            mReceiver = null
            Log.d(mTag, "unregisterReceiver mReceiver")
        }

        super.onDestroyView()
    }

    /*override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(mTag, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)

    }*/

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.lifecycle?.addObserver(this)
    }

    /*fun toast(message: String) {
        val toast = Toast.makeText(settingContext, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)
        toast.show()
    }*/
}