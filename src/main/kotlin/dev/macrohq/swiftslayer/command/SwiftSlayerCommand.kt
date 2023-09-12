package dev.macrohq.swiftslayer.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import dev.macrohq.swiftslayer.util.UngrabUtil
import dev.macrohq.swiftslayer.util.autoBatphone

@Command(value = "swiftslayer")
class SwiftSlayerCommand {

    var a = false

    @Main
    fun main() {
        if (!a) UngrabUtil.ungrabMouse() else UngrabUtil.regrabMouse()
        a = !a
    }

    @SubCommand
    fun testautobatphone() {
        autoBatphone.enable()
    }
}