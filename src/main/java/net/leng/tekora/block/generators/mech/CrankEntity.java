package net.leng.tekora.block.generators.mech;

import net.leng.tekora.blockent.generators.mech.AbstractTekoraRotationalEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CrankEntity extends AbstractTekoraRotationalEntity {
    public CrankEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }
}
