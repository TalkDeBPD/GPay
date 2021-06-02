package com.xbaimiao.ks2Pay.deposit

import org.bukkit.entity.Player

interface Callback {

    fun run(type: DepositType, price: Double, player: Player, deposit: Deposit)

}