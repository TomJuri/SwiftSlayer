package dev.macrohq.swiftslayer.util

import net.minecraft.util.MouseHelper
import org.lwjgl.input.Mouse

object UngrabUtil {

    private var isUngrabbed = false
    private var oldMouseHelper: MouseHelper? = null
    private var doesGameWantUngrabbed = false

    fun ungrabMouse() {
        if (isUngrabbed) return
        gameSettings.pauseOnLostFocus = false
        if (oldMouseHelper == null) oldMouseHelper = mc.mouseHelper
        doesGameWantUngrabbed = !Mouse.isGrabbed()
        oldMouseHelper!!.ungrabMouseCursor()
        mc.inGameHasFocus = true
        mc.mouseHelper = object : MouseHelper() {
            override fun mouseXYChange() {}
            override fun grabMouseCursor() {
                doesGameWantUngrabbed = false
            }

            override fun ungrabMouseCursor() {
                doesGameWantUngrabbed = true
            }
        }
        isUngrabbed = true
    }

    fun regrabMouse() {
        if (!isUngrabbed) return
        mc.mouseHelper = oldMouseHelper
        if (!doesGameWantUngrabbed) mc.mouseHelper.grabMouseCursor()
        oldMouseHelper = null
        isUngrabbed = false
    }
}