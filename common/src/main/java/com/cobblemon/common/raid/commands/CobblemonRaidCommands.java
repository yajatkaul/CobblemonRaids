package com.cobblemon.common.raid.commands;

import com.cobblemon.common.raid.CobblemonRaids;
import com.cobblemon.common.raid.blocks.RaidBlocks;
import com.cobblemon.common.raid.blocks.custom.blocks.RaidSpot;
import com.cobblemon.common.raid.codecs.CatchSpawn;
import com.cobblemon.common.raid.codecs.RaidDen;
import com.cobblemon.common.raid.codecs.Webhook;
import com.cobblemon.common.raid.managers.DenManager;
import com.cobblemon.common.raid.managers.RaidBoss;
import com.cobblemon.common.raid.managers.RaidManager;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;

import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class CobblemonRaidCommands {
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection environment) {
        dispatcher.register(literal("raid")
                .then(argument("cords", Vec3Argument.vec3())
                        .executes(CobblemonRaidCommands::spawnBoss))
                .then(literal("test")
                        .executes(CobblemonRaidCommands::createDen))
                .then(literal("webhook")
                        .then(argument("url", StringArgumentType.greedyString())
                                .executes(CobblemonRaidCommands::createWebhookConnection)))
                .then(literal("leave")
                        .executes(CobblemonRaidCommands::leaveRaid)));
    }

    private static int leaveRaid(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        RaidBoss raid = RaidManager.getRaidFromPlayer(player);

        if (raid == null) {
            player.sendSystemMessage(Component.translatable("cobblemon_raids.not_in_raid"));
            return 0;
        }

        raid.removePlayer(player);

        return 1;
    }

    private static int createWebhookConnection(CommandContext<CommandSourceStack> context) {
        String url = context.getArgument("url", String.class);

        new Webhook(url).saveToJson(context.getSource().getServer());

        return 1;
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
            RaidBoss raid = new RaidBoss(20,
                    8, pokemon,
                    10,
                    den,
                    20,
                    level,
                    pos,
                    10,
                    30,
                    10,
                    10,
                    "minecraft:chests/simple_dungeon",
                    "minecraft:chests/simple_dungeon",
                    20,
                    10,
                    20
            );

            raidSpot.setRaid(raid);
        } catch (Exception e) {
            CobblemonRaids.LOGGER.info(String.valueOf(e));
        }

        return 1;
    }

    private static int createDen(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();

        CatchSpawn catchSpawn = new CatchSpawn(player.position(), player.position());
        new RaidDen("example", player.level().dimension(), player.position(), player.position(), List.of(catchSpawn)).saveToJson(context.getSource().getServer());

        return 1;
    }
}
