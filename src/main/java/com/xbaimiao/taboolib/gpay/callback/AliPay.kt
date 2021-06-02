package com.xbaimiao.taboolib.gpay.callback

import com.xbaimiao.taboolib.gpay.deposit.Callback
import com.xbaimiao.taboolib.gpay.deposit.Deposit
import com.xbaimiao.taboolib.gpay.deposit.DepositType
import org.bukkit.entity.Player

object AliPay : Callback {

    override fun run(type: DepositType, price: Double, player: Player, deposit: Deposit) {
        WXPay.run(type, price, player, deposit)
    }

}