package net.leng.tekora.blockent.shaft;

import net.leng.tekora.block.GearType;
import net.leng.tekora.block.shaft.ShaftBlock;
import net.leng.tekora.blockent.TekoraBlockEntities;
import net.leng.tekora.blockent.generators.mech.AbstractTekoraRotationalEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public abstract class ShaftEntity extends AbstractTekoraRotationalEntity {
    protected ShaftEntity(BlockEntityType<?> eventType, BlockPos blockPos, BlockState blockState) {
        super(eventType, blockPos, blockState);
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        // basic idea, if a block is oriented on the axis, measure its velocity
        // if a block is on the side, think of the forces involved.
        if (!level.isClientSide) {
            if (blockState.hasProperty(ShaftBlock.AXIS)) {
                List<BlockPos> axisPos = switch (blockState.getValue(ShaftBlock.AXIS)) {
                    case X -> List.of(blockPos.east(), blockPos.west());
                    case Y -> List.of(blockPos.above(), blockPos.below());
                    case Z -> List.of(blockPos.north(), blockPos.south());
                };
                for (BlockPos neighPos : axisPos) {
                    BlockEntity entity = level.getBlockEntity(neighPos);
                    if (entity instanceof ShaftEntity shaft) {
                        if (shaft.rpm == 0 && rpm != 0) shaft.rpm = rpm;
                        else if (rpm == 0 && shaft.rpm != 0) rpm = shaft.rpm;
                        else {
                            if ((rpm >= 0 && shaft.rpm >= 0) || (rpm <= 0 && shaft.rpm <= 0)) {
                                // a better mathematical model is needed
                                double avg = (rpm + shaft.rpm) / 2;
                                rpm = avg;
                                shaft.rpm = avg;
                            } else {
                                BlockState neighState = shaft.getBlockState();

                                GearType neighType = neighState.getValue(ShaftBlock.GEAR_TYPE);
                                GearType blockType = blockState.getValue(ShaftBlock.GEAR_TYPE);
                                Item neighItem = GearType.itemFromType(neighType);
                                Item blockItem = GearType.itemFromType(blockType);
                                level.playSound(null, neighPos, neighType.isFlammable() ? SoundEvents.WOOD_BREAK : SoundEvents.METAL_BREAK, SoundSource.BLOCKS);
                                level.playSound(null, blockPos, blockType.isFlammable() ? SoundEvents.WOOD_BREAK : SoundEvents.METAL_BREAK, SoundSource.BLOCKS);
                                level.removeBlock(blockPos, false);
                                level.removeBlock(neighPos, false);
                                level.addFreshEntity(new ItemEntity(level, neighPos.getX(), neighPos.getY(), neighPos.getZ(),
                                        new ItemStack(neighState.getBlock(), 1), 0, 0, 0));
                                level.addFreshEntity(new ItemEntity(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                                        new ItemStack(blockState.getBlock(), 1), 0, 0, 0));
                                if (neighItem != null) {
                                    level.addFreshEntity(new ItemEntity(level, neighPos.getX(), neighPos.getY(), neighPos.getZ(),
                                            new ItemStack(neighItem, 1), 0, 0, 0));
                                }
                                if (blockItem != null) {
                                    level.addFreshEntity(new ItemEntity(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                                            new ItemStack(blockItem, 1), 0, 0, 0)); // todo looks unnatural, fix it
                                }
                            }
                        }
                        // todo allow math to calculate for axial shaft momentum
                        // measurement systems: RPM (rotations/minute), power
                        // RPM = (in RPM)(in/out)
                        // in/out can either be 0.5, 1, or 2
                        // 0.5 if out is large & in is small
                        // 1 if out size == in size
                        // 2 if out is small & in is large
                    }
                }
                if (blockState.hasProperty(ShaftBlock.GEAR_TYPE) && blockState.getValue(ShaftBlock.GEAR_TYPE) != GearType.NONE) {
                    List<BlockPos> neighborPos = switch (blockState.getValue(ShaftBlock.AXIS)) {
                        case X -> List.of(blockPos.north(), blockPos.south(), blockPos.above(), blockPos.below());
                        case Y -> List.of(blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west());
                        case Z -> List.of(blockPos.east(), blockPos.west(), blockPos.above(), blockPos.below());
                    };
                    for (BlockPos neighPos : neighborPos) {
                        BlockEntity entity = level.getBlockEntity(neighPos);
                        if (entity instanceof ShaftEntity shaft) {
                            BlockState neighState = shaft.getBlockState();
                            if (neighState.hasProperty(ShaftBlock.GEAR_TYPE) && neighState.getValue(ShaftBlock.GEAR_TYPE) != GearType.NONE) {
                                // todo allow math for neighboring blocks to calculate cog movement momentum
                                // if rpm of either is 0, it can be mathematically considered as the "out" rpm
                                // out rpm = (in rpm) * (in teeth)/(out teeth)
                                // once energy is transferred to a machine,
                                // the machine will have the output be the power required to activate it (kinetic energy)
                                if (shaft.rpm == 0 && rpm != 0) shaft.rpm = -rpm;
                                else if (rpm == 0 && shaft.rpm != 0) rpm = -shaft.rpm;
                                else {
                                    if ((rpm >= 0 && shaft.rpm <= 0) || (rpm <= 0 && shaft.rpm >= 0)) {
                                        double total = Math.abs(shaft.rpm) + Math.abs(rpm);
                                        // this is overly simplistic, needs modification
                                        shaft.rpm = total;
                                        rpm = total;
                                    } else if (rpm != -shaft.rpm) {
                                        GearType neighType = neighState.getValue(ShaftBlock.GEAR_TYPE);
                                        GearType blockType = blockState.getValue(ShaftBlock.GEAR_TYPE);
                                        Item neighItem = GearType.itemFromType(neighType);
                                        Item blockItem = GearType.itemFromType(blockType);
                                        level.setBlockAndUpdate(neighPos, neighState.setValue(ShaftBlock.GEAR_TYPE, GearType.NONE));
                                        level.setBlockAndUpdate(blockPos, blockState.setValue(ShaftBlock.GEAR_TYPE, GearType.NONE));
                                        level.playSound(null, neighPos, neighType.isFlammable() ? SoundEvents.WOOD_BREAK : SoundEvents.METAL_BREAK, SoundSource.BLOCKS);
                                        level.playSound(null, blockPos, blockType.isFlammable() ? SoundEvents.WOOD_BREAK : SoundEvents.METAL_BREAK, SoundSource.BLOCKS);
                                        if (neighItem != null) {
                                            level.addFreshEntity(new ItemEntity(level, neighPos.getX(), neighPos.getY(), neighPos.getZ(),
                                                    new ItemStack(neighItem, 1), 0, 0, 0));
                                        }
                                        if (blockItem != null) {
                                            level.addFreshEntity(new ItemEntity(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                                                    new ItemStack(blockItem, 1), 0, 0, 0));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    protected abstract int getWeight();

    public static class Steel extends ShaftEntity {
        public Steel(BlockPos blockPos, BlockState blockState) {
            super(TekoraBlockEntities.STEEL_SHAFT_ENTITY.get(), blockPos, blockState);
        }

        @Override
        protected int getWeight() {
            return 0;
        }
    }
}
