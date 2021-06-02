package com.xbaimiao.ks2Pay.deposit

import com.google.gson.Gson
import com.xbaimiao.ks2Pay.Https
import com.xbaimiao.ks2Pay.Main
import com.xbaimiao.ks2Pay.async
import com.xbaimiao.ks2Pay.back.Order
import com.xbaimiao.ks2Pay.back.Query
import com.xbaimiao.ks2Pay.back.alipay.AliPayOrder
import com.xbaimiao.ks2Pay.back.alipay.AliPayQuery
import com.xbaimiao.ks2Pay.back.qq.QQOrder
import com.xbaimiao.ks2Pay.back.wxpay.WXPayOrder
import com.xbaimiao.ks2Pay.back.wxpay.WXPayQuery
import io.izzel.taboolib.TabooLibAPI
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.net.URL

class Deposit(
    val type: DepositType,
    val price: Double,
    val player: Player,
    private val callback: Callback,
    private val id: String,
    private val key: String
) {

    val PAPI = TabooLibAPI.getPluginBridge()

    private val url = when (type) {
        DepositType.WX -> "https://qr.ks2.xyz/qrcode.php?price=$price&type=${type.key}&user=${player.name}&id=$id&key=$key"
        DepositType.ALIPAY -> "https://qr.ks2.xyz/alipay/f2fpay/qr.php?subject=%E4%BD%99%E9%A2%9D%E5%85%85%E5%80%BC&total_amount=$price&id=$id&key=$key"
        else -> "null"
    }


    private val orderClass: Class<out Order> = when (type) {
        DepositType.WX -> WXPayOrder::class.java
        DepositType.ALIPAY -> AliPayOrder::class.java
        DepositType.QQ -> QQOrder::class.java
    }

    private val queryClass: Class<out Query> = when (type) {
        DepositType.WX -> WXPayQuery::class.java
        else -> AliPayQuery::class.java
    }

    private var paySuccess = false

    private val maxTime = 15 * 60
    private var timer = 0

    private lateinit var queryTask: BukkitRunnable

    private lateinit var order: Order

    lateinit var qr: CreateQR

    private val code = when (type) {
        DepositType.WX -> 1
        else -> 10000
    }

    var loadSuccess = false

    private fun query(): Boolean {
        if (order.getId() != id) {
            return false
        }

        val url = when (type) {
            DepositType.WX -> "https://qr.ks2.xyz/cx.php?tradeno=${order.getOrderNumber()}&id=$id&key=$key"
            DepositType.ALIPAY -> "https://qr.ks2.xyz/alipay/f2fpay/query.php?out_trade_no=${order.getOrderNumber()}&ks2id=$id&ks2key=$key"
            else -> "null"
        }
        val data = Https.sendGet(URL(url))
        if (data.contains("失败") || data.contains("关闭")) {
            return false
        }
        val gson = Gson()
        val query = gson.fromJson(data, queryClass)
        if (query.getId() != order.getId()) {
            return false
        }
        if (query.getCode() == code) {
            return true
        }
        return false
    }

    /**
     * 待充值参数加载完成后执行的代码
     */
    fun oKRun(runnable: Runnable) {
        object : BukkitRunnable() {
            override fun run() {
                if (loadSuccess) {
                    try {
                        runnable.run()
                    } catch (e: Exception) {

                    }
                    cancel()
                }
            }
        }.runTaskTimerAsynchronously(Main.plugin, 2, 2)
    }

    init {
        Runnable {
            val gson = Gson()
            val data = Https.sendGet(URL(url))
            if (data.contains("null")) {
                player.sendMessage(Main.prefix + "§4请填写正确的 §aID §4和 §aKEY")
                throw ExceptionInInitializerError("§4请填写正确的 §aID §4和 §aKEY")
            }
            order = gson.fromJson(data, orderClass)
            if (order.getCode() != code) {
                player.sendMessage(Main.prefix + PAPI.setPlaceholders(player, Main.config.getString("message.fail")!!))
                return@Runnable
            }
            qr = CreateQR(order.getQRUrl(), type)
            queryTask = object : BukkitRunnable() {
                override fun run() {
                    if (timer++ > maxTime) {
                        cancel()
                    }
                    if (query() && !paySuccess) {
                        paySuccess = true
                        callback.run(type, price, player, this@Deposit)
                        cancel()
                    }
                }
            }
            queryTask.runTaskTimerAsynchronously(Main.plugin, 20, 20)
            loadSuccess = true
        }.async()
    }


}