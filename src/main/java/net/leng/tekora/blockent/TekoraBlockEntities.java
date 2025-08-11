package net.leng.tekora.blockent;

import net.leng.tekora.Tekora;
import net.leng.tekora.block.TekoraBlocks;
import net.leng.tekora.blockent.shaft.ShaftEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class TekoraBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Tekora.MODID);

    public static final RegistryObject<BlockEntityType<ShaftEntity>> STEEL_SHAFT_ENTITY =
            BLOCK_ENTITIES.register("steel_shaft", () -> new BlockEntityType<>(
                    ShaftEntity.Steel::new, Set.of(TekoraBlocks.STEEL_SHAFT.get())));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

}
