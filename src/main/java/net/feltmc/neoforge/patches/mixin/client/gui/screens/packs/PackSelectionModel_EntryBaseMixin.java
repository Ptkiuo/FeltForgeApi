package net.feltmc.neoforge.patches.mixin.client.gui.screens.packs;

import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(PackSelectionModel.EntryBase.class)
public abstract class PackSelectionModel_EntryBaseMixin implements PackSelectionModel.Entry {
	@Shadow
	@Final
	private Pack pack;
	
	@Override
	public boolean notHidden() {
		return !pack.isHidden();
	}
}
