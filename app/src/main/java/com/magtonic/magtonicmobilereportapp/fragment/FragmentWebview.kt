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
import android.webkit.WebView
import android.webkit.WebViewClient
import com.magtonic.magtonicmobilereportapp.MainActivity.Companion.currentMenuID
import com.magtonic.magtonicmobilereportapp.MainActivity.Companion.menuList
import com.magtonic.magtonicmobilereportapp.R





class FragmentWebview: Fragment()  {
    private val mTag = FragmentWebview::class.java.name
    private var webviewContext: Context? = null

    private var mReceiver: BroadcastReceiver? = null
    private var isRegister = false

    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(mTag, "onCreate")

        webviewContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(mTag, "onCreateView")

        val view = inflater.inflate(R.layout.fragment_webview, container, false)

        webView = view.findViewById<WebView>(R.id.webView)
        val webSettings = webView!!.settings
        webView!!.webViewClient= WebViewClient()
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = true
        webSettings.javaScriptEnabled = true
        webView!!.loadUrl(menuList[currentMenuID].getUrlString())

        //renderWebPage(menuList[currentMenuID].getUrlString())

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(mTag, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)

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