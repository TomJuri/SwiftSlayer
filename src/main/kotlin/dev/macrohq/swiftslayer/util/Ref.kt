package dev.macrohq.swiftslayer.util

import dev.macrohq.swiftslayer.SwiftSlayer
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
    get() = SwiftSlayer.instance
val gameSettings
    get() = mc.gameSettings
val config
    get() = swiftSlayer.config
val pathExecutor
    get() = swiftSlayer.pathExecutor
val mobKiller
    get() = swiftSlayer.mobKiller
val autoBatphone
    get() = swiftSlayer.autoBatphone
val macroManager
    get() = swiftSlayer.macroManager
val genericBossKiller
    get() = swiftSlayer.genericBossKiller
val endermanBossKiller
    get() = swiftSlayer.endermanBossKiller
val revenant
    get() = swiftSlayer.revenant
val tracker
    get() = swiftSlayer.tracker