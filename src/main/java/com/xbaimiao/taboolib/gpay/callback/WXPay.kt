package com.xbaimiao.taboolib.gpay.callback

import com.xbaimiao.taboolib.gpay.Main
import com.xbaimiao.taboolib.gpay.deposit.Callback
import com.xbaimiao.taboolib.gpay.deposit.Deposit
import com.xbaimiao.taboolib.gpay.deposit.DepositType
import com.xbaimiao.taboolib.gpay.QR
import com.xbaimiao.taboolib.gpay.utils.sync
import io.izzel.taboolib.TabooLibAPI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object WXPay : Callback {

    val PAPI = TabooLibAPI.getPluginBridge()

    override fun run(type: DepositType, price: Double, player: Player, deposit: Deposit) {
        player.sendMessage(
            Main.prefix + PAPI.setPlaceholders(player, Main.config.getString("message.payFinish")!!)
                .replace("%price%", "$price").replace("%ks2pay_type%", type.typeName)
        )
        val money = Main.config.getString("setting.exchange")!!.toInt() * price.toInt()
        for (command in Main.config.getStringList("setting.commands")) {
            try {
                Runnable {
                    Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        PAPI.setPlaceholders(player, command.replace("%player_name%", player.name))
                            .replace("%ks2pay_money%", "$money")
                    )
                }.sync()
            } catch (e: Exception) {
                continue
            }
        }
        if (player in QR.payList) {
            QR.payList.remove(player)
            if (player.inventory.itemInMainHand.itemMeta?.displayName == "§c扫码支付") {
                player.inventory.setItemInMainHand(ItemStack(Material.AIR))
            }
        }
        player.updateInventory()
    }

}