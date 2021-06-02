package com.xbaimiao.ks2Pay.deposit

enum class DepositType(val key: String, val typeName: String) {
    WX("wxpay", "微信"), ALIPAY("alipay", "支付宝"), QQ("qqpay", "QQ")
}