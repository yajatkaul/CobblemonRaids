package com.cobblemon.common.raid.blocks.custom.blocks;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.common.raid.blocks.custom.blockEntities.RaidSpotEntity;
import com.cobblemon.common.raid.managers.RaidBoss;
import com.cobblemon.mod.common.item.PokemonItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
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

public class RaidSpot extends BaseEntityBlock {
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

    public static final MapCodec<RaidSpot> CODEC = simpleCodec(RaidSpot::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
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
                    .display(PokemonItem.from(this.raid.getBoss()))
                    .build();

            GooeyButton joinButton = GooeyButton.builder()
                    .display(Items.DIAMOND_SWORD.getDefaultInstance())
                    .onClick(() -> {
                        this.raid.addPlayer((ServerPlayer) player);
                        UIManager.closeUI((ServerPlayer) player);
                    })
                    .build();

            GooeyButton leaveButton = GooeyButton.builder()
                    .display(Items.BRUSH.getDefaultInstance())
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RaidSpotEntity(blockPos, blockState);
    }
}
