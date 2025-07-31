package com.cobblemon.common.raid.codecs;

import com.cobblemon.common.raid.codecs.pokemon.RaidMon;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;

public record RaidData(RaidMon raidMon,
                       int maxHealth,
                       int catchLevel,
                       int bossLevel,
                       int damagePerWin,
                       int baseScale,
                       int maxPlayers,
                       long preBattleDuration,
                       long battleDuration,
                       long prepareDuration,
                       long catchDuration,
                       String rarity,
                       RaidLoot lootTables,
                       float weight,
                       int totalBalls,
                       Optional<String> biome
) {
    public static final Codec<RaidData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RaidMon.CODEC.fieldOf("raidMon").forGetter(RaidData::raidMon),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("maxHealth").forGetter(RaidData::maxHealth),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("catchLevel").forGetter(RaidData::catchLevel),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("bossLevel").forGetter(RaidData::bossLevel),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("damagePerWin").forGetter(RaidData::damagePerWin),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("baseScale").forGetter(RaidData::baseScale),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("maxPlayers").forGetter(RaidData::maxPlayers),
            Codec.LONG.fieldOf("preBattleDuration").forGetter(RaidData::preBattleDuration),
            Codec.LONG.fieldOf("battleDuration").forGetter(RaidData::battleDuration),
            Codec.LONG.fieldOf("prepareDuration").forGetter(RaidData::prepareDuration),
            Codec.LONG.fieldOf("catchDuration").forGetter(RaidData::catchDuration),
            Codec.STRING.fieldOf("rarity").forGetter(RaidData::rarity),
            RaidLoot.CODEC.fieldOf("lootTables").forGetter(RaidData::lootTables),
            Codec.FLOAT.fieldOf("weight").forGetter(RaidData::weight),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("totalBalls", 20).forGetter(RaidData::totalBalls),
            Codec.STRING.optionalFieldOf("biome").forGetter(RaidData::biome)
    ).apply(instance, RaidData::new));
}
