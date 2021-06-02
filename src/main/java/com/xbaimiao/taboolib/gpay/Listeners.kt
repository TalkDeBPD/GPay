package com.xbaimiao.taboolib.gpay

import io.izzel.taboolib.module.inject.TListener
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack

@TListener
object Listeners : Listener {

    val payList = ArrayList<Player>()

    fun addPayPlayer(player: Player) {
        if (player in payList) {
            return
        }
        payList.add(player)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun held(event: PlayerItemHeldEvent) {
        val player = event.player
        if (player in payList) {
            event.isCancelled = true
            player.sendMessage(Main.prefix + "请先完成支付,如需取消请丢弃地图...")
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun pick(event: PlayerPickupItemEvent) {
        val player = event.player
        if (player in payList) {
            event.isCancelled = true
            player.sendMessage(Main.prefix + "请先完成支付,如需取消请丢弃地图...")
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun drop(event: PlayerDropItemEvent) {
        val item = event.itemDrop.itemStack
        if (event.player.inventory.itemInMainHand.type == Material.AIR || item.itemMeta?.displayName == "§c扫码支付") {
            if (event.player in payList) {
                val player = event.player
                payList.remove(player)
                if (item.itemMeta?.displayName == "§c扫码支付") {
                    event.itemDrop.remove()
                    return
                }
                event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun quit(event: PlayerQuitEvent) {
        val player = event.player
        if (player in payList) {
            if (player.inventory.itemInMainHand.itemMeta?.displayName == "§c扫码支付") {
                player.inventory.setItemInMainHand(ItemStack(Material.AIR))
            }
            payList.remove(player)
        }
    }

}