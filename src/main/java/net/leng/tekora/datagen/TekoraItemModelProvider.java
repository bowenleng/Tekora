package net.leng.tekora.datagen;

import net.leng.tekora.Tekora;
import net.leng.tekora.block.TekoraBlocks;
import net.leng.tekora.item.TekoraItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class TekoraItemModelProvider extends ItemModelProvider {
    public TekoraItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Tekora.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(TekoraItems.BRASS_GEAR.get());
        basicItem(TekoraItems.STEEL_GEAR.get());
        basicItem(TekoraItems.WOODEN_GEAR.get());

        basicItem(TekoraItems.DEBUG_ITEM.get());

        blockAsItem(TekoraBlocks.STEEL_SHAFT);
    }

    private ItemModelBuilder simpleBlockItem(RegistryObject<? extends Block> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(Tekora.MODID,"item/" + item.getId().getPath()));
    }

    private ItemModelBuilder blockAsItem(RegistryObject<? extends Block> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.fromNamespaceAndPath(Tekora.MODID, "block/" + item.getId().getPath()));
    }
}
