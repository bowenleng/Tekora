package net.leng.tekora.block;

import net.leng.tekora.Tekora;
import net.leng.tekora.block.shaft.ShaftBlock;
import net.leng.tekora.item.TekoraItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class TekoraBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Tekora.MODID);

    public static final RegistryObject<Block> STEEL_SHAFT = createBlockWithItem("steel_shaft", ShaftBlock.Steel::new);

    private static RegistryObject<Block> createBlockWithItem(String name, Supplier<? extends Block> factory) {
        RegistryObject<Block> block = BLOCKS.register(name, factory);
        TekoraItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().setId(TekoraItems.ITEMS.key(name))));
        return block;
    }
    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
