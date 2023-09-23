package net.feltmc.neoforge.patches.mixin.client.gui;

import net.minecraft.client.gui.Font;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Font.class)
public class FontMixin implements net.minecraftforge.client.extensions.IForgeFont {
    @Override
    public Font self() {
        return (Font)(Object) this;
    }
}
