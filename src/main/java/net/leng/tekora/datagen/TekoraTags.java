package net.leng.tekora.datagen;

import net.leng.tekora.Tekora;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class TekoraTags {
    public static class Blocks {
        private static TagKey<Block> createMinecraftTag(String name) {
            return BlockTags.create(ResourceLocation.withDefaultNamespace(name));
        }

        private static TagKey<Block> createForgeTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("forge", name));
        }

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(Tekora.MODID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> GEARS = createForgeTag("gears");
        public static final TagKey<Item> BRASS_GEAR = createForgeTag("gears/brass");
        public static final TagKey<Item> STEEL_GEAR = createForgeTag("gears/steel");
        public static final TagKey<Item> WOODEN_GEAR = createForgeTag("gears/wooden");

        private static TagKey<Item> createMinecraftTag(String name) {
            return ItemTags.create(ResourceLocation.withDefaultNamespace(name));
        }

        private static TagKey<Item> createForgeTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", name));
        }

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(Tekora.MODID, name));
        }
    }
}
