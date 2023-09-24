package net.feltmc.neoforge.patches.mixin.client.gui.components;

import com.llamalad7.mixinextras.sugar.Local;
import fr.catcore.cursedmixinextensions.annotations.Public;
import net.feltmc.neoforge.patches.interfaces.AbstractWidgetInterface;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin implements net.minecraftforge.client.extensions.IAbstractWidgetExtension, AbstractWidgetInterface {
    @Shadow protected int height;

    @Shadow public boolean active;

    @Redirect(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractWidget;onClick(DD)V"))
    private void redirectOnClick(AbstractWidget instance, double d, double e, @Local(ordinal = 0) int i) {
        this.onClick(d, e, i);
    }

    @Override
    public void setHeight(int value) {
        this.height = value;
    }

    @Public
    private static final int UNSET_FG_COLOR = -1;
    protected int packedFGColor = UNSET_FG_COLOR;

    @Override
    public int getFGColor() {
        if (packedFGColor != UNSET_FG_COLOR) return packedFGColor;
        return this.active ? 16777215 : 10526880; // White : Light Grey
    }

    @Override
    public void setFGColor(int color) {
        this.packedFGColor = color;
    }

    @Override
    public void clearFGColor() {
        this.packedFGColor = UNSET_FG_COLOR;
    }
}
