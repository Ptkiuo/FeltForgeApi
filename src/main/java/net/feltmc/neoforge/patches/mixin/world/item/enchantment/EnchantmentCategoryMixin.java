package net.feltmc.neoforge.patches.mixin.world.item.enchantment;

import fr.catcore.cursedmixinextensions.annotations.NewConstructor;
import fr.catcore.cursedmixinextensions.annotations.ShadowConstructor;
import net.feltmc.feltasm.asm.CreateStatic;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.IExtensibleEnum;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Predicate;

@SuppressWarnings("MissingUnique")
@Mixin(EnchantmentCategory.class)
public abstract class EnchantmentCategoryMixin implements IExtensibleEnum {
    private Predicate<Item> delegate;

    // TODO felt: investigate necessity of no-arg constructor (incompatible with enum)
    
    @ShadowConstructor
    public abstract void thiz(String key, int value);
    
    @NewConstructor
    private void thiz(String key, int value, Predicate<Item> delegate) {
        thiz(key, value);
        this.delegate = delegate;
    }

    @CreateStatic
    public EnchantmentCategory create(String name, Predicate<Item> delegate) {
        throw new IllegalStateException("Enum not extended");
    }

    public boolean canEnchant(Item stack) {
        return this.delegate != null && this.delegate.test(stack);
    }
}
