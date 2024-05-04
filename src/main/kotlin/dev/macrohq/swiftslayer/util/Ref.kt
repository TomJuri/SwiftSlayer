package dev.macrohq.swiftslayer.util

import dev.macrohq.swiftslayer.SwiftSlayer
import dev.macrohq.swiftslayer.macro.mobKillers.RevMobKiller
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.Tessellator

val mc
    get() = Minecraft.getMinecraft()
val player
    get() = mc.thePlayer
val world
    get() = mc.theWorld
val tessellator
    get() = Tessellator.getInstance()
val swiftSlayer
    get() = SwiftSlayer
val gameSettings
    get() = mc.gameSettings
val config
    get() = swiftSlayer.config
val pathExecutor
    get() = swiftSlayer.pathExecutor
val mobKiller
    get() = when(SwiftSlayer.config.slayer) {
            0 -> {
                RevMobKiller
            }

        else -> {
            RevMobKiller
        }
    }
val autoBatphone
    get() = swiftSlayer.autoBatphone
val macroManager
    get() = swiftSlayer.macroManager
val genericBossKiller
    get() = RevMobKiller.getInstance()
val endermanBossKiller
    get() = swiftSlayer.endermanBossKiller
val revenant
    get() = swiftSlayer.revenant
val tracker
    get() = swiftSlayer.tracker