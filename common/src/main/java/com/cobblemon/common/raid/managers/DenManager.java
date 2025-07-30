package com.cobblemon.common.raid.managers;

import com.cobblemon.common.raid.codecs.RaidDen;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DenManager {
    private static final Set<RaidDen> raidDenList = new HashSet<>();
    private static final List<RaidDen> raidDenActive = new ArrayList<>();
    private static final List<RaidDen> raidDenInactive = new ArrayList<>();

    public static List<RaidDen> getAllInactiveDens() {
        return raidDenInactive;
    }

    public static RaidDen getRandomDen() {
        if (raidDenInactive.isEmpty()) {
            return null;
        }
        return raidDenInactive.getFirst();
    }

    public static RaidDen getDenByName(String name) {
        for (RaidDen den : raidDenList) {
            if (den.name().equals(name)) {
                return den;
            }
        }

        return null;
    }

    public static void occupyDen(RaidDen den) {
        raidDenActive.add(den);
        raidDenInactive.remove(den);
    }

    public static void freeDen(RaidDen den) {
        raidDenInactive.add(den);
        raidDenActive.add(den);
    }

    public static void load(MinecraftServer server) {
        Set<RaidDen> raidDens = RaidDen.loadFromJson(server);
        raidDenList.addAll(raidDens);
        raidDenInactive.addAll(raidDens);
    }

    public static void unload() {
        raidDenList.clear();
    }
}
