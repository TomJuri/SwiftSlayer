package dev.macrohq.swiftslayer.util;

import dev.macrohq.swiftslayer.config.SwiftSlayerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.world.World;

public class Ref {
    public static Minecraft mc() { return Minecraft.getMinecraft(); }
    public static EntityPlayerSP player() { return mc().thePlayer; }
    public static World world() { return mc().theWorld; }
    public static GameSettings gameSettings() { return mc().gameSettings; }
}
