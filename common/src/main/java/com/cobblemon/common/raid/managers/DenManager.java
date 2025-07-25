package com.cobblemon.common.raid.managers;

import com.cobblemon.common.raid.codecs.RaidDen;
import net.minecraft.server.MinecraftServer;

import java.util.HashSet;
import java.util.Set;

public class DenManager {
    private static final Set<RaidDen> raidDenList = new HashSet<>();

    public static Set<RaidDen> getAllDens() {
        return raidDenList;
    }

    public static RaidDen getDenByName(String name) {
        for (RaidDen den: raidDenList) {
            if(den.name().equals(name)) {
                return den;
            }
        }

        return null;
    }

    public static void load(MinecraftServer server) {
        raidDenList.addAll(RaidDen.loadFromJson(server));
    }

    public static void unload() {
        raidDenList.clear();
    }
}
