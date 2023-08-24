package dev.macrohq.swiftslayer.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;

public class SwiftSlayerConfig extends Config {
    public SwiftSlayerConfig(){
        super(new Mod("SwiftSlayer", ModType.SKYBLOCK), "swiftslayer.json");
        initialize();
    }
}
