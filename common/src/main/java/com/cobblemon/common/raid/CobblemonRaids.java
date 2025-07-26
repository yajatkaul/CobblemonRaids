package com.cobblemon.common.raid;

import com.cobblemon.common.raid.blocks.RaidBlocks;
import com.cobblemon.common.raid.blocks.custom.RaidSpot;
import com.cobblemon.common.raid.codecs.CatchSpawn;
import com.cobblemon.common.raid.codecs.RaidDen;
import com.cobblemon.common.raid.events.CobbleEvents;
import com.cobblemon.common.raid.managers.DenManager;
import com.cobblemon.common.raid.managers.RaidBoss;
import com.cobblemon.common.raid.managers.RaidManager;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class CobblemonRaids {
    public static final Logger LOGGER = LoggerFactory.getLogger("CobblemonRaids");
    public static final String MOD_ID = "cobblemon_raids";

    public static void register() {
        CobbleEvents.register();
        RaidBlocks.register();
    }

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection environment) {
        dispatcher.register(literal("test")
                .then(argument("cords", Vec3Argument.vec3())
                        .executes(CobblemonRaids::spawnBoss))
                .then(literal("test")
                        .executes(CobblemonRaids::createDen)));
    }

    private static int spawnBoss(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();

        //TEMP
        ServerLevel level = (ServerLevel) player.level();

        int x = level.getRandom().nextInt(100);
        int y = 64;
        int z = level.getRandom().nextInt(100);

        BlockPos pos = new BlockPos(x, y, z);

        RaidSpot raidSpot = (RaidSpot) RaidBlocks.RAID_SPOT.get();

        player.sendSystemMessage(Component.literal(pos.toString()));
        level.setBlock(pos, raidSpot.defaultBlockState(), Block.UPDATE_ALL);

        try {
            RaidDen den = DenManager.getDenByName("example");

            Pokemon pokemon = PokemonProperties.Companion.parse("pikachu").create();
            RaidBoss raid = new RaidBoss(20, 8, pokemon, 10, den, 20, pos);

            raidSpot.setRaid(raid);
            RaidManager.spawnBoss(context.getSource().getLevel(), raid);
        } catch (Exception e) {
            CobblemonRaids.LOGGER.info(String.valueOf(e));
        }

        return 1;
    }

    private static int createDen(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();

        CatchSpawn catchSpawn = new CatchSpawn(player.position(), player.position());
        new RaidDen("example", player.position(), player.position(), List.of(catchSpawn)).saveToJson(context.getSource().getServer());

        return 1;
    }

    public static void onServerTickEnd(MinecraftServer server) {
        RaidManager.tick(server);
        for (RaidBoss raid : RaidManager.getAllRaids()) {
            raid.tick();
        }
    }

    public static void onServerStarted(MinecraftServer server) {
        DenManager.load(server);
    }

    public static void onServerClosed(MinecraftServer server) {
        DenManager.unload();
    }
}
