package com.magtonic.magtonicmobilereportapp.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.os.Bundle
//import android.support.v4.app.Fragment
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.*
import android.widget.*
import com.magtonic.magtonicmobilereportapp.MainActivity.Companion.isKeyBoardShow
import com.magtonic.magtonicmobilereportapp.MainActivity.Companion.screenHeight
import com.magtonic.magtonicmobilereportapp.MainActivity.Companion.screenWidth
import com.magtonic.magtonicmobilereportapp.R
import com.magtonic.magtonicmobilereportapp.data.Constants

class FragmentLogin: Fragment() {
    private val mTag = FragmentLogin::class.java.name
    private var loginContext: Context? = null

    private var mReceiver: BroadcastReceiver? = null
    private var isRegister = false

    private var editTextAccount: EditText? = null
    private var editTextPassword: EditText? = null
    private var btnLogin: Button? = null

    private var progressBar: ProgressBar? = null
    private var relativeLayout: RelativeLayout? = null
    private var linearLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(mTag, "onCreate")

        loginContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(mTag, "onCreateView")

        val view = inflater.inflate(R.layout.fragment_login, container, false)

        relativeLayout = view.findViewById(R.id.login_container)
        progressBar = ProgressBar(loginContext, null, android.R.attr.progressBarStyleLarge)
        val params = RelativeLayout.LayoutParams(screenHeight / 4, screenWidth / 4)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)
        relativeLayout!!.addView(progressBar, params)
        progressBar!!.visibility = View.GONE

        //detect soft keyboard
        linearLayout = view.findViewById(R.id.linearLayoutLogin)
        linearLayout!!.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            linearLayout!!.getWindowVisibleDisplayFrame(r)
            //val screenHeight = linearLayout!!.getRootView().getHeight()
            val screenHeight = linearLayout!!.rootView.height
            val keypadHeight = screenHeight - r.bottom
            isKeyBoardShow = (keypadHeight > screenHeight * 0.15)
        }
        /*linearLayout!!.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val r = Rect()
                    linearLayout!!.getWindowVisibleDisplayFrame(r)
                    //val screenHeight = linearLayout!!.getRootView().getHeight()
                    val screenHeight = linearLayout!!.rootView.height
                    val keypadHeight = screenHeight - r.bottom
                    isKeyBoardShow = (keypadHeight > screenHeight * 0.15)

                }
            }
        )*/

        val textViewTitle : TextView = view.findViewById(R.id.first_load_textView_title2)

        editTextAccount = view.findViewById(R.id.editTextAccount)
        editTextPassword = view.findViewById(R.id.editTextPassword)
        btnLogin = view.findViewById(R.id.btnLogin)


        val title1 = getString(R.string.first_company_name)
        val title2 = getString(R.string.loadingTitle2)
        val logintitle = title1 + title2
        textViewTitle.text = logintitle



        btnLogin!!.setOnClickListener {
            progressBar!!.visibility = View.VISIBLE
            btnLogin!!.visibility= View.INVISIBLE

            val account: EditText? = editTextAccount
            val password: EditText? = editTextPassword
            if (account != null && password != null) {
                if (account.text.isEmpty()) {
                    progressBar!!.visibility = View.GONE
                    btnLogin!!.visibility= View.VISIBLE
                    if (isAdded) {
                        toast(resources.getString(R.string.login_account_empty))
                    }
                } else if (password.text.isEmpty()) {
                    progressBar!!.visibility = View.GONE
                    btnLogin!!.visibility= View.VISIBLE
                    if (isAdded) {
                        toast(resources.getString(R.string.login_password_empty))
                    }
                } else {
                    Log.d(mTag, "no other ")
                    val loginIntent = Intent()
                    loginIntent.action = Constants.ACTION.ACTION_LOGIN_ACTION
                    loginIntent.putExtra("account", account.text.toString())
                    loginIntent.putExtra("password", password.text.toString())
                    //loginIntent.putExtra("imei", imei)
                    loginContext!!.sendBroadcast(loginIntent)



                }
            }
        }



        val filter: IntentFilter

        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                if (intent.action != null) {
                    when (intent.action) {
                        Constants.ACTION.ACTION_NETWORK_FAILED -> {
                            Log.d(mTag, "ACTION_NETWORK_FAILED")

                            progressBar!!.visibility = View.GONE
                            btnLogin!!.visibility = View.VISIBLE
                        }
                        Constants.ACTION.ACTION_LOGIN_FAILED -> {
                            Log.d(mTag, "ACTION_LOGIN_FAILED")

                            //Log.e(mTag, "account = $account, password = $password, imei = $imei")
                            progressBar!!.visibility = View.GONE
                            btnLogin!!.visibility = View.VISIBLE
                        }

                    }
                }
            }
        }


        if (!isRegister) {
            filter = IntentFilter()
            filter.addAction(Constants.ACTION.ACTION_NETWORK_FAILED)
            filter.addAction(Constants.ACTION.ACTION_LOGIN_FAILED)
            loginContext?.registerReceiver(mReceiver, filter)
            isRegister = true
            Log.d(mTag, "registerReceiver mReceiver")
        }

        return view
    }

    override fun onDestroyView() {
        Log.i(mTag, "onDestroy")

        if (isRegister && mReceiver != null) {
            try {
                loginContext!!.unregisterReceiver(mReceiver)
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

    private fun toast(message: String) {
        val toast = Toast.makeText(loginContext, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)
        val group = toast.view as ViewGroup
        group.setBackgroundResource(R.drawable.toast_corner_round)
        val textView = group.getChildAt(0) as TextView
        textView.textSize = 25.0f
        toast.show()
    }
}