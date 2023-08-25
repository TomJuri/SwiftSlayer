package dev.macrohq.swiftslayer;

import dev.macrohq.swiftslayer.commands.Set;
import dev.macrohq.swiftslayer.config.SwiftSlayerConfig;
import dev.macrohq.swiftslayer.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "swiftslayer", name = "SwiftSlayer", version = "%%VERSION%%")
public class SwiftSlayer {

    @Mod.Instance("swiftslayer")
    private static SwiftSlayer instance;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        new SwiftSlayerConfig();

        MinecraftForge.EVENT_BUS.register(new RenderUtil());
        ClientCommandHandler.instance.registerCommand(new Set());
    }

    public static SwiftSlayer getInstance() {
        return instance;
    }
}
