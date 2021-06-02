package com.xbaimiao.taboolib.gpay

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

    val prefix get() = config.getStringColored("message.prefix")

    override fun onEnable() {
        if (Version.isBefore(Version.v1_12)) {
            plugin.logger.warning("本插件不支持1.11.x及以下版本")
            onDisable()
            return
        }
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