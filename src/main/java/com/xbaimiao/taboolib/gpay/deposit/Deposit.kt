package com.xbaimiao.taboolib.gpay.deposit

import com.lly835.bestpay.model.PayResponse
import com.xbaimiao.taboolib.gpay.Main
import com.xbaimiao.taboolib.gpay.utils.CreateQR
import com.xbaimiao.taboolib.gpay.utils.Trade
import com.xbaimiao.taboolib.gpay.utils.async
import io.izzel.taboolib.TabooLibAPI
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class Deposit(
    val type: DepositType,
    val price: Double,
    val player: Player,
    private val callback: Callback,
) {


    private val bridge = TabooLibAPI.getPluginBridge()
    private val maxTime = 15 * 60
    private lateinit var queryTask: BukkitRunnable
    private lateinit var orderId: String
    lateinit var qr: CreateQR
    private var loadSuccess = false
    lateinit var response: PayResponse

    init {
        Runnable {
            response = when (type) {
                DepositType.ALIPAY -> Trade.aliNative("余额充值", price)
                DepositType.WX -> Trade.wxNative("余额充值", price)
                else -> Trade.wxNative("余额充值", price)
            }
            if (response.codeUrl == null) {
                player.sendMessage(
                    Main.prefix + bridge.setPlaceholders(
                        player,
                        Main.config.getString("message.fail")!!
                    )
                )
                return@Runnable
            }
            orderId = response.orderId
            qr = CreateQR(response.codeUrl, type)
            queryTask = object : BukkitRunnable() {

                private var paySuccess = false

                var timer = 0

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

    private fun query(): Boolean {
        return when (type) {
            DepositType.ALIPAY -> return Trade.aliQuery(orderId)
            DepositType.WX -> return Trade.wxQuery(orderId)
            else -> false
        }
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


}