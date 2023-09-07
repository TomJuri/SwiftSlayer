package dev.macrohq.swiftslayer.util

import cc.polyfrost.oneconfig.utils.dsl.runAsync
import dev.macrohq.swiftslayer.mixin.MinecraftInvoker

object KeyBindUtil {

    private var cpsReset = 0L
    private var leftCps = 0
    private var rightCps = 0

    fun leftClick() {
        if(System.currentTimeMillis() - cpsReset > 1000) {
            cpsReset = System.currentTimeMillis()
            leftCps = 0
            rightCps = 0
        }
        if(leftCps > 12) return
        (mc as MinecraftInvoker).invokeClickMouse()
        leftCps++
    }

    fun rightClick() {
        if(System.currentTimeMillis() - cpsReset > 1000) {
            cpsReset = System.currentTimeMillis()
            leftCps = 0
            rightCps = 0
        }
        if(rightCps > 12) return
        (mc as MinecraftInvoker).invokeRightClickMouse()
        rightCps++
    }

    fun jump() {
        gameSettings.keyBindJump.setPressed(true)
        runAsync {
            Thread.sleep(100)
            gameSettings.keyBindJump.setPressed(false)
        }
    }
}
