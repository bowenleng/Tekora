package net.nukollodda.tekora.item.tools.thorium;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.nukollodda.tekora.item.IRadioactiveItems;
import net.nukollodda.tekora.item.isotopic.radioactive.Thorium;
import net.nukollodda.tekora.tiers.TekoraTiers;
import org.jetbrains.annotations.NotNull;

public class ThoriumPickaxe extends PickaxeItem implements IRadioactiveItems {
    public ThoriumPickaxe() {
        super(TekoraTiers.THORIUM, 1, -2.8f, new Item.Properties().fireResistant());
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, @NotNull LivingEntity pTarget, @NotNull LivingEntity pAttacker) {
        pStack.hurtAndBreak(1, pTarget, i -> i.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        pTarget.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 300, 0, true, false, false));
        pTarget.addEffect(new MobEffectInstance(MobEffects.WITHER, 600, 0, true, false, false));
        pTarget.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 1050, 0, true, false, false));
        pTarget.addEffect(new MobEffectInstance(MobEffects.HUNGER, 1350, 0, true, false, false));
        pTarget.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 1500, 0, true, false, false));
        pTarget.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 1500, 0, true, false, false));
        return true;
    }

    @Override
    public double getRadiation() {
        return Thorium.Isotopes.TH232.getRadiationVal();
    }
}
