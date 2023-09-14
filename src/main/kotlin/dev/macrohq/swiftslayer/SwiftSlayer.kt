package dev.macrohq.swiftslayer

import cc.polyfrost.oneconfig.utils.commands.CommandManager
import dev.macrohq.swiftslayer.command.PathfindTest
import dev.macrohq.swiftslayer.command.SwiftSlayerCommand
import dev.macrohq.swiftslayer.config.SwiftSlayerConfig
import dev.macrohq.swiftslayer.feature.AutoBatphone
import dev.macrohq.swiftslayer.feature.Failsafe
import dev.macrohq.swiftslayer.gui.AuthFailedDisplay
import dev.macrohq.swiftslayer.macro.*
import dev.macrohq.swiftslayer.pathfinding.PathExecutor
import dev.macrohq.swiftslayer.util.AuthUtil
import dev.macrohq.swiftslayer.util.RenderUtil
import dev.macrohq.swiftslayer.util.RotationUtil
import dev.macrohq.swiftslayer.util.mc
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import okhttp3.OkHttpClient
import okhttp3.Request
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

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
    var removeLater: BlockPos? = null

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        try {
            val realData: String = AuthUtil.getHWID() + "|" + System.currentTimeMillis() + "|" + mc.session.playerID
            val publicKey: RSAPublicKey =
                AuthUtil.getPublicKey("MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA4QhpTdwM5avLzneyOsuQmxuIn0K9S8RhP7OxNX71rM454LJ9TSGVh6u2V++Nxnl9jJ60a1U7j5NQbgalPj1y1uJeWna6fDZ1EvUaR9+U8obZxbIvqZXsWUpBufaTCaOWmRojOIwRlr24BWgGkQ1uWiqGFjWrQ3Jg9Q7PrrxacMS65ztoRQZZKhfLZxSf6EO/F2YspUUddBK6npXOR49/PgfDyf8Zpm099MlVkH92fdj08y8y/rAwHj1X63b/G3q85pRdKZtzhfa9xfzNjV3Fol/uQXXozaz2KN8psS7H1iZtXsmKiZmTSZcisXcCCUd2XKiJwA61kD51FEinscqG9Z+gWpszkL+XCX17XczJUD6OA628/mpBOVJ/ygWjc7irbKn8R/fdIgdAPu1nPp4WXApMXPsKp+YcJsFdU35N6oAp1uSG2w1ofRfBKd464/U1a8YAaRUji69O2r94v8sC4md2fPI8nnGSJ8vkkVPp4d7NJZQG853vmrr9KClu0gQdDNgkVLAOXt9WDX/1/xLAbpO0COoBI4f01jy/y4P+hRrfrHWFApl7e13iDSQAGMiQ3vKGcKTjJ5bu6q/KVky9x6TULqqj1CMfplSrCqaehhZlRNZmLgWQVhRrfx9/6jc/DfRd2fbCe9+9ZCRiarMMTrK8dtcKTKxuXuYfZrAq530CAwEAAQ==")
            val privateKey: RSAPrivateKey = AuthUtil.getPrivateKey(
                "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"
            )
            val fakeAndRealData = StringBuilder()
            for (i in 0..4) {
                if (i == 3) {
                    fakeAndRealData.append(AuthUtil.rsaEncrypt(realData.toByteArray(), publicKey))
                } else {
                    fakeAndRealData.append(
                        AuthUtil.rsaEncrypt(
                            AuthUtil.generateRandomString(realData.length).toByteArray(), publicKey
                        )
                    )
                }
                if (i < 4) {
                    fakeAndRealData.append("-")
                }
            }
            val url =
                "https://macrohq.dev/api/Q6W2ecZlZ3InKEEriO7onlQ1i1vAVCC9ipOF4qwsfNIK1xg6Z80NZTgbXbOJ?DvigquT6UAztO5IdMb0iIex6skfcHrPs8lDeTCxLfzG98n0AdNWeIN2VX3Kd=$fakeAndRealData"
            val request = Request.Builder().url(url)
                .addHeader("User-Agent", "TOM-CLIENT-JAVA8-Shipping-g1vJHlyarpLgba7Fu3SoUm5dGkoDHeI5f8QIFur7").build()
            val client = OkHttpClient()
            val response = client.newCall(request).execute()
            if (response.body() != null) {
                val decoded: String = AuthUtil.rsaDecrypt(response.body()!!.string(), privateKey)
                val split = decoded.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if ("success" == split[0] && AuthUtil.getHWID() == split[1] && mc.session.playerID.equals(split[2]) && System.currentTimeMillis() - split[3].toLong() < 20000) {
                    config = SwiftSlayerConfig()
                    pathExecutor = PathExecutor()
                    mobKiller = MobKiller()
                    endermanBossKiller = EndermanBossKiller()
                    autoBatphone = AutoBatphone()
                    macroManager = MacroManager()
                    genericBossKiller = GenericBossKiller()
                    revenant = Revenant()
                    MinecraftForge.EVENT_BUS.register(this)
                    MinecraftForge.EVENT_BUS.register(pathExecutor)
                    MinecraftForge.EVENT_BUS.register(mobKiller)
                    MinecraftForge.EVENT_BUS.register(autoBatphone)
                    MinecraftForge.EVENT_BUS.register(macroManager)
                    MinecraftForge.EVENT_BUS.register(genericBossKiller)
                    MinecraftForge.EVENT_BUS.register(Failsafe())
                    MinecraftForge.EVENT_BUS.register(revenant)
                    CommandManager.register(PathfindTest())
                    CommandManager.register(SwiftSlayerCommand())
                    return
                }
            }
        } catch (ignored: Exception) {
        }
        throw AuthFailedDisplay()
    }

    @SubscribeEvent
    fun onRenderWorldLast(event: RenderWorldLastEvent) {
        // this is here because im not sure if objects can have events cuz they are kinda static
        RotationUtil.onRenderWorldLast()
        RenderUtil.onRenderWorldLast(event)
    }
}
