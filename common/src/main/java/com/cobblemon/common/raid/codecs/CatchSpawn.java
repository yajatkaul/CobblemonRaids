package com.cobblemon.common.raid.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec3;

public record CatchSpawn(Vec3 bossSpawn,
                         Vec3 playerSpawn
) {
    public static final Codec<CatchSpawn> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Vec3.CODEC.fieldOf("bossSpawn").forGetter(CatchSpawn::bossSpawn),
            Vec3.CODEC.fieldOf("playerSpawn").forGetter(CatchSpawn::playerSpawn)
    ).apply(instance, CatchSpawn::new));
}
