package dev.macrohq.swiftslayer.util

import cc.polyfrost.oneconfig.utils.dsl.runAsync
import dev.macrohq.swiftslayer.mixin.MinecraftInvoker
import kotlin.random.Random

object KeyBindUtil {

    private var leftClicking = false
    private var rightClicking = false
    
    fun leftClick() {
        (mc as MinecraftInvoker).invokeClickMouse()
    }

    fun leftClick(clicksPerSecond: Int) {
        if (leftClicking) return
        leftClicking = true
        runAsync {
            while (leftClicking) {
                leftClick()
              Thread.sleep(900 / clicksPerSecond.toLong() + Random.nextLong(0, 100))
            }
        }
    }

    fun rightClick() {
        (mc as MinecraftInvoker).invokeRightClickMouse()
    }

    fun rightClick(clicksPerSecond: Int) {
        if (rightClicking) return
        rightClicking = true
        runAsync {
            while (rightClicking) {
                rightClick()
              Thread.sleep(900 / clicksPerSecond.toLong() + Random.nextLong(0, 100))
            }
        }
    }

    fun stopClicking() {
        leftClicking = false
        rightClicking = false
    }

    fun jump() {
        if (!player.onGround) return
        gameSettings.keyBindJump.setPressed(true)
        runAsync {
            Thread.sleep(100)
            gameSettings.keyBindJump.setPressed(false)
        }
    }
}
