package com.xbaimiao.taboolib.gpay.utils

import com.lly835.bestpay.config.AliPayConfig
import com.lly835.bestpay.config.WxPayConfig
import com.lly835.bestpay.service.impl.BestPayServiceImpl
import com.xbaimiao.taboolib.gpay.Main
import java.io.File

object Service {

    private val wxPayConfig: WxPayConfig = object : WxPayConfig() {
        init {
            val section = Main.key.getConfigurationSection("pay_wx")!!
            appId = section.getString("appid")
            mchId = section.getString("mchid")
            mchKey = section.getString("mchKey")
            appSecret = section.getString("appSecret")
            notifyUrl = section.getString("notifyUrl")
            keyPath = Main.plugin.dataFolder.path + File.separator + section.getString("keyPath")
        }
    }

    private val aliPayConfig: AliPayConfig = object : AliPayConfig() {
        init {
            val section = Main.key.getConfigurationSection("pay_ali")!!
            appId = section.getString("appid")
            val private = StringBuilder().also {
                for (s in section.getStringList("privateKey")) {
                    it.append(s)
                }
            }
            privateKey = private.toString()
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