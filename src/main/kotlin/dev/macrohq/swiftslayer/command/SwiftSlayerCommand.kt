package dev.macrohq.swiftslayer.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import dev.macrohq.swiftslayer.util.UnGrabUtil
import dev.macrohq.swiftslayer.util.autoBatphone

@Command(value = "swiftslayer")
class SwiftSlayerCommand {

    var a = false

    @Main
    fun main() {
      if (!a) UnGrabUtil.unGrabMouse() else UnGrabUtil.grabMouse()
        a = !a
    }

    @SubCommand
    fun testautobatphone() {
        autoBatphone.enable()
    }
}