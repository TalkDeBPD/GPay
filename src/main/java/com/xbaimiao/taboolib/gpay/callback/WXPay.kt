package com.xbaimiao.taboolib.gpay.callback

import com.xbaimiao.taboolib.gpay.Listeners
import com.xbaimiao.taboolib.gpay.Main
import com.xbaimiao.taboolib.gpay.deposit.Callback
import com.xbaimiao.taboolib.gpay.deposit.Deposit
import com.xbaimiao.taboolib.gpay.deposit.DepositType
import com.xbaimiao.taboolib.gpay.utils.sync
import io.izzel.taboolib.TabooLibAPI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object WXPay : Callback {

    private val PAPI = TabooLibAPI.getPluginBridge()

    override fun run(type: DepositType, price: Double, player: Player, deposit: Deposit) {
        player.sendMessage(
            Main.prefix + PAPI.setPlaceholders(player, Main.config.getString("message.payFinish")!!)
                .replace("{1}", "$price").replace("{0}", type.typeName)
        )
        val money = Main.config.getString("setting.exchange")!!.toInt() * price.toInt()
        for (command in Main.config.getStringList("setting.commands")) {
            try {
                Runnable {
                    Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        PAPI.setPlaceholders(player, command.replace("%player_name%", player.name))
                            .replace("{0}", "$money")
                    )
                }.sync()
            } catch (e: Exception) {
                continue
            }
        }
        if (player in Listeners.payList) {
            Listeners.payList.remove(player)
            if (player.inventory.itemInMainHand.itemMeta?.displayName == "§c扫码支付") {
                player.inventory.setItemInMainHand(ItemStack(Material.AIR))
            }
        }
        player.updateInventory()
    }

}