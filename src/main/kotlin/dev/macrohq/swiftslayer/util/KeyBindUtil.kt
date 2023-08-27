package dev.macrohq.swiftslayer.util

import dev.macrohq.swiftslayer.mixin.MinecraftInvoker

object KeyBindUtil {
    fun leftClick() { (mc as MinecraftInvoker).invokeClickMouse() }
    fun rightClick() { (mc as MinecraftInvoker).invokeRightClickMouse() }
}
