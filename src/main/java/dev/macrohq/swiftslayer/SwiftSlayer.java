package dev.macrohq.swiftslayer;

import dev.macrohq.swiftslayer.config.SwiftSlayerConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "swiftslayer", name = "SwiftSlayer", version = "%%VERSION%%")
public class SwiftSlayer {
    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        new SwiftSlayerConfig();
    }
}
