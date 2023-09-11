package dev.macrohq.swiftslayer.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import dev.macrohq.swiftslayer.util.UngrabUtil

@Command(value = "swiftslayer")
class SwiftSlayerCommand {

    var a = false

    @Main
    fun main() {
        if (!a) UngrabUtil.ungrabMouse() else UngrabUtil.regrabMouse()
        a = !a
    }
}