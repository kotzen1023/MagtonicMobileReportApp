package com.magtonic.magtonicmobilereportapp.model.sys

class User {
    companion object {
        const val USER_ACCOUNT = "userAccount"
        const val PASSWORD = "password"
    }

    var userAccount: String = ""
    var password: String = ""
    var isLogin = false
}