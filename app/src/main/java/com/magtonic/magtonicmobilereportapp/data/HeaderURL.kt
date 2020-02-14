package com.magtonic.magtonicmobilereportapp.data

class HeaderURL(header: String, urlString: String) {
    private var header: String = ""
    private var urlString: String = ""

    init {
        this.header = header
        this.urlString = urlString
    }

    fun getHeader(): String {
        return header
    }

    fun setHeader(header: String) {
        this.header = header
    }

    fun getUrlString(): String {
        return urlString
    }

    fun setUrlString(urlString: String) {
        this.urlString = urlString
    }
}