package dev.macrohq.swiftslayer

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion
import org.spongepowered.asm.launch.MixinBootstrap
import org.spongepowered.asm.mixin.MixinEnvironment
import org.spongepowered.asm.mixin.Mixins

@MCVersion("1.8.9")
class DevMixinLoader : IFMLLoadingPlugin {
  override fun getASMTransformerClass(): Array<String> = emptyArray()
  override fun getModContainerClass(): String? = null
  override fun getSetupClass(): String? = null
  override fun injectData(data: Map<String, Any>) {}
  override fun getAccessTransformerClass(): String? = null

  init {
    MixinBootstrap.init()
    MixinEnvironment.getCurrentEnvironment().obfuscationContext = "searge"
    Mixins.addConfiguration("mixins.swiftslayer.json")
  }
}