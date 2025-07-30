package com.cobblemon.common.raid.datapack;

import com.cobblemon.common.raid.CobblemonRaids;
import com.cobblemon.common.raid.codecs.RaidData;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class DatapackRegister {
    public static Registry<RaidData> raidsRegistry;
    private static final ResourceKey<Registry<RaidData>> RAID_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(CobblemonRaids.MOD_ID, "raid_data"));

    public static void register(RegistryAccess registryAccess) {
        raidsRegistry = registryAccess.registryOrThrow(RAID_REGISTRY_KEY);
    }
}
