package net.leng.tekora.block;

import net.leng.tekora.item.GearItem;
import net.leng.tekora.item.TekoraItems;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;

public enum GearType implements StringRepresentable {
    BRASS(false),
    NONE( false),
    STEEL(false),
    WOODEN(true);

    private final boolean flammable;
    GearType(boolean flammable) {
        this.flammable = flammable;
    }

    public boolean isFlammable() {
        return flammable;
    }

    @Override
    public String getSerializedName() {
        return toString().toLowerCase();
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
