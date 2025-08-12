package net.leng.tekora.datagen;

import net.leng.tekora.Tekora;
import net.leng.tekora.item.TekoraItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class TekoraItemTagProvider extends ItemTagsProvider {
    public TekoraItemTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> completableFuture,
                                 CompletableFuture<TagLookup<Block>> lookupCompletableFuture, ExistingFileHelper existingFileHelper) {
        super(packOutput, completableFuture, lookupCompletableFuture, Tekora.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(TekoraTags.Items.BRASS_GEAR).add(TekoraItems.BRASS_GEAR.get());
        tag(TekoraTags.Items.STEEL_GEAR).add(TekoraItems.STEEL_GEAR.get());
        tag(TekoraTags.Items.WOODEN_GEAR).add(TekoraItems.WOODEN_GEAR.get());
        tag(TekoraTags.Items.GEARS).addTags(
                TekoraTags.Items.BRASS_GEAR,
                TekoraTags.Items.STEEL_GEAR,
                TekoraTags.Items.WOODEN_GEAR
        );
    }
}
