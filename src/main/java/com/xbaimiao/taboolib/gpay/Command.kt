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
            if (!args[0].matches(Regex("[0-9]+"))){
                player.sendMessage(Main.prefix + "§c请输入正确的数字")
                return
            }
            val money = args[0].toDouble()
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
            if (!args[0].matches(Regex("[0-9]+"))){
                player.sendMessage(Main.prefix + "§c请输入正确的数字")
                return
            }
            val money = args[0].toDouble()
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

    override fun onTabComplete(sender: CommandSender, command: String, argument: String): MutableList<String>? {
        if (argument == "金额"){
            return arrayListOf("1","6","12","30","64","128","200","328","648")
        }
        return null
    }

}