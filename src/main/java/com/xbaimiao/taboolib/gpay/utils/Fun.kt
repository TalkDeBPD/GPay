package com.xbaimiao.taboolib.gpay.utils

import com.xbaimiao.taboolib.gpay.Main
import org.bukkit.Bukkit

fun Runnable.async() {
    Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, this)
}

fun Runnable.sync() {
    Bukkit.getScheduler().runTask(Main.plugin, this)
}
