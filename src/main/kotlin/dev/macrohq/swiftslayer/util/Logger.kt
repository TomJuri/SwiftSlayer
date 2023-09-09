package dev.macrohq.swiftslayer.util

import net.minecraft.util.ChatComponentText

object Logger {
    fun info(message: Any) {
        send("a$message")
    }

    fun error(message: Any) {
        send("c$message")
    }

    fun log(message: Any) {
        send("7$message", true)
    }

    private fun send(message: String, debug: Boolean = false) {
        if (debug && !config.debugMode) return
        player.addChatMessage(ChatComponentText("§aSwift§bSlayer §8» §$message"))
    }
}
