package net.leng.tekora.block.machines.thermal;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class KilnFurnace extends BaseEntityBlock {
    public static final MapCodec<KilnFurnace> CODEC = simpleCodec(p -> new KilnFurnace());

    protected KilnFurnace() {
        super(Properties.ofFullCopy(Blocks.FURNACE));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return null;
    }
}
