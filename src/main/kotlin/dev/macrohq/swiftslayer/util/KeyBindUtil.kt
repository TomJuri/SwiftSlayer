package dev.macrohq.swiftslayer.util

import cc.polyfrost.oneconfig.utils.dsl.runAsync
import dev.macrohq.swiftslayer.mixin.MinecraftInvoker

object KeyBindUtil {

    private var leftClicking = false
    private var rightClicking = false
    
    fun leftClick() {
        (mc as MinecraftInvoker).invokeClickMouse()
    }

    fun leftClick(clicksPerSecond: Int) {
        leftClicking = true
        runAsync {
            while (leftClicking) {
                leftClick()
                Thread.sleep(1000 / clicksPerSecond.toLong())
            }
        }
    }

    fun rightClick() {
        (mc as MinecraftInvoker).invokeRightClickMouse()
    }

    fun rightClick(clicksPerSecond: Int) {
        rightClicking = true
        runAsync {
            while (rightClicking) {
                rightClick()
                Thread.sleep(1000 / clicksPerSecond.toLong())
            }
        }
    }

    fun stopClicking() {
        leftClicking = false
        rightClicking = false
    }

    fun jump() {
        gameSettings.keyBindJump.setPressed(true)
        runAsync {
            Thread.sleep(100)
            gameSettings.keyBindJump.setPressed(false)
        }
    }
}
