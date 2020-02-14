package com.magtonic.magtonicmobilereportapp.fragment

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
//import android.support.v4.app.Fragment
import androidx.fragment.app.Fragment
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.magtonic.magtonicmobilereportapp.R
import com.magtonic.magtonicmobilereportapp.data.Constants

class FragmentLogout : Fragment() {
    private val mTag = FragmentLogout::class.java.name

    private var logoutContext: Context? = null

    companion object {
        //private val TAG = LoginFragment::class.java.name

        private var mReceiver: BroadcastReceiver? = null
        private var isRegister = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(mTag, "onCreate")
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(mTag, "onCreateView")



        val view = inflater.inflate(R.layout.fragment_logout, container, false)

        //TextView textView = view.findViewById(R.id.textLogin);

        logoutContext = context

        val btnLogout: Button = view.findViewById(R.id.btnLogout)

        btnLogout.setOnClickListener {
            val confirmdialog = AlertDialog.Builder(logoutContext)
            confirmdialog.setIcon(R.drawable.baseline_warning_black_48)
            confirmdialog.setTitle(resources.getString(R.string.logout_title))
            confirmdialog.setMessage(resources.getString(R.string.logout_title_msg))
            confirmdialog.setPositiveButton(
                resources.getString(R.string.ok)
            ) { _, _ ->
                val loginoutIntent = Intent(Constants.ACTION.ACTION_LOGOUT_ACTION)
                logoutContext?.sendBroadcast(loginoutIntent)
            }
            confirmdialog.setNegativeButton(
                resources.getString(R.string.cancel)
            ) { _, _ -> }
            confirmdialog.show()
        }




        return view
    }

    override fun onDestroyView() {
        Log.i(mTag, "onDestroy")

        if (isRegister && mReceiver != null) {
            try {
                logoutContext!!.unregisterReceiver(mReceiver)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }

            isRegister = false
            mReceiver = null
            Log.d(mTag, "unregisterReceiver mReceiver")
        }

        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(mTag, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)

    }

    /*fun toast(message: String) {
        val toast = Toast.makeText(logoutContext, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)
        toast.show()
    }*/
}