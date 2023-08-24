package dev.macrohq.swiftslayer;

import dev.macrohq.swiftslayer.config.SwiftSlayerConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "swiftslayer", name = "SwiftSlayer", version = "%%VERSION%%")
public class SwiftSlayer {

    @Mod.Instance("swiftslayer")
    private static SwiftSlayer instance;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        new SwiftSlayerConfig();
    }

    public static SwiftSlayer getInstance() {
        return instance;
    }
}
