package net.feltmc.neoforge.patches.mixin.client.gui.screens.inventory;

import net.feltmc.neoforge.patches.interfaces.CreativeModeInventoryScreen_SlotWrapperInterface;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CreativeModeInventoryScreen.SlotWrapper.class)
public class CreativeModeInventoryScreen_SlotWrapperMixin extends Slot implements CreativeModeInventoryScreen_SlotWrapperInterface {
	@Shadow
	@Final
	Slot target;
	
	public CreativeModeInventoryScreen_SlotWrapperMixin(Container container, int i, int j, int k) {
		super(container, i, j, k);
	}
	
	@Override
	public int getSlotIndex() {
		return this.target.getSlotIndex();
	}
	
	@Override
	public boolean isSameInventory(Slot other) {
		return this.target.isSameInventory(other);
	}
	
	@Override
	public Slot setBackground(ResourceLocation atlas, ResourceLocation sprite) {
		this.target.setBackground(atlas, sprite);
		return this;
	}
}
