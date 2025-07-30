package com.cobblemon.common.raid.mixin;

import com.cobblemon.common.raid.CobblemonRaids;
import com.cobblemon.common.raid.items.pokeballs.RaidBalls;
import com.cobblemon.mod.common.api.pokeball.PokeBalls;
import com.cobblemon.mod.common.pokeball.PokeBall;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(value = PokeBalls.class, remap = false)
public abstract class PokeBallsMixin {
    @Final
    @Shadow
    private static HashMap<ResourceLocation, PokeBall> defaults;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void init(CallbackInfo ci) {
        defaults.put(ResourceLocation.fromNamespaceAndPath(CobblemonRaids.MOD_ID, "raid_ball"), RaidBalls.RAID_BALL);
    }
}
