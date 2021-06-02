package com.xbaimiao.taboolib.gpay.utils

import com.lly835.bestpay.config.AliPayConfig
import com.lly835.bestpay.config.WxPayConfig
import com.lly835.bestpay.service.impl.BestPayServiceImpl
import io.izzel.taboolib.module.config.TConfig
import java.io.File

object Pay {

    private val tConfig = TConfig.create(File("src\\main\\resources\\key.yml"))

    val wxPayConfig: WxPayConfig = object : WxPayConfig() {
        init {
            val section = tConfig.getConfigurationSection("pay_wx")!!
            appId = section.getString("appid")
            mchId = section.getString("mchid")
            mchKey = section.getString("mchKey")
            appSecret = section.getString("appSecret")
            notifyUrl = section.getString("notifyUrl")
            keyPath = section.getString("keyPath")
        }
    }
    val aliPayConfig: AliPayConfig = object : AliPayConfig() {
        init {
            val section = tConfig.getConfigurationSection("pay_ali")!!
            appId = section.getString("appid")
            val private = StringBuilder().also {
                for (s in section.getStringList("privateKey")) {
                    it.append(s)
                }
            }
            privateKey = privateKey.toString()
            val public = StringBuilder().also {
                for (s in section.getStringList("aliPayPublicKey")) {
                    it.append(s)
                }
            }
            aliPayPublicKey = public.toString()
        }
    }

    val bestPayService = BestPayServiceImpl()

    init {
        bestPayService.setWxPayConfig(wxPayConfig)
        bestPayService.setAliPayConfig(aliPayConfig)
    }

}