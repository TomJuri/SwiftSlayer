package dev.macrohq.swiftslayer.util;

import dev.macrohq.swiftslayer.mixin.MinecraftInvoker;
import net.minecraft.client.settings.KeyBinding;

public class KeyBindUtil {
    public static void leftClick() { ((MinecraftInvoker) Ref.mc()).invokeClickMouse(); }
    public static void rightClick() { ((MinecraftInvoker) Ref.mc()).invokeRightClickMouse(); }
    public static void setPressed(KeyBinding key, boolean pressed) { KeyBinding.setKeyBindState(key.getKeyCode(), pressed); }
}
