package dev.macrohq.swiftslayer

import cc.polyfrost.oneconfig.utils.commands.CommandManager
import dev.macrohq.swiftslayer.command.PathfindTest
import dev.macrohq.swiftslayer.command.SwiftSlayerCommand
import dev.macrohq.swiftslayer.config.SwiftSlayerConfig
import dev.macrohq.swiftslayer.feature.AutoBatphone
import dev.macrohq.swiftslayer.feature.Failsafe
import dev.macrohq.swiftslayer.gui.AuthFailedDisplay
import dev.macrohq.swiftslayer.macro.EndermanBossKiller
import dev.macrohq.swiftslayer.macro.GenericBossKiller
import dev.macrohq.swiftslayer.macro.MacroManager
import dev.macrohq.swiftslayer.macro.MobKiller
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
    var removeLater: BlockPos? = null

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        try {
            val realData: String = AuthUtil.getHWID() + "|" + System.currentTimeMillis() + "|" + mc.session.playerID
            val publicKey: RSAPublicKey =
                AuthUtil.getPublicKey("MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAtXyd0UpgM40+t5yLwYesXGHNx/QU/HF1sa2S5c0q2SbQ8YeCaLj1apO0wXd626Axtek+IKjeUMeYrjM8XYerQ9U2T+BszUq9A7C5OdmZFDMnvRnDl9KBlJccRYTFvZG6X1UW+RkLnJ3Mpf8WHOgiVSE27+rcWc1GwCEWVElDFajbRxZFYJvXJVrulB36rz7I6RLELagmUFA23R3/SUZZvVRfnjXDx0oR3kWo0ZJgzqbFG/dQEa9ASD6lzI2aT0cESDj8NJxCbH55AyIzauUDjq7yTLvYsF4xLC3afQblnIKjrklI7ZRwVaT0j6edehCjZtqj9ga7Hxp2JlkSn5PREPgq80APmCwelwK2UkdNRyyHereiiC1RahT1raS2F+OfrDYRi6FxF0mowRh/AsszcgQfSV7WC9b9vXwDCzotuuZ9t9xoHP6wy/zkeCMOX3J5cSuM+UnnO1DkNjvNN3BLs/PSSga3havwGTj5vefx17h0e22kHAnH3LMLcq9RFn2evlS9TziezeMPYASDOuxXWYUQk+JwPNAP817/yuLZinjBGFpjC3gFVhxi6xW8D8dYNH8eQZx/eqWdfGZLGsp4de2aLkP5sSDG3modrYtTh57zfAc508nKLdjg9scoian2NP0gD1ItQkgPb1rF7TNsJjZIvAthdea/6OKY3Mnzzn0CAwEAAQ==")
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
                    MinecraftForge.EVENT_BUS.register(this)
                    MinecraftForge.EVENT_BUS.register(pathExecutor)
                    MinecraftForge.EVENT_BUS.register(mobKiller)
                    MinecraftForge.EVENT_BUS.register(autoBatphone)
                    MinecraftForge.EVENT_BUS.register(macroManager)
                    MinecraftForge.EVENT_BUS.register(genericBossKiller)
                    MinecraftForge.EVENT_BUS.register(Failsafe())
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
