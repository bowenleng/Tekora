package net.nukollodda.tekora.item.isotopic.radioactive;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.nukollodda.tekora.item.isotopic.AbstractIsotopicItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Thorium extends AbstractRadioactiveItem {
    private int[] isotopeRatio;
    public Thorium() {
        super(new Item.Properties().fireResistant());
    }

    public static AbstractRadioactiveItem.Isotopes[] getIsotopeList() {
        return new AbstractRadioactiveItem.Isotopes[]{Isotopes.TH232};
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("msg.tekora.rad"));
        pTooltipComponents.add(Component.literal(AbstractRadioactiveItem.formatRad(getRadiation()))
                .withStyle(AbstractRadioactiveItem.radColor(getRadiation())));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public double getRadiation() {
        return Isotopes.TH232.getRadiationVal();
    }

    @Override
    public double getFissionRate() {
        return Isotopes.TH232.getFissionRate();
    }

    @Override
    public double getNeutronAbsorptionRate() {
        return Isotopes.TH232.getNeutronAbsorptionRate();
    }

    @Override
    public float getFissionEnergy() {
        return Isotopes.TH232.getFissionEnergy();
    }

    @Override
    public float getNeutronAbsorptionEnergy() {
        return Isotopes.TH232.getNeutronAbsorptionEnergy();
    }

    public enum Isotopes implements AbstractRadioactiveItem.Isotopes {
        TH232();

        public int getId() {
            return 0;
        }

        @Override
        public double getRadiationVal() {
            return 0.000021;
        }

        @Override
        public double getFissionRate() {
            return 0.01;
        }

        @Override
        public double getNeutronAbsorptionRate() {
            return 0.96;
        }

        @Override
        public float getFissionEnergy() {
            return 20;
        }

        @Override
        public float getNeutronAbsorptionEnergy() {
            return 1942f;
        }

        @Override
        public AbstractRadioactiveItem.Isotopes getPostAbsorptionIsotope() {
            return Uranium.Isotopes.U233;
        }
    }

    public static class Nugget extends Thorium {
        @Override
        public double getRadiation() {
            return super.getRadiation() / 9;
        }

        @Override
        public float getFissionEnergy() {
            return super.getFissionEnergy() / 9;
        }

        @Override
        public float getNeutronAbsorptionEnergy() {
            return super.getNeutronAbsorptionEnergy() / 9;
        }
    }

    public static class CompoundDust extends Thorium {
        private final Item decayItem;

        public CompoundDust(Item item) {
            this.decayItem = item;
        }

        public Item getDecayItem() {
            return decayItem;
        }
    }
}
