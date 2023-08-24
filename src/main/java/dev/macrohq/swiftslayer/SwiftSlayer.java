package dev.macrohq.swiftslayer;

import dev.macrohq.swiftslayer.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid=SwiftSlayer.modid, version=SwiftSlayer.version)
public class SwiftSlayer {
    public static final String modid = "tree";
    public static final String version = "1.0";
    public static Config config;
    public static final Minecraft mc = Minecraft.getMinecraft();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        config = new Config();
    }
}
