package net.leng.tekora.datagen;

import net.leng.tekora.Tekora;
import net.leng.tekora.item.TekoraItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class TekoraItemModelProvider extends ItemModelProvider {
    public TekoraItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Tekora.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(TekoraItems.BRASS_GEAR.get());
        basicItem(TekoraItems.STEEL_GEAR.get());
        basicItem(TekoraItems.WOODEN_GEAR.get());
    }
}
