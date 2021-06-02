package com.xbaimiao.taboolib.gpay

import org.bukkit.Bukkit
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection

fun Runnable.async() {
    Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, this)
}

fun Runnable.sync() {
    Bukkit.getScheduler().runTask(Main.plugin, this)
}
