package net.feltmc.neoforge.patches.mixin.client.gui.screens.controls;

import net.minecraft.client.gui.screens.controls.KeyBindsList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(KeyBindsList.KeyEntry.class)
public abstract class KeyBindsList$KeyEntryMixin extends KeyBindsList.Entry {
    @ModifyArg(method = "<init>", index = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/Button$Builder;bounds(IIII)Lnet/minecraft/client/gui/components/Button$Builder;", ordinal = 0))
    private int modifyBounds(int i) {
        return i + 20;
    }
}
