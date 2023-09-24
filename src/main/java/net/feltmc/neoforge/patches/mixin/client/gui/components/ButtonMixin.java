package net.feltmc.neoforge.patches.mixin.client.gui.components;

import fr.catcore.cursedmixinextensions.annotations.NewConstructor;
import fr.catcore.cursedmixinextensions.annotations.ShadowConstructor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Button.class)
public abstract class ButtonMixin extends AbstractButton {
    public ButtonMixin(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    @ShadowConstructor
    abstract void ctr(int i, int j, int k, int l, Component component, Button.OnPress onPress, Button.CreateNarration createNarration);

    @NewConstructor
    protected void ctr(Button.Builder builder) {
        this.ctr(builder.x, builder.y, builder.width, builder.height, builder.message, builder.onPress, builder.createNarration);
        this.setTooltip(builder.tooltip); // Forge: Make use of the Builder tooltip
    }
}
