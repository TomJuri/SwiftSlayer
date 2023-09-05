package dev.macrohq.swiftslayer.macro

abstract class Macro {
    abstract fun run()
    abstract fun onEnable()
    abstract fun onDisable()
}