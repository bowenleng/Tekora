package net.leng.tekora.item;

import net.leng.tekora.Tekora;
import net.leng.tekora.block.GearType;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TekoraItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Tekora.MODID);

    public static final RegistryObject<Item> BRASS_GEAR = ITEMS.register("brass_gear", () -> new GearItem(GearType.BRASS, "brass_gear"));
    public static final RegistryObject<Item> STEEL_GEAR = ITEMS.register("steel_gear", () -> new GearItem(GearType.STEEL, "steel_gear"));
    public static final RegistryObject<Item> WOODEN_GEAR = ITEMS.register("wooden_gear", () -> new GearItem(GearType.WOODEN, "wooden_gear"));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
