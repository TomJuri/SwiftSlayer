package dev.macrohq.swiftslayer.util

import net.minecraft.util.ChatComponentText

object Logger {
    fun info(message: String) {
        send("a$message")
    }

    fun error(message: String) {
        send("c$message")
    }

    private fun send(message: String) {
        player.addChatMessage(ChatComponentText("§aSwift§bSlayer §8» §$message"))
    }
}
