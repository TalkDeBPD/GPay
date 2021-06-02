package com.xbaimiao.ks2Pay

import com.xbaimiao.ks2Pay.listeners.QR
import io.izzel.taboolib.Version
import io.izzel.taboolib.loader.Plugin
import io.izzel.taboolib.module.config.TConfig
import io.izzel.taboolib.module.inject.TInject
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.net.URL

object Main : Plugin() {

    lateinit var main: Main

    @TInject(value = ["config.yml"])
    lateinit var config: TConfig
        private set
    lateinit var prefix: String
    lateinit var id: String
    lateinit var key: String
    private val titleUrl = "https://qr.ks2.xyz/gg.html"

    private val version = Https.sendGet(URL("https://qr.ks2.xyz/version.txt"))

    override fun onEnable() {
        if (Version.isBefore(Version.v1_12)) {
            plugin.logger.warning("本插件不支持1.11.x及以下版本")
            onDisable()
            return
        }
        main = this
        startTitle()
        prefix = config.getStringColored("message.prefix")
        id = config.getString("id")!!
        key = config.getString("key")!!
    }

    private fun startTitle() {
        Runnable {
            val data = Https.sendGet(URL(titleUrl))
            if (data == "") {
                return@Runnable
            }
            val list = data.replace("&", "§").split("\\n").toList()
            list.forEach {
                plugin.logger.info(it)
            }
        }.async()
    }

    override fun onDisable() {
        Bukkit.getOnlinePlayers().forEach { player ->
            if (player in QR.payList) {
                if (player.inventory.itemInMainHand.itemMeta?.displayName == "§c扫码支付") {
                    player.inventory.setItemInMainHand(ItemStack(Material.AIR))
                }
                QR.payList.remove(player)
            }
        }
    }
}