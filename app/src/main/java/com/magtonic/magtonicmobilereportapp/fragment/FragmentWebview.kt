package com.magtonic.magtonicmobilereportapp.fragment



import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

import android.graphics.Rect
import android.os.Bundle

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings

import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout

import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import com.magtonic.magtonicmobilereportapp.MainActivity.Companion.currentMenuID
import com.magtonic.magtonicmobilereportapp.MainActivity.Companion.isKeyBoardShow
import com.magtonic.magtonicmobilereportapp.MainActivity.Companion.menuList
import com.magtonic.magtonicmobilereportapp.MainActivity.Companion.webviewProgressBar
import com.magtonic.magtonicmobilereportapp.R
import com.magtonic.magtonicmobilereportapp.data.Constants


class FragmentWebview: Fragment(), LifecycleObserver {
    private val mTag = FragmentWebview::class.java.name
    private var webviewContext: Context? = null

    private var mReceiver: BroadcastReceiver? = null
    private var isRegister = false

    private var webView: WebView? = null

    private var relativeLayout: RelativeLayout? = null
    private var linearLayout: LinearLayout? = null

    //var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(mTag, "onCreate")

        webviewContext = context
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(mTag, "onCreateView")

        val view = inflater.inflate(R.layout.fragment_webview, container, false)

        relativeLayout = view.findViewById(R.id.webview_container)
        linearLayout = view.findViewById(R.id.linearLayoutWebView)

        webviewProgressBar = view.findViewById(R.id.progressBar)


        webView = view.findViewById(R.id.webView)
        val webSettings = webView!!.settings
        //webView!!.webViewClient= WebViewClient()
        webSettings.setSupportZoom(true)
        webSettings.loadWithOverviewMode = true
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = true
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webView!!.webChromeClient = MyWebChromeClient()
        webView!!.webViewClient = WebClient()
        webView!!.loadUrl(menuList[currentMenuID].getUrlString())

        //renderWebPage(menuList[currentMenuID].getUrlString())

        linearLayout!!.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            linearLayout!!.getWindowVisibleDisplayFrame(r)
            val screenHeight = linearLayout!!.rootView.height
            val keypadHeight = screenHeight - r.bottom
            isKeyBoardShow = (keypadHeight > screenHeight * 0.15)


        }

        val filter: IntentFilter

        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action != null) {
                    if (intent.action!!.equals(Constants.ACTION.ACTION_WEBVIEW_GOBACK, ignoreCase = true)) {
                        Log.d(mTag, "ACTION_WEBVIEW_GOBACK")

                        if (webView!!.canGoBack()) {
                            webView!!.goBack()
                        } else {
                            val goBackIntent = Intent()
                            goBackIntent.action = Constants.ACTION.ACTION_WEBVIEW_CANNOT_GOBACK
                            webviewContext!!.sendBroadcast(goBackIntent)
                        }


                    }
                }
            }
        }

        if (!isRegister) {
            filter = IntentFilter()
            filter.addAction(Constants.ACTION.ACTION_WEBVIEW_GOBACK)

            webviewContext?.registerReceiver(mReceiver, filter)
            isRegister = true
            Log.d(mTag, "registerReceiver mReceiver")
        }

        return view
    }

    override fun onDestroyView() {
        Log.i(mTag, "onDestroy")

        if (isRegister && mReceiver != null) {
            try {
                webviewContext!!.unregisterReceiver(mReceiver)
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

    class MyWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            Log.i("===>$newProgress", "onProgressChanged")
            if (newProgress < 100) {
                webviewProgressBar!!.visibility = View.VISIBLE
                webviewProgressBar!!.progress = newProgress
            } else {
                webviewProgressBar!!.visibility = View.GONE
            }
        }

    }

    class WebClient : WebViewClient() {
        /*override fun shouldOverrideUrlLoading(
            view: WebView,
            url: String
        ): Boolean {
            view.loadUrl(url)
            return true
        }*/

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)

            Log.e("===>", "onPageFinished")

            webviewProgressBar!!.visibility = View.GONE
        }

        override fun onPageCommitVisible(view: WebView?, url: String?) {
            super.onPageCommitVisible(view, url)

            Log.e("===>", "onPageCommitVisible")

            webviewProgressBar!!.visibility = View.GONE
        }

    }

    /*protected fun renderWebPage(urlToRender: String) {
        webView!!.setWebViewClient(object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
                // Do something on page loading started
            }

            override fun onPageFinished(view: WebView, url: String) {
                // Do something when page loading finished
            }
        })

        webView!!.setWebChromeClient(object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {}
        })

        /*
            WebSettings
                Manages settings state for a WebView. When a WebView is first created, it obtains a
                set of default settings. These default settings will be returned from any getter
                call. A WebSettings object obtained from WebView.getSettings() is tied to the life
                of the WebView. If a WebView has been destroyed, any method call on WebSettings
                will throw an IllegalStateException.
        */
        // Enable the javascript
        webView!!.getSettings().setJavaScriptEnabled(true)

        /*
            public abstract void setSupportZoom (boolean support)
                Sets whether the WebView should support zooming using its on-screen zoom controls
                and gestures. The particular zoom mechanisms that should be used can be set with
                setBuiltInZoomControls(boolean). This setting does not affect zooming performed
                using the zoomIn() and zoomOut() methods. The default is true.

            Parameters
                support : whether the WebView should support zoom

        */
        webView!!.getSettings().setSupportZoom(true)

        /*
            public abstract void setBuiltInZoomControls (boolean enabled)
                Sets whether the WebView should use its built-in zoom mechanisms. The built-in zoom
                mechanisms comprise on-screen zoom controls, which are displayed over the WebView's
                content, and the use of a pinch gesture to control zooming. Whether or not these
                on-screen controls are displayed can be set with setDisplayZoomControls(boolean).
                The default is false.

                The built-in mechanisms are the only currently supported zoom mechanisms, so it is
                recommended that this setting is always enabled.

            Parameters
                enabled : whether the WebView should use its built-in zoom mechanisms
        */
        webView!!.getSettings().setBuiltInZoomControls(true)

        /*
            public abstract void setDisplayZoomControls (boolean enabled)
                Sets whether the WebView should display on-screen zoom controls when using the
                built-in zoom mechanisms. The default is true.

            Parameters
                enabled : whether the WebView should display on-screen zoom controls
        */
        webView!!.getSettings().setDisplayZoomControls(true)

        // Render the web page
        webView!!.loadUrl(urlToRender)
    }*/
}