package com.cobblemon.common.raid.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record RaidLoot(Optional<String> winLoot, Optional<String> defeatLoot) {
    public static final Codec<RaidLoot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("winLoot").forGetter(RaidLoot::winLoot),
            Codec.STRING.optionalFieldOf("defeatLoot").forGetter(RaidLoot::defeatLoot)
    ).apply(instance, RaidLoot::new));
}
