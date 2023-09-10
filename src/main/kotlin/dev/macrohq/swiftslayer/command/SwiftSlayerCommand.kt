package dev.macrohq.swiftslayer.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import dev.macrohq.swiftslayer.util.config

@Command(value = "swiftslayer")
class SwiftSlayerCommand {
    @Main
    fun main() {
        config.openGui()
    }
}