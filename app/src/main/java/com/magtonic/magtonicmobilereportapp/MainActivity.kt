package com.magtonic.magtonicmobilereportapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.*

import android.os.Build

import android.os.Bundle


import androidx.core.view.GravityCompat

import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem

import androidx.drawerlayout.widget.DrawerLayout

import com.google.android.material.navigation.NavigationView

import androidx.fragment.app.Fragment

import androidx.appcompat.app.AppCompatActivity

import androidx.appcompat.widget.Toolbar
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import com.magtonic.magtonicmobilereportapp.api.ApiFunc
import com.magtonic.magtonicmobilereportapp.data.Constants
import com.magtonic.magtonicmobilereportapp.data.HeaderURL
import com.magtonic.magtonicmobilereportapp.fragment.FragmentLogin

import com.magtonic.magtonicmobilereportapp.fragment.FragmentSetting
import com.magtonic.magtonicmobilereportapp.fragment.FragmentWebview
import com.magtonic.magtonicmobilereportapp.model.receive.ReceiveTransform
import com.magtonic.magtonicmobilereportapp.model.send.HttpUserAuthPara
import com.magtonic.magtonicmobilereportapp.model.sys.User
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val mTag = MainActivity::class.java.name

    var pref: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    private val fileName = "Preference"

    private var mContext: Context? = null

    private var imm: InputMethodManager? = null
    private var mReceiver: BroadcastReceiver? = null
    private var isRegister = false

    private var navView: NavigationView? = null
    private var textViewUserName: TextView? = null

    private var toastHandle: Toast? = null

    private var account: String = ""
    private var password: String = ""
    //private var username: String = ""
    companion object {
        @JvmStatic var screenWidth: Int = 0
        @JvmStatic var screenHeight: Int = 0
        @JvmStatic var isKeyBoardShow: Boolean = false
        @JvmStatic var menuList: ArrayList<HeaderURL> = ArrayList()
        @JvmStatic var currentMenuID: Int = 0
        @JvmStatic var user: User? = null
        @JvmStatic var webviewProgressBar: ProgressBar? = null
    }

    enum class CurrentFragment {
        LOGIN_FRAGMENT, SETTING_FRAGMENT, WEBVIEW_FRAGMENT
    }
    private var currentFrag: CurrentFragment = CurrentFragment.WEBVIEW_FRAGMENT

    private var currentSelectMenuItem: MenuItem? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(mTag, "onCreate")

        mContext = applicationContext

        val displayMetrics = DisplayMetrics()
        //windowManager.defaultDisplay.getMetrics(displayMetrics)
        //mContext!!.display!!.getMetrics(displayMetrics)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M)
        {
            windowManager.defaultDisplay.getMetrics(displayMetrics)

            screenHeight = displayMetrics.heightPixels
            screenWidth = displayMetrics.widthPixels
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            mContext!!.display!!.getRealMetrics(displayMetrics)

            screenHeight = displayMetrics.heightPixels
            screenWidth = displayMetrics.widthPixels
        } else { //Android 11
            //mContext!!.display!!.getMetrics(displayMetrics)
            screenHeight = windowManager.currentWindowMetrics.bounds.height()
            screenWidth = windowManager.currentWindowMetrics.bounds.width()

        }

        Log.e(mTag, "width = $screenWidth, height = $screenHeight")

        pref = getSharedPreferences(fileName, Context.MODE_PRIVATE)
        account = pref!!.getString(User.USER_ACCOUNT, "") as String
        password = pref!!.getString(User.PASSWORD, "") as String

        user = User()

        user!!.userAccount = account
        user!!.password = password

        Log.e(mTag, "account = "+user!!.userAccount+", password = "+user!!.password)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val header = navView!!.inflateHeaderView(R.layout.nav_header_main)
        textViewUserName = header.findViewById(R.id.textViewUserName)
        navView!!.removeHeaderView(navView!!.getHeaderView(0))

        val mDrawerToggle = object : ActionBarDrawerToggle(
            this, /* host Activity */
            drawerLayout, /* DrawerLayout object */
            toolbar, /* nav drawer icon to replace 'Up' caret */
            R.string.navigation_drawer_open, /* "open drawer" description */
            R.string.navigation_drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state.  */

            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)

                Log.d(mTag, "onDrawerClosed")

            }

            /** Called when a drawer has settled in a completely open state.  */
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)

                Log.d(mTag, "onDrawerOpened")

                if (isKeyBoardShow) {
                    imm?.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0)
                }
            }
        }

        drawerLayout.addDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()
        navView!!.setNavigationItemSelectedListener(this)

        //check login
        var fragment: Fragment? = null
        //var fragmentClass: Class<*>? = null
        val fragmentClass: Class<*>

        /*val item0 = HeaderURL("YouTube", "https://www.youtube.com/")
        menuList.add(item0)
        val item1 = HeaderURL("Facebook", "https://zh-tw.facebook.com/")
        menuList.add(item1)
        val item2 = HeaderURL("Twitter", "https://twitter.com/")
        menuList.add(item2)
        val item3 = HeaderURL("Google", "https://www.google.com/")
        menuList.add(item3)*/

        //if (account.equals("") && password.equals("")) {
        if (account.isEmpty() && password.isEmpty()) {
            //set title
            title = getString(R.string.nav_login)


            //show menu
            navView!!.menu.clear()

            navView!!.menu.add(R.id.headerMenuList, R.id.nav_setting, 0, getString(R.string.nav_setting))
            //val drawableSetting = resources.getDrawable(R.drawable.baseline_settings_black_48, mContext!!.theme)
            val drawableSetting = ResourcesCompat.getDrawable(resources, R.drawable.baseline_settings_black_48, mContext!!.theme)
            navView!!.menu.getItem(0).icon = drawableSetting
            navView!!.menu.getItem(0).isVisible = true

            //navView!!.menu.getItem( menuList.size).subMenu.getItem(0).icon = drawableSetting //setting
            //navView!!.menu.getItem( menuList.size).subMenu.getItem(0).isVisible = true
            navView!!.menu.add(R.id.headerMenuList, R.id.nav_logout, 0, getString(R.string.nav_logout))
            //val drawableLogout = resources.getDrawable(R.drawable.baseline_exit_to_app_black_48, mContext!!.theme)
            val drawableLogout = ResourcesCompat.getDrawable(resources, R.drawable.baseline_exit_to_app_black_48, mContext!!.theme)
            navView!!.menu.getItem(1).icon = drawableLogout
            navView!!.menu.getItem(1).isVisible = false

            //navView!!.menu.getItem( menuList.size).subMenu.getItem(1).icon = drawableLogout //logout
            //navView!!.menu.getItem( menuList.size).subMenu.getItem(1).isVisible = false

            navView!!.menu.add(R.id.headerMenuList, R.id.nav_login, 0, getString(R.string.nav_login))
            //val drawableLogin = resources.getDrawable(R.drawable.baseline_touch_app_black_48, mContext!!.theme)
            val drawableLogin = ResourcesCompat.getDrawable(resources, R.drawable.baseline_touch_app_black_48, mContext!!.theme)
            navView!!.menu.getItem(2).icon = drawableLogin
            navView!!.menu.getItem(2).isVisible = true

            //navView!!.menu.getItem( menuList.size).subMenu.getItem(2).icon = drawableLogin //login
            //navView!!.menu.getItem( menuList.size).subMenu.getItem(2).isVisible = true

            fragmentClass = FragmentLogin::class.java
            currentFrag = CurrentFragment.LOGIN_FRAGMENT

        } else {
            //autologin
            Log.e(mTag, "account = $account password $password")

            runOnUiThread {
                callAPILogin(account, password)
            }

            fragmentClass = FragmentLogin::class.java

            currentFrag = CurrentFragment.LOGIN_FRAGMENT
        }

        try {
            fragment = fragmentClass.newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val fragmentManager = supportFragmentManager
        //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commitAllowingStateLoss()



        val filter: IntentFilter
        @SuppressLint("CommitPrefEdits")
        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action != null) {
                    when {
                        intent.action!!.equals(Constants.ACTION.ACTION_LOGIN_ACTION, ignoreCase = true) -> {
                            Log.d(mTag, "ACTION_LOGIN_ACTION")
                            account = intent.getStringExtra("account") as String
                            password = intent.getStringExtra("password") as String

                            Log.e(mTag, "account = $account password $password")

                            runOnUiThread {
                                callAPILogin(account, password)
                            }


                        }
                        intent.action!!.equals(Constants.ACTION.ACTION_LOGIN_NETWORK_ERROR, ignoreCase = true) -> {
                            Log.d(mTag, "ACTION_LOGIN_NETWORK_ERROR")


                        }
                        intent.action!!.equals(Constants.ACTION.ACTION_LOGIN_SUCCESS, ignoreCase = true) -> {
                            Log.d(mTag, "ACTION_LOGIN_SUCCESS")

                            title = menuList[0].getHeader()

                            //set username
                            //textViewUserName!!.setText(getString(R.string.nav_greeting, username))
                            //textViewUserName!!.text = getString(R.string.nav_greeting, username)
                            //save to User
                            user!!.userAccount = account
                            user!!.password = password
                            user!!.isLogin = true

                            //hide keyboard
                            Log.e(mTag, "isKeyBoardShow = $isKeyBoardShow")
                            if (isKeyBoardShow)
                                imm?.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0)

                            //save
                            editor = pref!!.edit()
                            editor!!.putString(User.USER_ACCOUNT, account)
                            editor!!.putString(User.PASSWORD, password)
                            editor!!.apply()

                            //start with receipt fragment
                            var webfragment: Fragment? = null
                            val webFragmentClass = FragmentWebview::class.java

                            try {
                                webfragment = webFragmentClass.newInstance()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                            val webFragmentManager = supportFragmentManager
                            //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                            webFragmentManager.beginTransaction().replace(R.id.flContent, webfragment!!).commitAllowingStateLoss()

                            navView!!.menu.getItem(0).isChecked = true


                        }
                        intent.action!!.equals(Constants.ACTION.ACTION_LOGIN_FAILED, ignoreCase = true) -> {
                            Log.d(mTag, "ACTION_LOGIN_FAILED")


                        }
                        intent.action!!.equals(Constants.ACTION.ACTION_LOGOUT_ACTION, ignoreCase = true) -> {
                            Log.d(mTag, "ACTION_LOGOUT_ACTION")

                            //save to User
                            user!!.userAccount = ""
                            user!!.password = ""
                            user!!.isLogin = false
                            //saveRJStorageUpload
                            editor = pref?.edit()
                            editor?.putString(User.PASSWORD, "")
                            editor?.putString(User.USER_ACCOUNT, "")
                            editor?.apply()

                            navView!!.menu.clear()

                            navView!!.menu.add(R.id.headerMenuList, R.id.nav_setting, 0, getString(R.string.nav_setting))
                            //val drawableSetting = resources.getDrawable(R.drawable.baseline_settings_black_48, mContext!!.theme)
                            val drawableSetting = ResourcesCompat.getDrawable(resources, R.drawable.baseline_settings_black_48, mContext!!.theme)
                            navView!!.menu.getItem(0).icon = drawableSetting
                            navView!!.menu.getItem(0).isVisible = true

                            navView!!.menu.add(R.id.headerMenuList, R.id.nav_logout, 0, getString(R.string.nav_logout))
                            //val drawableLogout = resources.getDrawable(R.drawable.baseline_exit_to_app_black_48, mContext!!.theme)
                            val drawableLogout = ResourcesCompat.getDrawable(resources, R.drawable.baseline_exit_to_app_black_48, mContext!!.theme)
                            navView!!.menu.getItem(1).icon = drawableLogout
                            navView!!.menu.getItem(1).isVisible = false

                            navView!!.menu.add(R.id.headerMenuList, R.id.nav_login, 0, getString(R.string.nav_login))
                            //val drawableLogin = resources.getDrawable(R.drawable.baseline_touch_app_black_48, mContext!!.theme)
                            val drawableLogin = ResourcesCompat.getDrawable(resources, R.drawable.baseline_touch_app_black_48, mContext!!.theme)
                            navView!!.menu.getItem(2).icon = drawableLogin
                            navView!!.menu.getItem(2).isVisible = true


                            currentFrag = CurrentFragment.LOGIN_FRAGMENT


                            var loginFragment: Fragment? = null
                            val loginFragmentClass = FragmentLogin::class.java

                            try {
                                loginFragment = loginFragmentClass.newInstance()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                            // Insert the fragment by replacing any existing fragment
                            val loginFragmentManager = supportFragmentManager
                            //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                            loginFragmentManager.beginTransaction().replace(R.id.flContent, loginFragment!!).commitAllowingStateLoss()

                            navView!!.menu.getItem(2).isChecked = true //login

                            title = resources.getString(R.string.nav_login)
                        }
                        intent.action!!.equals(Constants.ACTION.ACTION_HIDE_KEYBOARD, ignoreCase = true) -> {
                            Log.d(mTag, "ACTION_HIDE_KEYBOARD")

                            imm?.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0)
                        }
                        intent.action!!.equals(Constants.ACTION.ACTION_WEBVIEW_CANNOT_GOBACK, ignoreCase = true) -> {
                            Log.d(mTag, "ACTION_WEBVIEW_CANNOT_GOBACK")

                            showExitConfirmDialog()
                        }
                    }
                }


            }
        }


        if (!isRegister) {
            filter = IntentFilter()
            //login
            filter.addAction(Constants.ACTION.ACTION_LOGIN_ACTION)
            filter.addAction(Constants.ACTION.ACTION_LOGIN_NETWORK_ERROR)
            filter.addAction(Constants.ACTION.ACTION_LOGIN_SUCCESS)
            filter.addAction(Constants.ACTION.ACTION_LOGIN_FAILED)
            //logout
            filter.addAction(Constants.ACTION.ACTION_LOGOUT_ACTION)

            //hide keyboard
            filter.addAction(Constants.ACTION.ACTION_HIDE_KEYBOARD)
            //detect webview cannot go back
            filter.addAction(Constants.ACTION.ACTION_WEBVIEW_CANNOT_GOBACK)

            mContext!!.registerReceiver(mReceiver, filter)
            isRegister = true
            Log.d(mTag, "registerReceiver mReceiver")
        }
    }

    override fun onDestroy() {
        Log.i(mTag, "onDestroy")

        if (isRegister && mReceiver != null) {
            try {
                mContext!!.unregisterReceiver(mReceiver)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }

            isRegister = false
            mReceiver = null
            Log.d(mTag, "unregisterReceiver mReceiver")
        }

        super.onDestroy()
    }

    override fun onResume() {
        Log.i(mTag, "onResume")
        super.onResume()

    }

    override fun onPause() {
        Log.i(mTag, "onPause")
        super.onPause()

    }

    override fun onBackPressed() {

        val goBackIntent = Intent()
        goBackIntent.action = Constants.ACTION.ACTION_WEBVIEW_GOBACK
        sendBroadcast(goBackIntent)

        /*val confirmdialog = AlertDialog.Builder(this@MainActivity)
        confirmdialog.setIcon(R.drawable.baseline_exit_to_app_black_48)
        confirmdialog.setTitle(resources.getString(R.string.exit_app_title))
        confirmdialog.setMessage(resources.getString(R.string.exit_app_msg))
        confirmdialog.setPositiveButton(
            resources.getString(R.string.confirm)
        ) { _, _ ->
            val drawer : DrawerLayout = findViewById(R.id.drawer_layout)
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            }

            //isLogin = false

            finish()
        }
        confirmdialog.setNegativeButton(
            resources.getString(R.string.cancel)
        ) { _, _ ->
            // btnScan.setVisibility(View.VISIBLE);
            // btnConfirm.setVisibility(View.GONE);
        }
        confirmdialog.show()*/
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.main_hide_or_show_keyboard -> {
                imm?.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0)
            }
        }

        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        selectDrawerItem(item)


        return true
    }

    private fun selectDrawerItem(menuItem: MenuItem) {
        var fragment: Fragment? = null
        var fragmentClass: Class<*>? = null
        var createNewFragment = false

        var title = ""
        //hide keyboard
        val view = currentFocus

        if (view != null) {
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }

        Log.e(mTag, "MenuItem ID = "+menuItem.itemId)

        if (menuItem.itemId != R.id.nav_logout) {
            if (currentMenuID != menuItem.itemId) {
                createNewFragment = true
            }
            currentSelectMenuItem = menuItem
            currentMenuID = menuItem.itemId
        }



        for (i in 0 until navView!!.menu.size()) {
            navView!!.menu.getItem(i).isChecked = false
        }

        when (menuItem.itemId) {

            R.id.nav_login -> {
                /*if (createNewFragment) {
                    title = getString(R.string.nav_login)
                    fragmentClass = FragmentLogin::class.java
                    currentFrag = CurrentFragment.LOGIN_FRAGMENT
                }*/
                title = getString(R.string.nav_login)
                fragmentClass = FragmentLogin::class.java
                menuItem.isChecked = true
                currentFrag = CurrentFragment.LOGIN_FRAGMENT
            }
            R.id.nav_logout -> {

                //currentMenuID = -1
                //createNewFragment = false
                menuItem.isChecked = true
                showLogoutConfirmDialog()
            }
            R.id.nav_setting -> {
                if (createNewFragment) {
                    title = getString(R.string.nav_setting)
                    fragmentClass = FragmentSetting::class.java
                    menuItem.isChecked = true
                    currentFrag = CurrentFragment.SETTING_FRAGMENT
                }

            }
            else -> { //webview
                if (createNewFragment) {
                    title = menuList[currentMenuID].getHeader()
                    fragmentClass = FragmentWebview::class.java
                    menuItem.isChecked = true
                    currentFrag = CurrentFragment.WEBVIEW_FRAGMENT
                }

            }
        }

        if (createNewFragment) {
            try {
                fragment = fragmentClass?.newInstance() as Fragment
            } catch (e: Exception) {
                e.printStackTrace()
            }


            // Insert the fragment by replacing any existing fragment
            val fragmentManager = supportFragmentManager
            //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commitAllowingStateLoss()

            // Highlight the selected item has been done by NavigationView

            // Set action bar title
            if (title.isNotEmpty())
                setTitle(title)
            else
                setTitle(menuItem.title)
        }


        // Close the navigation drawer
        val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
    }

    fun callAPILogin(account: String, password: String) {
        Log.e(mTag, "callAPILogin")
        val para =
            HttpUserAuthPara()
        para.username = account
        para.password = password

        ApiFunc().login(para, loginCallback)
    }//login

    private var loginCallback: Callback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e(mTag, "err msg = $e")
            runOnUiThread(netErrRunnable)
        }

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: Response) {
            val res = ReceiveTransform.removeArraySignal(response.body!!.string())

            //Log.e(mTag, "response.body!!.string() = ${response.body!!.string()}")



            runOnUiThread {

                if (res != "No Authority!") {

                    Log.e(mTag, "res = $res")
                    var tempString = res.replace("{", "")
                    tempString = tempString.replace("}", "|")
                    Log.e(mTag, "tempString = $tempString")

                    //val headerString = tempString.replace(" ", "")
                    //Log.e(mTag, "headerString = $headerString")

                    val headerArray = tempString.split("|")

                    menuList.clear()

                    for (i in headerArray.indices) {
                        Log.e(mTag, "headerArray[$i] = ${headerArray[i]}")

                        if (headerArray[i] != "") {
                            val itemArray = headerArray[i].split(",")

                            if (itemArray.size == 2) {
                                val item = HeaderURL(itemArray[0], itemArray[1])
                                menuList.add(item)
                            }


                        }
                    }

                    //show menu
                    navView!!.menu.clear()

                    for (i in 0 until menuList.size) {
                        //navView!!.menu.add(menuList[i].getHeader())
                        navView!!.menu.add(R.id.headerMenuList, i, 0, menuList[i].getHeader())

                        //val drawable = resources.getDrawable(R.drawable.baseline_link_black_48, mContext!!.theme)
                        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.baseline_link_black_48, mContext!!.theme)

                        navView!!.menu.getItem(i).icon = drawable
                        //navView!!.menu.add(R.id.headerMenuList, i, 0, (menuList[i].getHeader()))

                    }

                    navView!!.menu.add(
                        R.id.headerMenuList,
                        R.id.nav_setting,
                        0,
                        getString(R.string.nav_setting)
                    )
                    //val drawableSetting = resources.getDrawable(R.drawable.baseline_settings_black_48, mContext!!.theme)
                    val drawableSetting = ResourcesCompat.getDrawable(resources, R.drawable.baseline_settings_black_48, mContext!!.theme)
                    navView!!.menu.getItem(menuList.size).icon = drawableSetting
                    navView!!.menu.getItem(menuList.size).isVisible = true

                    //navView!!.menu.getItem( menuList.size).subMenu.getItem(0).icon = drawableSetting //setting
                    //navView!!.menu.getItem( menuList.size).subMenu.getItem(0).isVisible = true
                    navView!!.menu.add(
                        R.id.headerMenuList,
                        R.id.nav_logout,
                        0,
                        getString(R.string.nav_logout)
                    )
                    //val drawableLogout = resources.getDrawable(R.drawable.baseline_exit_to_app_black_48, mContext!!.theme)
                    val drawableLogout = ResourcesCompat.getDrawable(resources, R.drawable.baseline_exit_to_app_black_48, mContext!!.theme)
                    navView!!.menu.getItem(menuList.size + 1).icon = drawableLogout
                    navView!!.menu.getItem(menuList.size + 1).isVisible = true

                    //navView!!.menu.getItem( menuList.size).subMenu.getItem(1).icon = drawableLogout //logout
                    //navView!!.menu.getItem( menuList.size).subMenu.getItem(1).isVisible = false

                    navView!!.menu.add(
                        R.id.headerMenuList,
                        R.id.nav_login,
                        0,
                        getString(R.string.nav_logout)
                    )
                    //val drawableLogin = resources.getDrawable(R.drawable.baseline_touch_app_black_48, mContext!!.theme)
                    val drawableLogin = ResourcesCompat.getDrawable(resources, R.drawable.baseline_touch_app_black_48, mContext!!.theme)
                    navView!!.menu.getItem(menuList.size + 2).icon = drawableLogin
                    navView!!.menu.getItem(menuList.size + 2).isVisible = false

                    Log.e(mTag, "loginCallback success")

                    val successIntent = Intent()
                    successIntent.action = Constants.ACTION.ACTION_LOGIN_SUCCESS
                    sendBroadcast(successIntent)

                } else {
                    Log.e(mTag, "loginCallback failed")

                    toast(getString(R.string.login_fail))

                    val failIntent = Intent()
                    failIntent.action = Constants.ACTION.ACTION_LOGIN_FAILED
                    sendBroadcast(failIntent)
                }

                /*navView!!.menu.add(R.id.headerMenuList, menuList.size, 0, getString(R.string.nav_setting))
                val drawableSetting = resources.getDrawable(R.drawable.baseline_settings_black_48, mContext!!.theme)
                navView!!.menu.getItem(menuList.size).icon = drawableSetting

                navView!!.menu.add(R.id.headerMenuList, menuList.size+1, 0, getString(R.string.nav_logout))
                val drawableLogout = resources.getDrawable(R.drawable.baseline_exit_to_app_black_48, mContext!!.theme)
                navView!!.menu.getItem(menuList.size+1).icon = drawableLogout

                navView!!.menu.add(R.id.headerMenuList, menuList.size+2, 0, getString(R.string.nav_login))
                val drawableLogin = resources.getDrawable(R.drawable.baseline_touch_app_black_48, mContext!!.theme)
                navView!!.menu.getItem(menuList.size+2).icon = drawableLogin*/



            }

            /*runOnUiThread {
                try {
                    //val  rjUser: RJUser? = null
                    //val  rjUser: RJUser = Gson().fromJson<Any>(res, RJUser::class.javaObjectType) as RJUser

                    val rjUser = Gson().fromJson<Any>(res, RJUser::class.java) as RJUser

                    //if (rjUser.result.equals("0")) {
                    if (rjUser.result == "0") {
                        //fail
                        //mLoadingView.setStatus(LoadingView.GONE)
                        // Toast.makeText(mContext,rjUser.tc_zx104,Toast.LENGTH_LONG).show();
                        //showMyToast(rjUser.tc_zx104, mContext)
                        toast(rjUser.tc_zx104)

                        val failIntent = Intent()
                        failIntent.action = Constants.ACTION.ACTION_LOGIN_FAILED
                        sendBroadcast(failIntent)
                    } else {
                        //success
                        Log.e(mTag, "loginCallback success")

                        val successIntent = Intent()
                        successIntent.action = Constants.ACTION.ACTION_LOGIN_SUCCESS
                        sendBroadcast(successIntent)


                        username = rjUser.tc_zx104


                        Log.e(
                            mTag,
                            "username = " + rjUser.tc_zx104 + " account = " + account + " password = " + password
                        )
                    }

                }// try
                catch (e: IOException) {
                    //mLoadingView.setStatus(LoadingView.GONE)
                    //Toast.makeText(mContext,getString(R.string.toast_server_error),Toast.LENGTH_LONG).show();
                    //showMyToast(getString(R.string.toast_server_error), mContext)
                    e.printStackTrace()

                    val failIntent = Intent()
                    failIntent.action = Constants.ACTION.ACTION_LOGIN_FAILED
                    sendBroadcast(failIntent)

                    runOnUiThread {

                        toast(getString(R.string.toast_server_error))
                    }
                }
            }*/
        }//response
    }

    internal var netErrRunnable: Runnable = Runnable {
        //mLoadingView.setStatus(LoadingView.GONE)
        // Toast.makeText(mContext,getString(R.string.toast_network_error),Toast.LENGTH_LONG).show();
        //showMyToast(getString(R.string.toast_network_error), mContext)
        toast(getString(R.string.toast_network_error))
        val failIntent = Intent()
        failIntent.action = Constants.ACTION.ACTION_NETWORK_FAILED
        sendBroadcast(failIntent)


    }

    private fun toast(message: String) {

        if (toastHandle != null)
            toastHandle!!.cancel()

        val toast = Toast.makeText(mContext, HtmlCompat.fromHtml("<h1>$message</h1>", HtmlCompat.FROM_HTML_MODE_COMPACT), Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)

        /*val toast = Toast.makeText(outsourcedProcessContext, message, Toast.LENGTH_SHORT)
         toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)
         val group = toast.view as ViewGroup
         val textView = group.getChildAt(0) as TextView
         textView.textSize = 30.0f*/
        toast.show()

        toastHandle = toast
    }

    private fun showLogoutConfirmDialog() {

        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        val promptView = View.inflate(this@MainActivity, R.layout.confirm_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(this@MainActivity).create()
        alertDialogBuilder.setView(promptView)

        //final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        val textViewMsg = promptView.findViewById<TextView>(R.id.textViewDialog)
        val btnCancel = promptView.findViewById<Button>(R.id.btnDialogCancel)
        val btnConfirm = promptView.findViewById<Button>(R.id.btnDialogConfirm)

        textViewMsg.text = getString(R.string.logout_title_msg)
        btnCancel.text = getString(R.string.cancel)
        btnConfirm.text = getString(R.string.confirm)

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
        btnCancel!!.setOnClickListener {
            if (currentSelectMenuItem != null) {
                currentSelectMenuItem!!.isChecked = false
            }

            alertDialogBuilder.dismiss()
        }
        btnConfirm!!.setOnClickListener {
            val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
            drawer.closeDrawer(GravityCompat.START)

            val logoutIntent = Intent(Constants.ACTION.ACTION_LOGOUT_ACTION)
            mContext?.sendBroadcast(logoutIntent)
            alertDialogBuilder.dismiss()
        }
        alertDialogBuilder.show()

    }

    private fun showExitConfirmDialog() {

        // get prompts.xml view
        /*LayoutInflater layoutInflater = LayoutInflater.from(Nfc_read_app.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);*/
        val promptView = View.inflate(this@MainActivity, R.layout.confirm_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(this@MainActivity).create()
        alertDialogBuilder.setView(promptView)

        //final EditText editFileName = (EditText) promptView.findViewById(R.id.editFileName);
        val textViewMsg = promptView.findViewById<TextView>(R.id.textViewDialog)
        val btnCancel = promptView.findViewById<Button>(R.id.btnDialogCancel)
        val btnConfirm = promptView.findViewById<Button>(R.id.btnDialogConfirm)

        textViewMsg.text = getString(R.string.exit_app_msg)
        btnCancel.text = getString(R.string.cancel)
        btnConfirm.text = getString(R.string.confirm)

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
        btnCancel!!.setOnClickListener {
            alertDialogBuilder.dismiss()
        }
        btnConfirm!!.setOnClickListener {
            val drawer : DrawerLayout = findViewById(R.id.drawer_layout)
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            }
            alertDialogBuilder.dismiss()
            //isLogin = false

            finish()


        }
        alertDialogBuilder.show()
    }
}
