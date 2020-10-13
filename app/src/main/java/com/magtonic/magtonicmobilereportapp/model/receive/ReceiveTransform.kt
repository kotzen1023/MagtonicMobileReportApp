package com.magtonic.magtonicmobilereportapp.model.receive

import android.util.Log

class ReceiveTransform {
    companion object {
        private val mTAG = ReceiveTransform::class.java.name
        //const val arrField : String = "dataList"
        fun addToJsonArrayStr(str: String): String {

            return "{\"dataList\":$str}"
        }

        fun restoreToJsonStr(str: String): String {
            /*var str = str


            //return  "{dataList:"+str + "}";
            str = str.substring(1, str.length - 1)
            return str*/
            //val jsonStr = str.substring(1, str.length - 1)

            return str.substring(1, str.length - 1)

        }

        fun restoreToJsonStr2(str: String): String {
            /*var str = str

            //return  "{dataList:"+str + "}";
            str = str.substring(1, str.length - 1)
            return str*/
            //val jsonStr = str.substring(1, str.length - 1)
            return str.substring(0, str.length)

        }

        fun removeArraySignal(str: String): String {
            var newString = str.replace("[", "")
            newString = newString.replace("]", "")

            return newString
        }

    }
}