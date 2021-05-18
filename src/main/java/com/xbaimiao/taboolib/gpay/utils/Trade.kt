package com.xbaimiao.taboolib.gpay.utils

import java.util.Calendar.*

object Trade {

    @JvmStatic
    val storeId = "001"

    @JvmStatic
    val operatorId = "xbaimiao"

    @JvmStatic
    fun createOutTradeNo(): String {
        val calendar = getInstance()
        return "${calendar.get(YEAR)}${calendar.get(MONTH) + 1}${calendar.get(DATE)}" + (System.currentTimeMillis() + (Math.random() * 10000000L).toLong()).toString()
    }

}