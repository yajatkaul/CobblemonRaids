package com.cobblemon.common.raid.codecs.pokemon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record RaidMon(String name, String pokemon, String pokemonImage) {
    public static final Codec<RaidMon> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(RaidMon::name),
            Codec.STRING.fieldOf("pokemon").forGetter(RaidMon::pokemon),
            Codec.STRING.fieldOf("pokemonImage").forGetter(RaidMon::pokemonImage)
    ).apply(instance, RaidMon::new));
}
