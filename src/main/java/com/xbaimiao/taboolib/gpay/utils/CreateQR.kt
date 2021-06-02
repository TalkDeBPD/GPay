package com.xbaimiao.ks2Pay.deposit

import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.xbaimiao.taboolib.gpay.Main
import io.izzel.taboolib.TabooLibAPI
import io.izzel.taboolib.internal.xseries.XMaterial
import io.izzel.taboolib.util.item.ItemBuilder
import io.izzel.taboolib.util.item.Items
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import java.awt.image.BufferedImage

class CreateQR(string: String, private val type: DepositType) {

    val PAPI = TabooLibAPI.getPluginBridge()

    private val qrCodeWriter1 = QRCodeWriter()

    private val bitMatrix1: BitMatrix = qrCodeWriter1.encode(string, BarcodeFormat.QR_CODE, 128, 128)

    private fun toBufferedImage(bitMatrix: BitMatrix): BufferedImage {
        val width = bitMatrix.width
        val height = bitMatrix.height
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        for (i in 0 until width) {
            for (j in 0 until height) {
                image.setRGB(i, j, if (bitMatrix[i, j]) -0x1000000 else 0XFFFFFF)
            }
        }
        return image
    }

    companion object {
        private val packetPlayOutMapClass = Version.getNmsClass("PacketPlayOutMap")
        private val packetPlayOutSetSlotClass = Version.getNmsClass("PacketPlayOutSetSlot")
        private val itemStackClass = Version.getNmsClass("ItemStack")
        private val craftPlayerClass = Version.getObcClass("entity.CraftPlayer")
        private val craftItemStackClass = Version.getObcClass("inventory.CraftItemStack")
        private val craftMapViewClass = Version.getObcClass("map.CraftMapView")
        private val entityHumanClass = Version.getNmsClass("EntityHuman")
        private val containerClass = Version.getNmsClass("Container")
        private val entityPlayerClass = Version.getNmsClass("EntityPlayer")
        private val playerConnectionClass = Version.getNmsClass("PlayerConnection")
        private val packetClass = Version.getNmsClass("Packet")
        private val renderDataClass = Version.getObcClass("map.RenderData")
        private val mapViewClass = Class.forName("org.bukkit.map.MapView")

        val judgeItem by lazy {
            val paper = ItemBuilder(Material.PAPER)
                .name("§c扫码支付")
                .build()
            paper
        }
    }

    private val image: BufferedImage = toBufferedImage(bitMatrix1)

    private val renderer = object : MapRenderer() {
        var rendered = false
        override fun render(mapView: MapView, mapCanvas: MapCanvas, player: Player) {
            if (rendered)
                return
            mapCanvas.drawImage(0, 0, image)
            rendered = true
        }
    }

    private fun v1122MapId(): Short {
        val id = mapViewClass.getDeclaredMethod("getId").invoke(mapView)
        return (id as Number).toShort()
    }

    private lateinit var mapMeta: MapMeta

    private val mapView: MapView by lazy {
        val mapView = Bukkit.createMap(Bukkit.getWorlds()[0])
        mapView.addRenderer(renderer)
        mapView
    }

    private val mapItem by lazy {
        val mapName = if (Version.isAfter(Version.v1_13)) "FILLED_MAP" else "MAP"
        val map = if (Version.isAfter(Version.v1_13)) {
            ItemBuilder(XMaterial.valueOf(mapName))
                .name("§6${type.typeName} §5扫码支付")
                .lore(
                    ArrayList<String>().also {
                        it.add("§6取消支付请丢弃地图")
                    }
                )
                .build()
        } else ItemBuilder(ItemStack(Items.asMaterial(mapName)!!, 1, v1122MapId()))
            .name("§6${type.typeName} §5扫码支付")
            .lore(
                ArrayList<String>().also {
                    it.add("§6取消支付请丢弃地图")
                }
            )
            .build()
        val mapMeta = map.itemMeta as MapMeta
        this.mapMeta = mapMeta
        if (Version.isAfter(Version.v1_13)) {
            mapMeta.mapView = mapView //1.12.2不能用
        }
        map.itemMeta = mapMeta
        map
    }

    private fun sendPacket(player: Player, packet: Any) {
        val employer: Any = craftPlayerClass.getDeclaredMethod("getHandle").invoke(player)
        val playerConnection = entityPlayerClass.getDeclaredField("playerConnection").get(employer)
        playerConnectionClass.getDeclaredMethod("sendPacket", packetClass).invoke(playerConnection, packet)
    }

    fun sendMap(player: Player) {
        if (player.inventory.itemInMainHand.type == Material.AIR) {
            player.inventory.setItemInMainHand(judgeItem)
        }
        Bukkit.getScheduler().runTaskLater(Main.plugin, Runnable {
            val employer: Any = craftPlayerClass.getDeclaredMethod("getHandle").invoke(player)
            val defaultContainer = entityHumanClass.getDeclaredField("defaultContainer").get(employer)
            val windowId = containerClass.getDeclaredField("windowId").get(defaultContainer)
            val emItemStack =
                craftItemStackClass.getDeclaredMethod("asNMSCopy", ItemStack::class.java).invoke(null, mapItem)
            val itemPacket = packetPlayOutSetSlotClass
                .getConstructor(Int::class.java, Int::class.java, itemStackClass)
                .newInstance(windowId, getMainHandSlot(player), emItemStack)
            sendPacket(player, itemPacket)

            val buffer = renderDataClass.getDeclaredField("buffer")
                .get(craftMapViewClass.getDeclaredMethod("render", craftPlayerClass).invoke(mapView, player))

            if (Version.isAfter(Version.v1_14)) {
                val mapPacket = packetPlayOutMapClass
                    .getConstructor(
                        Int::class.java,
                        Byte::class.java,
                        Boolean::class.java,
                        Boolean::class.java,
                        Collection::class.java,
                        ByteArray::class.java,
                        Int::class.java,
                        Int::class.java,
                        Int::class.java,
                        Int::class.java
                    ).newInstance(
                        mapMeta.mapId,
                        mapView.scale.value,
                        false, false,
                        ArrayList<Any>(),
                        buffer,
                        0, 0, 128, 128
                    )
                sendPacket(player, mapPacket)
                return@Runnable
            }
            if (Version.isAfter(Version.v1_12)) {
                val mapPacket = packetPlayOutMapClass
                    .getConstructor(
                        Int::class.java,
                        Byte::class.java,
                        Boolean::class.java,
                        Collection::class.java,
                        ByteArray::class.java,
                        Int::class.java,
                        Int::class.java,
                        Int::class.java,
                        Int::class.java
                    )
                val packet: Any
                if (Version.isAfter(Version.v1_13)) {
                    packet = mapPacket.newInstance(
                        mapMeta.mapId,
                        mapView.scale.value,
                        false,
                        ArrayList<Any>(),
                        buffer,
                        0, 0, 128, 128
                    )
                } else {
                    packet = mapPacket.newInstance(
                        v1122MapId().toInt(),
                        mapView.scale.value,
                        false,
                        ArrayList<Any>(),
                        buffer,
                        0, 0, 128, 128
                    )
                }
                sendPacket(player, packet)
                return@Runnable
            }
        }, 3L)
    }

    private fun getMainHandSlot(player: Player): Int {
        return player.inventory.heldItemSlot + 36
    }

}