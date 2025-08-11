package net.leng.tekora.block.shaft;

import com.mojang.serialization.MapCodec;
import net.leng.tekora.block.GearType;
import net.leng.tekora.block.TekoraBlocks;
import net.leng.tekora.blockent.TekoraBlockEntities;
import net.leng.tekora.blockent.shaft.ShaftEntity;
import net.leng.tekora.item.GearItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public abstract class ShaftBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final EnumProperty<GearType> GEAR_TYPE = EnumProperty.create("gear_type", GearType.class);
    // public static final BooleanProperty LARGE_GEAR = BooleanProperty.create("large_gear", false);

    private static final VoxelShape SHAFT_X = box(0, 6, 6, 16, 10, 10);
    private static final VoxelShape SHAFT_Y = box(6, 0, 6, 10, 16, 10);
    private static final VoxelShape SHAFT_Z = box(6, 6, 0, 10, 10, 16);

    private static final VoxelShape SHAPE_X = Shapes.join(
            SHAFT_X,
            box(7, 0, 0, 9, 16, 16),
            (b1, b2) -> b1 || b2);
    private static final VoxelShape SHAPE_Y = Shapes.join(
            SHAFT_Y,
            box(0, 7, 0, 16, 9, 16),
            (b1, b2) -> b1 || b2);
    private static final VoxelShape SHAPE_Z = Shapes.join(
            SHAFT_Z,
            box(0, 0, 7, 16, 16, 9),
            (b1, b2) -> b1 || b2);

//    private static final VoxelShape SHAPE_X_LARGE = Shapes.join(
//            SHAFT_X,
//            box(7, -16, -16, 9, 32, 32),
//            (b1, b2) -> b1 || b2);
//    private static final VoxelShape SHAPE_Y_LARGE = Shapes.join(
//            SHAFT_Y,
//            box(-16, 7, -16, 32, 9, 32),
//            (b1, b2) -> b1 || b2);
//    private static final VoxelShape SHAPE_Z_LARGE = Shapes.join(
//            SHAFT_Z,
//            box(-16, -16, 7, 32, 32, 9),
//            (b1, b2) -> b1 || b2);

    protected ShaftBlock(String name) {
        super(Properties.ofFullCopy(Blocks.IRON_BARS).noOcclusion().setId(TekoraBlocks.BLOCKS.key(name))); // todo, make this flammable if gear_type=="wooden"
        registerDefaultState(defaultBlockState().setValue(AXIS, Direction.Axis.Y).setValue(GEAR_TYPE, GearType.NONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AXIS, GEAR_TYPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState()
                .setValue(AXIS, pContext.getNearestLookingDirection().getAxis()); // todo remove if things don't work
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
        return switch (direction) {
            case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> switch (state.getValue(AXIS)) {
                case X -> state.setValue(AXIS, Direction.Axis.Z);
                case Z -> state.setValue(AXIS, Direction.Axis.X);
                default -> state;
            };
            default -> state;
        };
    }

    @Override
    protected BlockState mirror(BlockState pState, Mirror pMirror) {
        return this.defaultBlockState()
                .setValue(AXIS, pState.getValue(AXIS))
                .setValue(GEAR_TYPE, pState.getValue(GEAR_TYPE));
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (pState.hasProperty(AXIS)) {
            Direction.Axis axis = pState.getValue(AXIS);
            boolean isNone = !pState.hasProperty(GEAR_TYPE) || pState.getValue(GEAR_TYPE).equals(GearType.NONE);
            return switch (axis) { // todo make shape work accordingly
                case X -> isNone ? SHAFT_X : SHAPE_X;
                case Y -> isNone ? SHAFT_Y : SHAPE_Y;
                case Z -> isNone ? SHAFT_Z : SHAPE_Z;
            };
        }
        return SHAPE_Y;
    }

    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL; // todo, add animation
    }

    @Override
    protected InteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if (!pLevel.isClientSide) {
            Item stackItem = pStack.getItem();
            if (stackItem instanceof GearItem gear) {
                pState.setValue(GEAR_TYPE, gear.getGearType());
                pLevel.setBlockAndUpdate(pPos, pState);
                pStack.shrink(1);
            } else if (pState.hasProperty(GEAR_TYPE)) {
                GearType type = pState.getValue(GEAR_TYPE);
                if (pStack.isEmpty() && type != GearType.NONE) {
                    Item item = GearType.itemFromType(type);
                    if (item != null) {
                        pPlayer.setItemInHand(pHand, new ItemStack(item, 1));
                        pState.setValue(GEAR_TYPE, GearType.NONE);
                        pLevel.setBlockAndUpdate(pPos, pState);
                    }
                } else if (GearType.itemMatchesType(stackItem, type) && pStack.getCount() < stackItem.getDefaultMaxStackSize()) {
                    pStack.setCount(pStack.getCount() + 1);
                    pState.setValue(GEAR_TYPE, GearType.NONE);
                    pLevel.setBlockAndUpdate(pPos, pState);
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {
        ArrayList<ItemStack> list = new ArrayList<>();
        if (pState.hasProperty(GEAR_TYPE)) {
            Item gear = GearType.itemFromType(pState.getValue(GEAR_TYPE));
            if (gear != null) {
                list.add(new ItemStack(gear, 1));
            }
        }
        list.add(new ItemStack(pState.getBlock(), 1));
        return list;
    }

    @Override
    protected void onRemove(BlockState pState, Level pLevel, BlockPos pPos,
                            BlockState pNewState, boolean pMovedByPiston) {
        if(pState.getBlock() != pNewState.getBlock() &&
                pLevel.getBlockEntity(pPos) instanceof ShaftEntity) {
            pLevel.updateNeighbourForOutputSignal(pPos, this);
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    public static class Steel extends ShaftBlock {
        public static final MapCodec<Steel> CODEC = simpleCodec(p -> new Steel());

        public Steel() {
            super("steel_gear");
        }

        @Override
        protected MapCodec<? extends BaseEntityBlock> codec() {
            return CODEC;
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
            return new ShaftEntity.Steel(pPos, pState);
        }

        @Override
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
            if(pLevel.isClientSide()) {
                return null;
            }

            return createTickerHelper(pBlockEntityType, TekoraBlockEntities.STEEL_SHAFT_ENTITY.get(),
                    (level, blockPos, blockState, steelShaftEntity) -> steelShaftEntity.tick(level, blockPos, blockState));
        }
    }

    // public static class Vanasteel extends ShaftBlock {}
}
