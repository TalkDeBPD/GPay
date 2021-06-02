package com.xbaimiao.taboolib.gpay.deposit

import org.bukkit.entity.Player

interface Callback {

    fun run(type: DepositType, price: Double, player: Player, deposit: Deposit)

}