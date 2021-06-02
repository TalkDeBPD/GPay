package com.xbaimiao.ks2Pay.callback

import com.xbaimiao.ks2Pay.deposit.Callback
import com.xbaimiao.ks2Pay.deposit.Deposit
import com.xbaimiao.ks2Pay.deposit.DepositType
import org.bukkit.entity.Player

object AliPay : Callback {

    override fun run(type: DepositType, price: Double, player: Player, deposit: Deposit) {
        WXPay.run(type, price, player, deposit)
    }

}