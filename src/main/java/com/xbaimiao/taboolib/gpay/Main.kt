package com.xbaimiao.taboolib.gpay

import com.xbaimiao.taboolib.gpay.utils.Service
import io.izzel.taboolib.Version
import io.izzel.taboolib.loader.Plugin
import io.izzel.taboolib.module.config.TConfig
import io.izzel.taboolib.module.inject.TInject
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object Main : Plugin() {

    @TInject(value = ["config.yml"])
    lateinit var config: TConfig
        private set

    @TInject(value = ["key.yml"])
    lateinit var key: TConfig
        private set

    @TInject(value = ["transaction.yml"])
    lateinit var transaction: TConfig
        private set

    val prefix get() = config.getStringColored("message.prefix")

    override fun onEnable() {
        if (Version.isBefore(Version.v1_12)) {
            plugin.logger.warning("本插件不支持1.11.x及以下版本")
            onDisable()
            return
        }
        println(Service.bestPayService)
        Bukkit.getScheduler()
            .runTaskTimerAsynchronously(this.plugin, Runnable { transaction.saveToFile() }, 20 * 30, 20 * 30)
    }

    override fun onDisable() {
        transaction.saveToFile()
        Bukkit.getOnlinePlayers().forEach { player ->
            if (player in Listeners.payList) {
                if (player.inventory.itemInMainHand.itemMeta?.displayName == "§c扫码支付") {
                    player.inventory.setItemInMainHand(ItemStack(Material.AIR))
                }
                Listeners.payList.remove(player)
            }
        }
    }

}