package com.cobblemon.common.raid.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record RaidLoot(String winLoot, String defeatLoot) {
    public static final Codec<RaidLoot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("winLoot").forGetter(RaidLoot::winLoot),
            Codec.STRING.fieldOf("defeatLoot").forGetter(RaidLoot::defeatLoot)
    ).apply(instance, RaidLoot::new));
}
