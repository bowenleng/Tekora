package net.nukollodda.tekora.block.entity.entities.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.nukollodda.tekora.block.WrappedHandler;
import net.nukollodda.tekora.block.entity.blocks.machines.AlloyFurnace;
import net.nukollodda.tekora.block.entity.entities.TekoraBlockEntities;
import net.nukollodda.tekora.block.entity.entities.machines.types.AbstractTekoraFurnaceEntity;
import net.nukollodda.tekora.menu.AlloyFurnaceMenu;
import net.nukollodda.tekora.recipes.types.AlloyingRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;


public class AlloyFurnaceEntity extends AbstractTekoraFurnaceEntity {
    private final ItemStackHandler itemHandler = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged(); // if a change happens to this block, the block gets reloaded
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> FurnaceBlockEntity.isFuel(stack);
                case 1, 2, 3 -> true;
                case 4 -> false;
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    // in these set of lambdas,
    // 'i' stands for either slot id or index
    // 's' stands for slot
    private final Map<Direction, LazyOptional<WrappedHandler>> directionHandlerMap =
            Map.of(Direction.DOWN, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 4, (i, s) -> false)),
                    Direction.NORTH, LazyOptional.of(() -> new WrappedHandler(itemHandler,
                            (i) -> i == 2, (i, s) -> itemHandler.isItemValid(3, s))),

                    Direction.SOUTH, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 4, (i, s) -> false)),
                    Direction.EAST, LazyOptional.of(() -> new WrappedHandler(itemHandler,
                            (i) -> i == 1, (i, s) -> itemHandler.isItemValid(1, s))),

                    Direction.WEST, LazyOptional.of(() -> new WrappedHandler(itemHandler,
                            (i) -> i == 2, (i, s) -> itemHandler.isItemValid(2, s))),

                    Direction.UP, LazyOptional.of(() -> new WrappedHandler(itemHandler,
                            (i) -> i == 2, (i, s) -> itemHandler.isItemValid(0, s))));
    /*
    * "north" is always going to be the away side
    * "south" is always going to be the side facing the player
    * "east" is always the right
    * "west" is always the left
    *
    * the second statement always determines whether we can or can't extract from a certain slot
    */

    public AlloyFurnaceEntity(BlockPos pos, BlockState blockState) {
        super(TekoraBlockEntities.ALLOY_FURNACE.get(), pos, blockState, 5);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.tekora.alloy_furnace");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new AlloyFurnaceMenu(id, inv, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == null) return lazyItemHandler.cast();

            if (directionHandlerMap.containsKey(side)) {
                Direction local = this.getBlockState().getValue(AlloyFurnace.FACING);
                if (side == Direction.UP || side == Direction.DOWN) {
                    return directionHandlerMap.get(side).cast();
                }
                return switch (local) {
                    case NORTH -> directionHandlerMap.get(side.getOpposite()).cast();
                    case EAST -> directionHandlerMap.get(side.getClockWise()).cast();
                    case WEST -> directionHandlerMap.get(side.getCounterClockWise()).cast();
                    default -> directionHandlerMap.get(side).cast();
                };
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("alloy_furnace.progress", this.progress);
        tag.putInt("alloy_furnace.fuel", this.fuel);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        this.progress = tag.getInt("alloy_furnace.progress");
        this.fuel = tag.getInt("alloy_furnace.fuel");
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inv.setItem(i, itemHandler.getStackInSlot(i));
        }
        // drops all the contents of this particular block entity

        Containers.dropContents(this.level, this.worldPosition, inv);
    }
    public static void tick(Level level, BlockPos pos, BlockState state, AlloyFurnaceEntity entity) {
        if (level.isClientSide()) {
            return;
        } // if a recipe exists, the tick does something

        ItemStack itemFuel = new ItemStack(entity.itemHandler.getStackInSlot(0).getItem());

        if (entity.fuel > 0)
            entity.fuel--;

        if (entity.hasRecipe()) {
            if (FurnaceBlockEntity.isFuel(itemFuel) && entity.fuel == 0) {
                entity.maxFuel = ForgeHooks.getBurnTime(itemFuel, RecipeType.BLASTING);
                entity.fuel = entity.maxFuel;
                entity.itemHandler.extractItem(0, 1, false);
            }

            if (entity.fuel > 0) {
                entity.progress++;
            }
            setChanged(level, pos, state);

            if (entity.progress > entity.maxProgress) { // crafts the item
                entity.craftItem();
            }

        } else {
            entity.resetProgress();
            setChanged(level, pos, state);
        }
    }

    protected void craftItem() { // extracts one of the ingredients
        Level level = this.level;
        SimpleContainer inv = new SimpleContainer(this.itemHandler.getSlots());
        for (int i = 0; i < this.itemHandler.getSlots(); i++) {
            inv.setItem(i, this.itemHandler.getStackInSlot(i));
        }

        Optional<AlloyingRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(AlloyingRecipe.Type.INSTANCE, inv, level);

        if (this.hasRecipe()) {
            int[] extracted = new int[]{0, 0, 0}; // how much is removed for every slot

            ItemStack prevItem;
            ItemStack curItem;
            ItemStack postItem;
            Ingredient ing;
            int ingNum;
            int totalRemove;
            int avNum;

            for (int i = 0; i < recipe.get().getIngredients().size(); i++) {
                ing = recipe.get().getIngredients().get(i);
                ingNum = recipe.get().getRecipeRatio().get(i);
                for (int j = 1; j < 4; j++) {
                    curItem = this.itemHandler.getStackInSlot(j);
                    prevItem = this.itemHandler.getStackInSlot(j-1 >= 1 ? j-1 : 3);
                    postItem = this.itemHandler.getStackInSlot(j+1 < 4 ? j+1 : 1);
                    if (ing.test(curItem)) {
                        if (prevItem.is(curItem.getItem()) && prevItem.getCount() + curItem.getCount() >= ingNum) {
                            totalRemove = Math.min(prevItem.getCount(), ingNum);
                            avNum = totalRemove / 2;
                            extracted[j-2 >= 0 ? j-2 : 2] = totalRemove % 2 == 0 ? avNum : avNum+1;
                            extracted[j-1] = avNum;
                        } else if (postItem.is(curItem.getItem()) && postItem.getCount() + curItem.getCount() >= ingNum) {
                            totalRemove = Math.min(postItem.getCount(), ingNum);
                            avNum = totalRemove / 2;
                            extracted[j-1] = totalRemove % 2 == 0 ? avNum : avNum+1;
                            extracted[j < 3 ? j : 0] = avNum;
                        } else if (curItem.getCount() >= ingNum) {
                            extracted[j-1] = ingNum;
                        }
                        // there is a bug where if two items are in the top slots, avNum is ignored somehow
                    }
                }
            }

            for (int i = 1; i < 4; i++) {
                if (!this.itemHandler.getStackInSlot(i).isEmpty()) this.itemHandler.extractItem(i, extracted[i-1], false);
                // basically, removes items if said items adds up to the recipe number
            }
            // slot organization, slot 0 = coal input, slot 1-2 = item inputs, slot 3 = output, for the electric variant, slot 1 = residue
            this.itemHandler.setStackInSlot(this.containerSize - 1, new ItemStack(recipe.get().getResultItem(level.registryAccess()).getItem(),
                    this.itemHandler.getStackInSlot(this.containerSize - 1).getCount() +
                            recipe.get().getResultItem(level.registryAccess()).getCount()));

            this.resetProgress();
        }
    }

    protected boolean hasRecipe() {
        Level level = this.level;
        SimpleContainer inv = new SimpleContainer(this.itemHandler.getSlots()); // makes an inventory from the block
        for (int i = 0; i < this.itemHandler.getSlots(); i++) {
            inv.setItem(i, this.itemHandler.getStackInSlot(i));
        }

        Optional<AlloyingRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(AlloyingRecipe.Type.INSTANCE, inv, level);

        return recipe.isPresent() && this.canInsertAmountIntoOutputSlot(inv) &&
                this.canInsertItemIntoOutputSlot(inv, recipe.get().getResultItem(level.registryAccess()));
    }
}