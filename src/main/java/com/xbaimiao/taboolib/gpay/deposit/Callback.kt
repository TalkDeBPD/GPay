package com.xbaimiao.taboolib.gpay.deposit

import com.xbaimiao.taboolib.gpay.deposit.Deposit
import com.xbaimiao.taboolib.gpay.deposit.DepositType
import org.bukkit.entity.Player

interface Callback {

    fun run(type: DepositType, price: Double, player: Player, deposit: Deposit)

}