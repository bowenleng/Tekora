package net.leng.tekora.block;

import net.leng.tekora.item.GearItem;
import net.leng.tekora.item.TekoraItems;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;

public enum GearType implements StringRepresentable {
    BRASS("brass", false),
    NONE("none", false),
    STEEL("steel", false),
    WOODEN("wooden", true);

    private final String name;
    private final boolean flammable;
    GearType(String name, boolean flammable) {
        this.name = name;
        this.flammable = flammable;
    }

    public boolean isFlammable() {
        return flammable;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public static Item itemFromType(GearType type) {
        return switch (type) {
            case BRASS -> TekoraItems.BRASS_GEAR.get();
            case STEEL -> TekoraItems.STEEL_GEAR.get();
            case WOODEN -> TekoraItems.WOODEN_GEAR.get();
            default -> null;
        };
    }

    public static boolean itemMatchesType(Item item, GearType type) {
        if (item instanceof GearItem gear) {
            return gear.getGearType().equals(type);
        }
        return false;
    }
}
