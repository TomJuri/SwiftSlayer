package dev.macrohq.swiftslayer.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import dev.macrohq.swiftslayer.util.bossSpawner


@Command(value = "bossspawner", aliases = ["bs", "bsp"])
class BossSpawnercommand {
    @Main
    fun main() {

    }

    @SubCommand
    fun stop(){
        bossSpawner.disable()
    }
}