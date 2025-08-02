package com.cobblemon.common.raid.blocks.custom.renderer;

import com.cobblemon.common.raid.blocks.custom.blockEntities.RaidSpotEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class RaidSpotEntityRenderer implements BlockEntityRenderer<RaidSpotEntity> {
    public RaidSpotEntityRenderer(BlockEntityRendererProvider.Context context) {
        // constructor logic
    }

    @Override
    public void render(RaidSpotEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        if(blockEntity.getRaid() == null) {
            return;
        }

        long time = blockEntity.getLevel().getGameTime();
        int startY = 0; // height from relative to the block
        int endY = blockEntity.getLevel().getMaxBuildHeight(); // max height
        int sectionHeight = endY - startY;
        int packedColor = 0xFF0000;

        // Simulate beacon beam
        BeaconRenderer.renderBeaconBeam(
                poseStack,
                multiBufferSource,
                partialTicks,
                time,
                startY,
                sectionHeight,
                packedColor
        );
    }
}
