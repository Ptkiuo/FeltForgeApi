package net.feltmc.neoforge.patches.mixin.client.gui.screens.packs;

import net.feltmc.neoforge.patches.interfaces.PackSelectionModelInterface;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PackSelectionModel.Entry.class)
public interface PackSelectionModel_EntryMixin extends PackSelectionModelInterface.EntryInterface {
}
