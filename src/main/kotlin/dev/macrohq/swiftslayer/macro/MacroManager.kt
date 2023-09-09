package dev.macrohq.swiftslayer.macro

import dev.macrohq.swiftslayer.util.Logger
import dev.macrohq.swiftslayer.util.config

object MacroManager {

    private var enabled = false

    fun toggle() {
        if (!enabled) {
            enabled = true
        } else {
            enabled = false
        }
    }

    private fun enable() {
        if (enabled) return
        if (config.slayer != 0 && config.slayerTier == 4) {
            Logger.error("There's no tier 5 boss for this slayer.")
            return
        }
        enabled = true
    }

    private fun disable() {
        if (!enabled) return
    }
}