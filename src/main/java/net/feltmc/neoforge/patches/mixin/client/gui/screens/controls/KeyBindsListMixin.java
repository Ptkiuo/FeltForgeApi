package net.feltmc.neoforge.patches.mixin.client.gui.screens.controls;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(KeyBindsList.class)
public class KeyBindsListMixin extends ContainerObjectSelectionList<KeyBindsList.Entry> {
    public KeyBindsListMixin(Minecraft minecraft, int i, int j, int k, int l, int m) {
        super(minecraft, i, j, k, l, m);
    }

    final static int NAME_SPLIT_LENGTH = 185;

    @ModifyVariable(method = "<init>", ordinal = 0, at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/gui/screens/controls/KeyBindsList;maxNameWidth:I"))
    private int maxNameLength(int i) {
        return Math.min(i, NAME_SPLIT_LENGTH);
    }

    @ModifyReturnValue(method = "getScrollbarPosition", at = @At("RETURN"))
    private int getScrollbarPositionSpace(int old) {
        return old + 20;
    }
}
