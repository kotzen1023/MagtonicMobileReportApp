package com.magtonic.magtonicmobilereportapp

import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver

import android.content.Context
import android.content.SharedPreferences

import android.os.Bundle

//import android.support.v4.view.GravityCompat
import androidx.core.view.GravityCompat
//import android.support.v7.app.ActionBarDrawerToggle
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
//import android.support.v4.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout
//import android.support.design.widget.NavigationView
import com.google.android.material.navigation.NavigationView
//import android.support.v4.app.Fragment
import androidx.fragment.app.Fragment
//import android.support.v7.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity
//import android.support.v7.widget.Toolbar
import androidx.appcompat.widget.Toolbar
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.magtonic.magtonicmobilereportapp.data.HeaderURL
import com.magtonic.magtonicmobilereportapp.fragment.FragmentLogin
import com.magtonic.magtonicmobilereportapp.fragment.FragmentLogout
import com.magtonic.magtonicmobilereportapp.fragment.FragmentSetting
import com.magtonic.magtonicmobilereportapp.fragment.FragmentWebview

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

    private var account: String = ""
    private var password: String = ""
    private var username: String = ""
    companion object {
        @JvmStatic var screenWidth: Int = 0
        @JvmStatic var screenHeight: Int = 0
        @JvmStatic var isKeyBoardShow: Boolean = false
        @JvmStatic var menuList: ArrayList<HeaderURL> = ArrayList()
        @JvmStatic var currentMenuID: Int = 0
    }

    enum class CurrentFragment {
        LOGIN_FRAGMENT, LOGOUT_FRAGMENT, SETTING_FRAGMENT, WEBVIEW_FRAGMENT
    }
    private var currentFrag: CurrentFragment = CurrentFragment.WEBVIEW_FRAGMENT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(mTag, "onCreate")

        mContext = applicationContext

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenHeight = displayMetrics.heightPixels
        screenWidth = displayMetrics.widthPixels

        Log.e(mTag, "width = $screenWidth, height = $screenHeight")

        pref = getSharedPreferences(fileName, Context.MODE_PRIVATE)
        account = pref!!.getString("USER_ACCOUNT", "") as String
        password = pref!!.getString("PASSWORD", "") as String
        username = pref!!.getString("USER_NAME", "") as String

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

        val item0 = HeaderURL("YouTube", "https://www.youtube.com/")
        menuList.add(item0)
        val item1 = HeaderURL("Facebook", "https://zh-tw.facebook.com/")
        menuList.add(item1)
        val item2 = HeaderURL("Twitter", "https://twitter.com/")
        menuList.add(item2)
        val item3 = HeaderURL("Google", "https://www.google.com/")
        menuList.add(item3)

        //if (account.equals("") && password.equals("")) {
        if (account.isEmpty() && password.isEmpty()) {
            //set title
            title = getString(R.string.nav_login)


            //show menu
            navView!!.menu.clear()
            //navView!!.menu.setGroupCheckable(R.id.headerMenuList, true, true)

            for (i in 0 until menuList.size) {
                //navView!!.menu.add(menuList[i].getHeader())
                navView!!.menu.add(R.id.headerMenuList, i, 0, menuList[i].getHeader())

                /*var drawable: Drawable
                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                    drawable = resources.getDrawable(R.drawable.baseline_link_black_48, mContext!!.theme)
                } else {
                    drawable = resources.getDrawable(R.drawable.baseline_link_black_48)
                }*/
                val drawable = when {
                    (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) -> {
                        resources.getDrawable(R.drawable.baseline_link_black_48, mContext!!.theme)
                    }
                    else -> resources.getDrawable(R.drawable.baseline_link_black_48)
                }
                navView!!.menu.getItem(i).icon = drawable
                //navView!!.menu.add(R.id.headerMenuList, i, 0, (menuList[i].getHeader()))
            }
            //navView!!.menu.add(R.id.submenu, menuList.size, 0, getString(R.string.nav_others))
            val subMenu = navView!!.menu.addSubMenu(R.id.submenu, menuList.size, 0, getString(R.string.nav_others))
            subMenu.add(R.id.submenu, R.id.nav_setting, 0, getString(R.string.nav_setting))
            subMenu.add(R.id.submenu, R.id.nav_logout, 0, getString(R.string.nav_logout))
            subMenu.add(R.id.submenu, R.id.nav_login, 0, getString(R.string.nav_login))

            /*val drawableSetting: Drawable
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                drawableSetting = resources.getDrawable(R.drawable.baseline_settings_black_48, mContext!!.theme)
            } else {
                drawableSetting = resources.getDrawable(R.drawable.baseline_settings_black_48)
            }*/
            val drawableSetting = when {
                (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) -> {
                    resources.getDrawable(R.drawable.baseline_settings_black_48, mContext!!.theme)
                }
                else -> resources.getDrawable(R.drawable.baseline_settings_black_48)
            }

            navView!!.menu.getItem( menuList.size).subMenu.getItem(0).icon = drawableSetting //setting
            navView!!.menu.getItem( menuList.size).subMenu.getItem(0).isVisible = true
            /*val drawableLogout: Drawable
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                drawableLogout = resources.getDrawable(R.drawable.baseline_exit_to_app_black_48, mContext!!.theme)
            } else {
                drawableLogout = resources.getDrawable(R.drawable.baseline_exit_to_app_black_48)
            }*/

            val drawableLogout = when {
                (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) -> {
                    resources.getDrawable(R.drawable.baseline_exit_to_app_black_48, mContext!!.theme)
                }
                else -> resources.getDrawable(R.drawable.baseline_exit_to_app_black_48)
            }

            navView!!.menu.getItem( menuList.size).subMenu.getItem(1).icon = drawableLogout //logout
            navView!!.menu.getItem( menuList.size).subMenu.getItem(1).isVisible = false
            /*val drawableLogin: Drawable
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                drawableLogin = resources.getDrawable(R.drawable.baseline_touch_app_black_48, mContext!!.theme)
            } else {
                drawableLogin = resources.getDrawable(R.drawable.baseline_touch_app_black_48)
            }*/
            val drawableLogin = when {
                (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) -> {
                    resources.getDrawable(R.drawable.baseline_touch_app_black_48, mContext!!.theme)
                }
                else -> resources.getDrawable(R.drawable.baseline_touch_app_black_48)
            }


            navView!!.menu.getItem( menuList.size).subMenu.getItem(2).icon = drawableLogin //login
            navView!!.menu.getItem( menuList.size).subMenu.getItem(2).isVisible = true




            //navView!!.menu.addSubMenu(R.string.nav_setting)
            //navView!!.menu.addSubMenu(R.string.nav_logout)
            //navView!!.menu.addSubMenu(R.string.nav_login)
            //navView!!.menu.getItem(menuList.size).subMenu.getItem(0).isVisible = false //setting
            //navView!!.menu.getItem(menuList.size).subMenu.getItem(1).isVisible = false //logout
            //navView!!.menu.getItem(menuList.size).subMenu.getItem(2).isVisible = true //login
            //show login

            fragmentClass = FragmentLogin::class.java
            currentFrag = CurrentFragment.LOGIN_FRAGMENT

        } else {
            //show receipt
            //set username
            if (textViewUserName != null) {
                //textViewUserName!!.setText(getString(R.string.nav_greeting, username))
                textViewUserName!!.text = getString(R.string.nav_greeting, username)
            } else {
                Log.e(mTag, "textViewUserName == null")
            }

            //set title
            //title = getString(R.string.nav_punchcard)

            //show menu
            navView!!.menu.clear()
            //navView!!.menu.setGroupCheckable(R.id.headerMenuList, true, true)

            for (i in 0 until menuList.size) {
                //navView!!.menu.add(menuList[i].getHeader())
                navView!!.menu.add(R.id.headerMenuList, i, 0, menuList[i].getHeader())

                /*var drawable: Drawable
                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                    drawable = resources.getDrawable(R.drawable.baseline_link_black_48, mContext!!.theme)
                } else {
                    drawable = resources.getDrawable(R.drawable.baseline_link_black_48)
                }*/
                val drawable = when {
                    (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) -> {
                        resources.getDrawable(R.drawable.baseline_link_black_48, mContext!!.theme)
                    }
                    else -> resources.getDrawable(R.drawable.baseline_link_black_48)
                }


                navView!!.menu.getItem(i).icon = drawable
                //navView!!.menu.add(R.id.headerMenuList, i, 0, (menuList[i].getHeader()))
            }
            //navView!!.menu.add(R.id.submenu, menuList.size, 0, getString(R.string.nav_others))
            val subMenu = navView!!.menu.addSubMenu(R.id.submenu, menuList.size, 0, getString(R.string.nav_others))
            subMenu.add(R.id.submenu, R.id.nav_setting, 0, getString(R.string.nav_setting))
            subMenu.add(R.id.submenu, R.id.nav_logout, 0, getString(R.string.nav_logout))
            subMenu.add(R.id.submenu, R.id.nav_login, 0, getString(R.string.nav_login))

            /*var drawableSetting: Drawable
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                drawableSetting = resources.getDrawable(R.drawable.baseline_settings_black_48, mContext!!.theme)
            } else {
                drawableSetting = resources.getDrawable(R.drawable.baseline_settings_black_48)
            }*/
            val drawableSetting = when {
                (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) -> {
                    resources.getDrawable(R.drawable.baseline_settings_black_48, mContext!!.theme)
                }
                else -> resources.getDrawable(R.drawable.baseline_settings_black_48)
            }



            navView!!.menu.getItem( menuList.size).subMenu.getItem(0).icon = drawableSetting //setting
            navView!!.menu.getItem( menuList.size).subMenu.getItem(0).isVisible = false
            /*var drawableLogout: Drawable
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                drawableLogout = resources.getDrawable(R.drawable.baseline_exit_to_app_black_48, mContext!!.theme)
            } else {
                drawableLogout = resources.getDrawable(R.drawable.baseline_exit_to_app_black_48)
            }*/
            val drawableLogout = when {
                (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) -> {
                    resources.getDrawable(R.drawable.baseline_exit_to_app_black_48, mContext!!.theme)
                }
                else -> resources.getDrawable(R.drawable.baseline_exit_to_app_black_48)

            }


            navView!!.menu.getItem( menuList.size).subMenu.getItem(1).icon = drawableLogout //logout
            navView!!.menu.getItem( menuList.size).subMenu.getItem(1).isVisible = true
            /*var drawableLogin: Drawable
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                drawableLogin = resources.getDrawable(R.drawable.baseline_touch_app_black_48, mContext!!.theme)
            } else {
                drawableLogin = resources.getDrawable(R.drawable.baseline_touch_app_black_48)
            }*/

            val drawableLogin = when {
                (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) -> {
                    resources.getDrawable(R.drawable.baseline_touch_app_black_48, mContext!!.theme)
                }
                else -> resources.getDrawable(R.drawable.baseline_touch_app_black_48)
            }

            navView!!.menu.getItem( menuList.size).subMenu.getItem(2).icon = drawableLogin //login
            navView!!.menu.getItem( menuList.size).subMenu.getItem(2).isVisible = false

            fragmentClass = FragmentWebview::class.java

            currentFrag = CurrentFragment.WEBVIEW_FRAGMENT
        }

        try {
            fragment = fragmentClass.newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val fragmentManager = supportFragmentManager
        //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment!!).commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        val confirmdialog = AlertDialog.Builder(this@MainActivity)
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
        confirmdialog.show()
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

        if (currentMenuID != menuItem.itemId) {
            createNewFragment = true
        }
        currentMenuID = menuItem.itemId

        when (menuItem.itemId) {

            R.id.nav_login -> {
                if (createNewFragment) {
                    title = getString(R.string.nav_login)
                    fragmentClass = FragmentLogin::class.java
                    currentFrag = CurrentFragment.LOGIN_FRAGMENT
                }
            }
            R.id.nav_logout -> {
                if (createNewFragment) {
                    title = getString(R.string.nav_logout)
                    fragmentClass = FragmentLogout::class.java
                    currentFrag = CurrentFragment.LOGOUT_FRAGMENT
                }
            }
            R.id.nav_setting -> {
                if (createNewFragment) {
                    title = getString(R.string.nav_setting)
                    fragmentClass = FragmentSetting::class.java
                    currentFrag = CurrentFragment.SETTING_FRAGMENT
                }
            }
            else -> { //webview
                if (createNewFragment) {
                    fragmentClass = FragmentWebview::class.java
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
}
