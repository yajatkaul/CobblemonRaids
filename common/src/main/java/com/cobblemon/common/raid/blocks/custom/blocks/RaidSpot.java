package com.cobblemon.common.raid.blocks.custom.blocks;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.common.raid.managers.RaidBoss;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RaidSpot extends Block {
    private RaidBoss raid = null;
    public static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 4, 14);

    public RaidSpot(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState arg, BlockGetter arg2, BlockPos arg3, CollisionContext arg4) {
        return SHAPE;
    }

    @Override
    protected RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    public void setRaid(RaidBoss boss) {
        this.raid = boss;
    }

    public RaidBoss getRaid() {
        return this.raid;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        if (player.level().isClientSide) {
            return InteractionResult.PASS;
        }

        if (this.raid != null) {
            GooeyButton filler = GooeyButton.builder()
                    .display(Items.GRAY_STAINED_GLASS_PANE.getDefaultInstance())
                    .build();

            GooeyButton pokemonInfoButton = GooeyButton.builder()
                    .display(getPokemonItem(this.raid.getBoss()))
                    .build();

            GooeyButton joinButton = GooeyButton.builder()
                    .display(Items.DIAMOND_SWORD.getDefaultInstance())
                    .with(DataComponents.ITEM_NAME, Component.translatable("buttons.join.cobblemon_raids"))
                    .with(DataComponents.LORE, new ItemLore(List.of(
                            Component.literal(String.format("%d/%d", this.raid.getPlayers().size(), this.raid.getMaxPlayers()))
                    )))
                    .onClick(() -> {
                        this.raid.addPlayer((ServerPlayer) player);
                        UIManager.closeUI((ServerPlayer) player);
                    })
                    .build();

            GooeyButton leaveButton = GooeyButton.builder()
                    .display(Items.BRUSH.getDefaultInstance())
                    .with(DataComponents.ITEM_NAME, Component.translatable("buttons.close.cobblemon_raids"))
                    .onClick(() -> UIManager.closeUI((ServerPlayer) player))
                    .build();

            ChestTemplate template = ChestTemplate.builder(5)
                    .fill(filler)
                    .set(3, 3, joinButton)
                    .set(2, 4, pokemonInfoButton)
                    .set(3, 5, leaveButton)
                    .build();

            GooeyPage page = GooeyPage.builder()
                    .title("Join Raid")
                    .template(template)
                    .build();

            UIManager.openUIForcefully((ServerPlayer) player, page);
        }

        return InteractionResult.SUCCESS;
    }

    private ItemStack getPokemonItem(Pokemon pokemon) {
        ItemStack pokemonStack = PokemonItem.from(pokemon);
        List<Component> lore = new ArrayList<>();

        // Add level line
        lore.add(Component.translatable("level.cobblemon_raids.info", this.raid.getCatchLevel()).withStyle(ChatFormatting.GOLD));

        // Header for IVs
        lore.add(Component.translatable("lore.cobblemon_raids.ivs").withStyle(ChatFormatting.LIGHT_PURPLE));

        // Add IVs with fixed formatting
        for (Map.Entry<? extends Stat, ? extends Integer> iv : pokemon.getIvs()) {
            String statName = iv.getKey().getDisplayName().getString();
            int value = iv.getValue();

            Component line = Component.literal(statName + ": ").withStyle(ChatFormatting.RED)
                    .append(Component.literal(String.valueOf(value)).withStyle(ChatFormatting.AQUA));

            lore.add(line);
        }

        // Footer line
        lore.add(Component.literal("---------------").withStyle(ChatFormatting.LIGHT_PURPLE));

        // Set the lore
        pokemonStack.set(DataComponents.LORE, new ItemLore(lore));

        return pokemonStack;
    }
}
