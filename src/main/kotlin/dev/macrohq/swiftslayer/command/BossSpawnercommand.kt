package dev.macrohq.swiftslayer.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand
import dev.macrohq.swiftslayer.macro.BossSpawner
import dev.macrohq.swiftslayer.macro.MobKiller
import dev.macrohq.swiftslayer.util.*
import net.minecraft.entity.monster.EntityZombie

@Command(value = "bossspawner", aliases = ["bs", "bsp"])
class BossSpawnercommand {
    @Main
    fun main() {
        bossSpawner.enable(EntityZombie::class.java)
    }

    @SubCommand
    fun stop(){
        bossSpawner.disable()
    }
}