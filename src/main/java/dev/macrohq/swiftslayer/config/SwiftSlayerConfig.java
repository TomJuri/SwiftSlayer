package dev.macrohq.swiftslayer.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;

public class SwiftSlayerConfig extends Config {

    @Switch(
            name = "Debug Log",
            category = "General",
            subcategory = "QOL",
            size = 2
    )
    public static boolean debug = true;
    public SwiftSlayerConfig(){
        super(new Mod("SwiftSlayer", ModType.SKYBLOCK), "swiftslayer.json");
        initialize();
    }
}
