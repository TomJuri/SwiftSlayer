package dev.macrohq.swiftslayer.util.movement.helper.player

import dev.macrohq.swiftslayer.SwiftSlayer
import net.minecraft.client.Minecraft
import net.minecraft.util.BlockPos
import kotlin.math.ceil

class PlayerContext(val ss: SwiftSlayer, override val mc: Minecraft) : IPlayerContext {
  override val player get() = mc.thePlayer
  override val playerController get() = mc.playerController
  override val world get() = mc.theWorld

  // Block player is standing on
  // Todo: Change name
  override val playerPosition get() = BlockPos(player.posX, ceil(player.posY) - 1, player.posZ)
}