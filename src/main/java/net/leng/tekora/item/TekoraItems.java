package net.leng.tekora.item;

import net.leng.tekora.Tekora;
import net.leng.tekora.block.GearType;
import net.leng.tekora.blockent.shaft.ShaftEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TekoraItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Tekora.MODID);

    public static final RegistryObject<Item> BRASS_GEAR = ITEMS.register("brass_gear", () -> new GearItem(GearType.BRASS, "brass_gear"));
    public static final RegistryObject<Item> STEEL_GEAR = ITEMS.register("steel_gear", () -> new GearItem(GearType.STEEL, "steel_gear"));
    public static final RegistryObject<Item> WOODEN_GEAR = ITEMS.register("wooden_gear", () -> new GearItem(GearType.WOODEN, "wooden_gear"));
    public static final RegistryObject<Item> DEBUG_ITEM = ITEMS.register("debug_item", () -> new Item(new Item.Properties().setId(ITEMS.key("debug_item"))) {
        @Override
        public InteractionResult useOn(UseOnContext pContext) {
            Level level = pContext.getLevel();
            if (!level.isClientSide) {
                BlockPos pos = pContext.getClickedPos();
                BlockEntity blockEnt = level.getBlockEntity(pos);
                Player player = pContext.getPlayer();
                if (player != null && blockEnt instanceof ShaftEntity shaft) {
                    player.displayClientMessage(Component.literal("Shaft RPM: " + shaft.getRpm()), false);
                }
            }
            return super.useOn(pContext);
        }

        @Override
        public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
            Level level = context.getLevel();
            if (!level.isClientSide) {
                BlockPos pos = context.getClickedPos();
                BlockEntity blockEnt = level.getBlockEntity(pos);
                Player player = context.getPlayer();
                if (player != null && blockEnt instanceof ShaftEntity shaft) {
                    player.displayClientMessage(Component.literal("Shaft RPM: " + shaft.getRpm()), false);
                }
            }
            return super.onItemUseFirst(stack, context);
        }
    });

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
