package com.cobblemon.common.raid.codecs.pokemon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;

public record RaidMon(String name,
                      String pokemon,
                      Optional<String> pokemonImage,
                      int maxHealth,
                      int catchLevel,
                      int bossLevel,
                      int damagePerWin,
                      int baseScale,
                      int maxPlayers
) {
    public static final Codec<RaidMon> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(RaidMon::name),
            Codec.STRING.fieldOf("pokemon").forGetter(RaidMon::pokemon),
            Codec.STRING.optionalFieldOf("pokemonImage").forGetter(RaidMon::pokemonImage),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("maxHealth").forGetter(RaidMon::maxHealth),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("catchLevel").forGetter(RaidMon::catchLevel),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("bossLevel").forGetter(RaidMon::bossLevel),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("damagePerWin").forGetter(RaidMon::damagePerWin),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("baseScale").forGetter(RaidMon::baseScale),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("maxPlayers").forGetter(RaidMon::maxPlayers)
    ).apply(instance, RaidMon::new));
}
