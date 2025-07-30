package com.cobblemon.common.raid.mixin;

import com.cobblemon.common.raid.CobblemonRaids;
import com.cobblemon.mod.common.api.pokeball.PokeBalls;
import com.cobblemon.mod.common.api.pokeball.catching.modifiers.MultiplierModifier;
import com.cobblemon.mod.common.pokeball.PokeBall;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;

@Mixin(value = PokeBalls.class, remap = false)
public abstract class PokeBallsMixin {
    @Final
    @Shadow
    private static HashMap<ResourceLocation, PokeBall> defaults;

    @Unique
    private static final PokeBall RAID_BALL = new PokeBall(ResourceLocation.fromNamespaceAndPath(CobblemonRaids.MOD_ID, "raid_ball"),
            new MultiplierModifier(1.0f, (thrower, pokemon) -> true),
            List.of(), // empty list
            0.8f,
            ResourceLocation.fromNamespaceAndPath(CobblemonRaids.MOD_ID, "raid_ball"),
            ResourceLocation.fromNamespaceAndPath(CobblemonRaids.MOD_ID, "item/raid_ball_model"),
            1.25f,
            false);

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void init(CallbackInfo ci) {
        defaults.put(ResourceLocation.fromNamespaceAndPath(CobblemonRaids.MOD_ID, "raid_ball"), RAID_BALL);
    }
}
