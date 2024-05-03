package dev.macrohq.swiftslayer.util

import net.minecraft.util.MouseHelper
import org.lwjgl.input.Mouse

object UnGrabUtil {
  private var isUnGrabbed = false
    private var oldMouseHelper: MouseHelper? = null
  private var doesGameWantUnGrabbed = false

  fun unGrabMouse() {
    if (isUnGrabbed || !config.ungrabMouse) return
        gameSettings.pauseOnLostFocus = false
        if (oldMouseHelper == null) oldMouseHelper = mc.mouseHelper
    doesGameWantUnGrabbed = !Mouse.isGrabbed()
        oldMouseHelper!!.ungrabMouseCursor()
        mc.inGameHasFocus = true
        mc.mouseHelper = object : MouseHelper() {
            override fun mouseXYChange() {}
            override fun grabMouseCursor() {
              doesGameWantUnGrabbed = false
            }
            override fun ungrabMouseCursor() {
              doesGameWantUnGrabbed = true
            }
        }
    isUnGrabbed = true
    }

  fun grabMouse() {
    if (!isUnGrabbed) return
        mc.mouseHelper = oldMouseHelper
    if (!doesGameWantUnGrabbed) mc.mouseHelper.grabMouseCursor()
        oldMouseHelper = null
    isUnGrabbed = false
    }
}