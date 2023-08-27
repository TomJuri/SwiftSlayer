package dev.macrohq.swiftslayer.command

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import dev.macrohq.swiftslayer.macro.BossSpawner
import dev.macrohq.swiftslayer.util.bossSpawner
import net.minecraft.entity.monster.EntityZombie

@Command(value = "bossspawner")
class BossSpawnercommand {
    @Main
    fun main() {
        bossSpawner.enable(EntityZombie::class.java)
    }
}