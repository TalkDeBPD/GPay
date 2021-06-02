package com.xbaimiao.taboolib.gpay.deposit

enum class DepositType(val key: String, val typeName: String) {
    WX("wxpay", "微信"), ALIPAY("alipay", "支付宝"), QQ("qqpay", "QQ")
}