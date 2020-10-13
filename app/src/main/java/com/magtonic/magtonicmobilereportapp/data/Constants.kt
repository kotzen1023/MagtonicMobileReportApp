package com.magtonic.magtonicmobilereportapp.data

class Constants {
    class ACTION {
        companion object {

            const val ACTION_LOGIN_ACTION : String = "com.magtonic.MagtonicMobileReport.LoginAction"
            const val ACTION_LOGIN_SUCCESS : String = "com.magtonic.MagtonicMobileReport.LoginSuccess"
            const val ACTION_LOGIN_FAILED : String = "com.magtonic.MagtonicMobileReport.LoginFailed"
            const val ACTION_LOGIN_NETWORK_ERROR : String = "com.magtonic.MagtonicMobileReport.LoginNetworkError"
            const val ACTION_LOGIN_SERVER_ERROR : String = "com.magtonic.MagtonicMobileReport.LoginServerError"
            const val ACTION_NETWORK_FAILED : String = "com.magtonic.MagtonicMobileReport.ActionNetworkFailed"

            const val ACTION_LOGOUT_ACTION : String = "com.magtonic.MagtonicMobileReport.LogoutAction"
            const val ACTION_HIDE_KEYBOARD : String = "com.magtonic.MagtonicMobileReport.HideKeyboardAction"

            const val ACTION_WEBVIEW_GOBACK : String = "com.magtonic.MagtonicMobileReport.WebViewGoBack"
            const val ACTION_WEBVIEW_CANNOT_GOBACK : String = "com.magtonic.MagtonicMobileReport.WebViewCannotGoBack"
        }

    }
}