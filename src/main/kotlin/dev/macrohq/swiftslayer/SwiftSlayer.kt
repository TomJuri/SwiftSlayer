package dev.macrohq.swiftslayer

import cc.polyfrost.oneconfig.utils.commands.CommandManager
import dev.macrohq.swiftslayer.command.PathfindTest
import dev.macrohq.swiftslayer.config.SwiftSlayerConfig
import dev.macrohq.swiftslayer.features.BatphoneHandler
import dev.macrohq.swiftslayer.macro.EndermanMacro
import dev.macrohq.swiftslayer.macro.MobKiller
import dev.macrohq.swiftslayer.pathfinding.PathExecutor
import dev.macrohq.swiftslayer.util.RenderUtil
import dev.macrohq.swiftslayer.util.RotationUtil
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Mod(modid = "swiftslayer", name = "SwiftSlayer", version = "%%VERSION%%")
class SwiftSlayer {
    companion object {
        @Mod.Instance("swiftslayer")
        lateinit var instance: SwiftSlayer private set
    }

    lateinit var pathExecutor: PathExecutor private set
    lateinit var config: SwiftSlayerConfig private set
    lateinit var mobKiller: MobKiller private set
    lateinit var endermanMacro: EndermanMacro private set
    lateinit var batphoneHandler: BatphoneHandler private set
    var removeLater: BlockPos? = null

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        config = SwiftSlayerConfig()
        pathExecutor = PathExecutor()
        mobKiller = MobKiller()
        endermanMacro = EndermanMacro()
        batphoneHandler = BatphoneHandler()
        MinecraftForge.EVENT_BUS.register(this)
        MinecraftForge.EVENT_BUS.register(pathExecutor)
        MinecraftForge.EVENT_BUS.register(mobKiller)
        MinecraftForge.EVENT_BUS.register(batphoneHandler)
        CommandManager.register(PathfindTest())
    }

    @SubscribeEvent
    fun onRenderWorldLast(event: RenderWorldLastEvent) {
        // this is here because im not sure if objects can have events cuz they are kinda static
        RotationUtil.onRenderWorldLast()
        RenderUtil.onRenderWorldLast(event)
    }
}
