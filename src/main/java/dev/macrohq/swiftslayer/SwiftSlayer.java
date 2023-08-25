package dev.macrohq.swiftslayer;

import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import dev.macrohq.swiftslayer.command.PathfindTest;
import dev.macrohq.swiftslayer.config.SwiftSlayerConfig;
import dev.macrohq.swiftslayer.pathfinding.PathExecutor;
import dev.macrohq.swiftslayer.util.RenderUtil;
import lombok.Getter;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "swiftslayer", name = "SwiftSlayer", version = "%%VERSION%%")
public class SwiftSlayer {

    @Getter
    @Mod.Instance("swiftslayer")
    private static SwiftSlayer instance;
    public PathExecutor pathExecutor = new PathExecutor();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        new SwiftSlayerConfig();
        MinecraftForge.EVENT_BUS.register(pathExecutor);
        MinecraftForge.EVENT_BUS.register(new RenderUtil());
        CommandManager.register(new PathfindTest());
    }
}
