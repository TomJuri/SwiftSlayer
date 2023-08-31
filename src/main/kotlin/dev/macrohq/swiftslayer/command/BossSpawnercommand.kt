package dev.macrohq.swiftslayer.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import dev.macrohq.swiftslayer.util.*
import net.minecraft.init.Blocks
import net.minecraft.util.MovingObjectPosition


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