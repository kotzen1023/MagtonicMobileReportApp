package com.magtonic.magtonicmobilereportapp

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
//import android.support.constraint.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout
//import android.support.v4.app.ActivityCompat
import androidx.core.app.ActivityCompat
//import android.support.v4.content.ContextCompat
import androidx.core.content.ContextCompat
//import android.support.v7.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import java.util.ArrayList
import java.util.HashMap

class OpenActivity: AppCompatActivity() {
    private val mTag = OpenActivity::class.java.name

    private val requestIdMultiplePermission = 1

    private var mContext: Context? = null
    private var contraintLayout: ConstraintLayout? = null
    private var animFadeIn: Animation? = null
    private var animFadeOut: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open)

        mContext = applicationContext

        contraintLayout = findViewById(R.id.contraintLayoutOpen)
        //val company: TextView = findViewById(R.id.openTextCompany)
        //val appName: TextView = findViewById(R.id.openTextAppName)
        //  val imgLogo: ImageView = findViewById(R.id.imageLogo);
        animFadeIn = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
        animFadeOut = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out)
        contraintLayout!!.startAnimation(animFadeIn)
        //contraintLayout.startAnimation(animFadeOut)
        //Thread.sleep(1000)
        //contraintLayout.startAnimation(animFadeOut)

        //Handler().postDelayed(
        Handler(Looper.getMainLooper()).postDelayed(
            {
                Log.e(mTag, "Handler().postDelayed")
                loadFadeOut()

            },// run end
            2000
        )

        contraintLayout!!.setOnClickListener {
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkAndRequestPermissions()
            } else {
                val intent = Intent(mContext, MainActivity::class.java)
                startActivity(intent)
                finish()
                //initView()
                //initLog()
            }*/
        }
    }

    override fun onDestroy() {
        Log.i(mTag, "onDestroy")
        super.onDestroy()
    }

    override fun onBackPressed() {
        val confirmdialog = AlertDialog.Builder(this)
        confirmdialog.setIcon(R.drawable.baseline_exit_to_app_black_48)
        confirmdialog.setTitle(resources.getString(R.string.exit_app_title))
        confirmdialog.setMessage(resources.getString(R.string.exit_app_msg))
        confirmdialog.setPositiveButton(
            resources.getString(R.string.confirm)
        ) { _, _ ->
            /*val drawer : DrawerLayout = findViewById(R.id.drawer_layout)
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            }*/

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

    private fun loadFadeOut() {
        contraintLayout!!.startAnimation(animFadeOut)
        contraintLayout!!.visibility = View.GONE

        Handler(Looper.getMainLooper()).postDelayed(
            {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkAndRequestPermissions()
                } else {
                    val intent = Intent(mContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    //initView()
                    //initLog()
                }



            },// run end
            2000
        )
    }

    private fun checkAndRequestPermissions() {

        val networkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)

        val listPermissionsNeeded = ArrayList<String>()

        if (networkPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET)
        }

        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                requestIdMultiplePermission
            )
            //return false;
        } else {
            Log.e(mTag, "All permission are granted")

            val intent = Intent(mContext, MainActivity::class.java)
            startActivity(intent)
            finish()
            //initView()
            //initLog()
        }
        //return true;
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Log.d(mTag, "Permission callback called-------")
        when (requestCode) {
            requestIdMultiplePermission -> {

                val perms = HashMap<String, Int>()
                // Initialize the map with both permissions
                //perms[Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                //perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.INTERNET] = PackageManager.PERMISSION_GRANTED
                //perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
                /*perms[Manifest.permission.ACCESS_NETWORK_STATE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.ACCESS_WIFI_STATE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.CHANGE_NETWORK_STATE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.CHANGE_WIFI_STATE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.BLUETOOTH_ADMIN] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.BLUETOOTH] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.READ_PHONE_STATE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.ACCESS_FINE_LOCATION] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.ACCESS_COARSE_LOCATION] = PackageManager.PERMISSION_GRANTED*/

                // Fill with actual results from user
                //if (grantResults.size > 0) {
                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices) {
                        perms[permissions[i]] = grantResults[i]
                        Log.d(mTag, "perms[permissions["+i+"]] = "+perms[permissions[i]])
                    }
                    // Check for both permissions


                    if (//perms[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                        //&& perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                        //&&
                        perms[Manifest.permission.INTERNET] == PackageManager.PERMISSION_GRANTED
                        //&& perms[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED
                        /*&& perms[Manifest.permission.ACCESS_NETWORK_STATE] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.ACCESS_WIFI_STATE] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.CHANGE_NETWORK_STATE] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.CHANGE_WIFI_STATE] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.BLUETOOTH_ADMIN] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.BLUETOOTH] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.READ_PHONE_STATE] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.ACCESS_FINE_LOCATION] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.ACCESS_COARSE_LOCATION] == PackageManager.PERMISSION_GRANTED*/
                    ) {
                        Log.d(mTag, "write permission granted")

                        // process the normal flow
                        //else any one or both the permissions are not granted
                        //init_folder_and_files()
                        //init_setting();
                        val intent = Intent(mContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        //initView()
                        //initLog()
                    } else {
                        Log.d(mTag, "Some permissions are not granted ask again ")
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                        //                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (/*ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            || */
                            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)
                            //|| ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                            /*|| ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_WIFI_STATE)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CHANGE_NETWORK_STATE)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CHANGE_WIFI_STATE)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_ADMIN)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)*/
                        ) {
                            showDialogOK { _, which ->
                                when (which) {
                                    DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                    DialogInterface.BUTTON_NEGATIVE ->
                                        // proceed with logic by disabling the related features or quit the app.
                                        finish()
                                }
                            }
                        } else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                .show()
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }//|| ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE )
                        //|| ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_WIFI_STATE )
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                    }//&& perms.get(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED &&
                    //perms.get(Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED
                }
            }
        }

    }

    private fun showDialogOK(okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage("Warning")
            .setPositiveButton("Ok", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
    }
}