package com.cobblemon.common.raid;

import com.cobblemon.common.raid.events.CobbleEvents;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class CobblemonRaids {
    public static final Logger LOGGER = LoggerFactory.getLogger("CobblemonRaids");
    public static final String MOD_ID = "cobblemon_raids";

    public static void register() {
        CobbleEvents.register();
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection environment) {
        dispatcher.register(literal("test")
                .then(argument("cords", Vec3Argument.vec3())
                        .executes(CobblemonRaids::spawnBoss)));
    }

    private static int spawnBoss(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();

        Vec3 pos = Vec3Argument.getVec3(context, "cords");

        try {
            Pokemon pokemon = PokemonProperties.Companion.parse("pikachu").create();
            RaidBoss raid = new RaidBoss(20, 8, pokemon, 10);

            RaidManager.spawnBoss(context.getSource().getLevel(), raid, pos);
        }catch (Exception e){
            CobblemonRaids.LOGGER.info(String.valueOf(e));
        }

        return 1;
    }

    public static void onServerTickEnd(MinecraftServer server) {
        for (RaidBoss raid: RaidManager.getAllRaids()) {
            raid.tick();
        }
    }
}
