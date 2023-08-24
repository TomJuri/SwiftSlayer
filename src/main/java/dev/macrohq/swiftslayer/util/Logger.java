package dev.macrohq.swiftslayer.util;

import net.minecraft.util.ChatComponentText;

public class Logger {
    public static void info(String message) {
        send("a" + message);
    }

    public static void error(String message) {
        send("c" + message);
    }

    private static void send(String message) {
        Ref.player().addChatMessage(new ChatComponentText("§" + message));
    }
}
