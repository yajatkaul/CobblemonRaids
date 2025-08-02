package com.cobblemon.common.raid.items.custom.items;

import com.cobblemon.common.raid.datacomponents.RaidDataComponents;
import com.cobblemon.common.raid.managers.RaidUtils;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class RaidLoot extends SimplePolymerItem {
    public RaidLoot(Properties properties, Item vanillaBaseItem) {
        super(properties, vanillaBaseItem);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (player.level().isClientSide) {
            return InteractionResultHolder.pass(itemStack);
        }

        List<ItemStack> item = itemStack.getOrDefault(RaidDataComponents.LOOT_COMPONENT, null);

        if (item == null) {
            return InteractionResultHolder.pass(itemStack);
        }

        for (ItemStack loot : item) {
            RaidUtils.giveItems((ServerPlayer) player, loot);
        }
        itemStack.consume(1, player);

        return InteractionResultHolder.success(itemStack);
    }
}
