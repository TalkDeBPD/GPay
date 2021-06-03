package com.xbaimiao.taboolib.gpay

import com.xbaimiao.taboolib.gpay.callback.WXPay
import com.xbaimiao.taboolib.gpay.deposit.Deposit
import com.xbaimiao.taboolib.gpay.deposit.DepositType
import io.izzel.taboolib.TabooLibAPI
import io.izzel.taboolib.module.command.base.*
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@BaseCommand(name = "gpay")
class Command : BaseMainCommand() {

    val bridge = TabooLibAPI.getPluginBridge()

    @SubCommand(type = CommandType.PLAYER)
    val wxpay = object : BaseSubCommand() {

        override fun getDescription(): String {
            return "微信充值指定金额"
        }

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("金额"))
        }

        override fun onCommand(sender: CommandSender, cmd: Command, name: String, args: Array<out String>) {
            val player = sender as Player
            if (args[0].contains(".")) {
                player.sendMessage(
                    Main.prefix + bridge.setPlaceholders(
                        player,
                        Main.config.getString("message.noInt")!!
                    )
                )
                return
            }
            val money: Double
            try {
                money = args[0].toDouble()
            } catch (e: NumberFormatException) {
                player.sendMessage(Main.prefix + "§c金额输入有误")
                return
            }
            if (money > 1000.0) {
                player.sendMessage(Main.prefix + "§c单笔充值最高1000元")
                return
            }
            player.sendMessage(
                Main.prefix + bridge.setPlaceholders(player, Main.config.getString("message.create")!!)
                    .replace("{0}", "微信")
            )
            val deposit = Deposit(DepositType.WX, money, player, WXPay)

            deposit.oKRun {
                deposit.qr.sendMap(player)
                Listeners.addPayPlayer(player)
                player.sendMessage(
                    Main.prefix + bridge.setPlaceholders(
                        player,
                        Main.config.getString("message.createFinish")!!
                    )
                )
            }

        }

    }

    @SubCommand(type = CommandType.PLAYER)
    val alipay = object : BaseSubCommand() {
        override fun getDescription(): String {
            return "支付宝充值指定金额"
        }

        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("金额"))
        }

        override fun onCommand(sender: CommandSender, cmd: Command, name: String, args: Array<out String>) {
            val player = sender as Player
            if (args[0].contains(".")) {
                player.sendMessage(
                    Main.prefix + bridge.setPlaceholders(
                        player,
                        Main.config.getString("message.noInt")!!
                    )
                )
                return
            }
            val money: Double
            try {
                money = args[0].toDouble()
            } catch (e: NumberFormatException) {
                player.sendMessage(Main.prefix + "§c金额输入有误")
                return
            }
            if (money > 1000.0) {
                player.sendMessage(Main.prefix + "§c单笔充值最高1000元")
                return
            }
            player.sendMessage(
                Main.prefix + bridge.setPlaceholders(player, Main.config.getString("message.create")!!)
                    .replace("{0}", "支付宝")
            )
            val deposit = Deposit(DepositType.ALIPAY, money, player, WXPay)

            deposit.oKRun {
                deposit.qr.sendMap(player)
                Listeners.addPayPlayer(player)
                player.sendMessage(
                    Main.prefix + bridge.setPlaceholders(
                        player,
                        Main.config.getString("message.createFinish")!!
                    )
                )
            }

        }

    }

}