package dev.macrohq.swiftslayer.config;

import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;

public class Config extends cc.polyfrost.oneconfig.config.Config{
    public Config(){
        super(new Mod("SwiftSlayer", ModType.UTIL_QOL), "swiftSlayer.json");
        initialize();
    }

}
