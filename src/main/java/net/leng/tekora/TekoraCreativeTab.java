package net.leng.tekora;

import net.leng.tekora.block.TekoraBlocks;
import net.leng.tekora.item.TekoraItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class TekoraCreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Tekora.MODID);

    public static final RegistryObject<CreativeModeTab> COMPONENTS_TAB = TABS.register("components", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .title(Component.translatable("creativetab.tekora.components"))
            .icon(() -> TekoraItems.STEEL_GEAR.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(TekoraBlocks.STEEL_SHAFT.get());
                output.accept(TekoraItems.BRASS_GEAR.get());
                output.accept(TekoraItems.STEEL_GEAR.get());
                output.accept(TekoraItems.WOODEN_GEAR.get());
            }).build());

    public static void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
    }
}
