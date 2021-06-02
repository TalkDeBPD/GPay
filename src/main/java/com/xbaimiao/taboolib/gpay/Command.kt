package com.xbaimiao.ks2Pay

import com.xbaimiao.taboolib.gpay.callback.WXPay
import com.xbaimiao.taboolib.gpay.deposit.Deposit
import com.xbaimiao.taboolib.gpay.deposit.DepositType
import com.xbaimiao.taboolib.gpay.QR
import com.xbaimiao.taboolib.gpay.Main
import io.izzel.taboolib.TabooLibAPI
import io.izzel.taboolib.module.command.base.*
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@BaseCommand(name = "ks2pay")
class Command : BaseMainCommand() {

    val PAPI = TabooLibAPI.getPluginBridge()

    @SubCommand(permission = "op")
    val reload = object : BaseSubCommand() {

        override fun getDescription(): String {
            return "重载插件"
        }

        override fun onCommand(sender: CommandSender, cmd: Command, name: String, args: Array<out String>) {
            Main.config.reload()
            Main.prefix = Main.config.getString("message.prefix")!!
            Main.id = Main.config.getString("id")!!
            Main.key = Main.config.getString("key")!!
            sender.sendMessage(Main.prefix + "插件重载完成!")
        }

    }

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
                player.sendMessage(Main.prefix + PAPI.setPlaceholders(player, Main.config.getString("message.noInt")!!))
                return
            }
            val money = args[0].toDouble()
            if (money > 1000.0) {
                player.sendMessage(Main.prefix + "§c单笔充值最高1000元")
                return
            }
            player.sendMessage(
                Main.prefix + PAPI.setPlaceholders(player, Main.config.getString("message.create")!!)
                    .replace("%ks2pay_type%", "微信")
            )
            val deposit = Deposit(DepositType.WX, money, player, WXPay, Main.id, Main.key)

            deposit.oKRun {
                deposit.qr.sendMap(player)
                QR.addPayPlayer(player)
                player.sendMessage(
                    Main.prefix + PAPI.setPlaceholders(
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
                player.sendMessage(Main.prefix + PAPI.setPlaceholders(player, Main.config.getString("message.noInt")!!))
                return
            }
            val money = args[0].toDouble()
            if (money > 1000.0) {
                player.sendMessage(Main.prefix + "§c单笔充值最高1000元")
                return
            }
            player.sendMessage(
                Main.prefix + PAPI.setPlaceholders(player, Main.config.getString("message.create")!!)
                    .replace("%ks2pay_type%", "支付宝")
            )
            val deposit = Deposit(DepositType.ALIPAY, money, player, WXPay, Main.id, Main.key)

            deposit.oKRun {
                deposit.qr.sendMap(player)
                QR.addPayPlayer(player)
                player.sendMessage(
                    Main.prefix + PAPI.setPlaceholders(
                        player,
                        Main.config.getString("message.createFinish")!!
                    )
                )
            }

        }

    }

}