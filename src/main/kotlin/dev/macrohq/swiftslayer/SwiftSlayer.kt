package dev.macrohq.swiftslayer

import cc.polyfrost.oneconfig.utils.commands.CommandManager
import dev.macrohq.swiftslayer.command.*
import dev.macrohq.swiftslayer.config.SwiftSlayerConfig
import dev.macrohq.swiftslayer.event.GameEventHandler
import dev.macrohq.swiftslayer.feature.*
import dev.macrohq.swiftslayer.macro.*
import dev.macrohq.swiftslayer.pathfinder.helper.BlockStateAccessor
import dev.macrohq.swiftslayer.pathfinder.helper.player.IPlayerContext
import dev.macrohq.swiftslayer.pathfinder.helper.player.PlayerContext
import dev.macrohq.swiftslayer.pathfinding.PathExecutor
import dev.macrohq.swiftslayer.util.KeyBindUtil
import dev.macrohq.swiftslayer.util.RenderUtil
import dev.macrohq.swiftslayer.util.RotationUtil
import net.minecraft.client.Minecraft
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/* fun main() {
    val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
    keyPairGenerator.initialize(4096)
    val keyPair = keyPairGenerator.generateKeyPair()
    println("Public Key 1: " + Base64.getEncoder().encodeToString(keyPair.public.encoded)).toString()
    println("Private Key 1: " + Base64.getEncoder().encodeToString(keyPair.private.encoded)).toString()

    val keyPairGenerator0 = KeyPairGenerator.getInstance("RSA")
    keyPairGenerator0.initialize(4096)
    val keyPair0 = keyPairGenerator0.generateKeyPair()
    println("Public Key 2: " + Base64.getEncoder().encodeToString(keyPair0.public.encoded)).toString()
    println("Private Key 2: " + Base64.getEncoder().encodeToString(keyPair0.private.encoded)).toString()
}*/

@Mod(modid = "swiftslayer", name = "SwiftSlayer", version = "%%VERSION%%")
class SwiftSlayer {
  companion object {
    @Mod.Instance("swiftslayer")
    lateinit var instance: SwiftSlayer private set
  }

  lateinit var pathExecutor: PathExecutor private set
  lateinit var config: SwiftSlayerConfig private set
  lateinit var mobKiller: MobKiller private set
  lateinit var endermanBossKiller: EndermanBossKiller private set
  lateinit var autoBatphone: AutoBatphone private set
  lateinit var macroManager: MacroManager private set
  lateinit var genericBossKiller: GenericBossKiller private set
  lateinit var revenant: Revenant private set
  lateinit var tracker: Tracker private set
  private lateinit var lockTest: LockTest
  var removeLater: BlockPos? = null

  @Mod.EventHandler
  fun init(event: FMLInitializationEvent) {
    config = SwiftSlayerConfig()
    pathExecutor = PathExecutor()
    mobKiller = MobKiller()
    endermanBossKiller = EndermanBossKiller()
    autoBatphone = AutoBatphone()
    macroManager = MacroManager()
    genericBossKiller = GenericBossKiller()
    revenant = Revenant()
    tracker = Tracker()
    lockTest = LockTest()
    MinecraftForge.EVENT_BUS.register(this)
    MinecraftForge.EVENT_BUS.register(pathExecutor)
    MinecraftForge.EVENT_BUS.register(mobKiller)
    MinecraftForge.EVENT_BUS.register(autoBatphone)
    MinecraftForge.EVENT_BUS.register(macroManager)
    MinecraftForge.EVENT_BUS.register(genericBossKiller)
    MinecraftForge.EVENT_BUS.register(Failsafe())
    MinecraftForge.EVENT_BUS.register(revenant)
    MinecraftForge.EVENT_BUS.register(tracker)
    MinecraftForge.EVENT_BUS.register(lockTest)
    MinecraftForge.EVENT_BUS.register(DirectionTest())
    MinecraftForge.EVENT_BUS.register(SupportItem())
    CommandManager.register(PathfindTest())
    CommandManager.register(SwiftSlayerCommand())
    CommandManager.register(DirectionTest())

    val cmd = TestCommand()
    CommandManager.register(cmd)
    MinecraftForge.EVENT_BUS.register(cmd)

    // New Structure
    FeatureManager.getInstance().loadFeatures().forEach(MinecraftForge.EVENT_BUS::register)
    MinecraftForge.EVENT_BUS.register(GameEventHandler(this))

  }

  fun isTrackerInitialized() = ::tracker.isInitialized

  @SubscribeEvent
  fun onRenderWorldLast(event: RenderWorldLastEvent) {
    // this is here because im not sure if objects can have events cuz they are kinda static
    RotationUtil.onRenderWorldLast()
    RenderUtil.onRenderWorldLast(event)
    KeyBindUtil.onRenderWorldLast()
  }

  // Hellow
  val mc: Minecraft = Minecraft.getMinecraft()
  val playerContext: IPlayerContext = PlayerContext(this, mc)
  var bsa: BlockStateAccessor? = null
}