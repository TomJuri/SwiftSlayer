package dev.macrohq.swiftslayer.feature

import net.minecraft.util.StringUtils
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class Tracker {

  private var startTime = 0L
  private var totalBosses = 0
  private var xpNeededForNextLevel = 0
  private var xpPerBoss = 0
  private var totalXp = 0

  fun getTotalXP() = totalXp
  fun getXPPerHour() = getBossesPerHour() * xpPerBoss
  fun getTotalBosses() = totalBosses
  fun getBossesPerHour() = (totalBosses * 3600000) / (System.currentTimeMillis() - startTime)
  fun getLevelUpIn(): String {
    val xpPerHour = getXPPerHour()
    if (xpPerHour.toInt() == 0 || xpNeededForNextLevel.toFloat() == 0f) return "00:00:00"
    val decimalHours = (xpNeededForNextLevel / xpPerHour.toFloat())
    val hours = decimalHours.toInt()
    val timeleft = decimalHours - hours
    val minutes = (timeleft * 60).toInt()
    val seconds = ((timeleft * 60 - minutes) * 60).toInt()
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
  }
  fun getTimeRunning(): String {
    val millis = System.currentTimeMillis() - startTime
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
  }

  fun reset() {
    startTime = System.currentTimeMillis()
    totalBosses = 0
    xpNeededForNextLevel = 0
    xpPerBoss = 0
    totalXp = 0
  }

  @SubscribeEvent
  fun onChatReceive(event: ClientChatReceivedEvent) {
    val message = StringUtils.stripControlCodes(event.message.unformattedText).replace(",", "")
    val regex = Regex("Next LVL in (\\d+) XP!")
    val matchResult = regex.find(message)
    if (matchResult != null) {
      val xpNeeded = matchResult.groupValues[1].toInt()
      if (xpNeededForNextLevel != 0)
        xpPerBoss = xpNeededForNextLevel - xpNeeded
      xpNeededForNextLevel = xpNeeded
      if (xpPerBoss != 0 && totalXp == 0)
        totalXp = xpPerBoss * 2
      else
        totalXp += xpPerBoss
      totalBosses++
    }
  }
}