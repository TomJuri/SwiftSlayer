package dev.macrohq.swiftslayer.util

import net.minecraft.util.ChatComponentText

object Logger {
  private var lastDebugMessage = ""

  fun info(message: Any) {
    this.send("a§l$message")
  }

  fun note(message: Any) {
    this.send("e$message")
  }

  fun error(message: Any) {
    this.send("c$message")
  }

  fun log(message: Any) {
    if (!config.debugMode || message == this.lastDebugMessage) return
    this.lastDebugMessage = message.toString()
    this.send("7$message")
  }
  private fun send(message: String) {
    player.addChatMessage(ChatComponentText("§aSwift§bSlayer §8» §$message"))
  }
}
