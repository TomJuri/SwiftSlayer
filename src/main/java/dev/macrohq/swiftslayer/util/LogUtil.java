package dev.macrohq.swiftslayer.util;

import dev.macrohq.swiftslayer.config.SwiftSlayerConfig;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class LogUtil {
    public static void log(String message){
        if(!SwiftSlayerConfig.debug) return;
        Ref.player().addChatMessage(new ChatComponentText(
                EnumChatFormatting.GOLD + "Log" + EnumChatFormatting.RESET +
                EnumChatFormatting.DARK_GRAY + ": " + EnumChatFormatting.GRAY + message));
    }

    public static void say(String message){
        write(message, EnumChatFormatting.GREEN);
    }

    public static void error(String message){
        write(message, EnumChatFormatting.RED);
    }

    static void write(String message, EnumChatFormatting color){
        Ref.player().addChatMessage(
                new ChatComponentText(
                        EnumChatFormatting.DARK_GRAY + "[" + EnumChatFormatting.DARK_RED +
                                "Swift" + EnumChatFormatting.DARK_GRAY + "]"
                                + EnumChatFormatting.RESET + EnumChatFormatting.GOLD + ": " + color + message
                )
        );
    }
}
