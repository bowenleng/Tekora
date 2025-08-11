package net.leng.tekora.item;

import net.leng.tekora.block.GearType;
import net.minecraft.world.item.Item;

public class GearItem extends Item {
    private final GearType gearType;
    public GearItem(GearType gearType, String name) {
        super(createProperties(name, gearType.isFlammable()));
        this.gearType = gearType;
    }

    public GearType getGearType() {
        return gearType;
    }

    private static Properties createProperties(String name, boolean flammable) {
        Properties basic = new Properties().setId(TekoraItems.ITEMS.key(name));
        return flammable ? basic.fireResistant() : basic;
    }
}
