package net.tekoramods.tekora.block.entity.blocks.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ElectricInfuser extends AbstractMachineBlock {
    public ElectricInfuser() {
        super(6f);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return null;
    }
}
