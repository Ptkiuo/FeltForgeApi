package net.feltmc.neoforge.patches.mixin.world.inventory;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("MissingUnique")
@Mixin(Slot.class)
public class SlotMixin {
	
	@Shadow
	@Final
	private int slot;
	
	@Shadow
	@Final
	public Container container;
	
	@Inject(method = "getNoItemIcon", at = @At("TAIL"), cancellable = true)
	public void getNoItemIcon$TAIL(CallbackInfoReturnable<@Nullable Pair<ResourceLocation, ResourceLocation>> cir) {
		cir.setReturnValue(backgroundPair);
	}
	
	/**
	 * Retrieves the index in the inventory for this slot, this value should typically not
	 * be used, but can be useful for some occasions.
	 *
	 * @return Index in associated inventory for this slot.
	 */
	public int getSlotIndex() {
		return slot;
	}
	
	/**
	 * Checks if the other slot is in the same inventory, by comparing the inventory reference.
	 * @param other _
	 * @return true if the other slot is in the same inventory
	 */
	public boolean isSameInventory(Slot other) {
		return this.container == other.container;
	}
	
	private Pair<ResourceLocation, ResourceLocation> backgroundPair;

	/**
	 * Sets the background atlas and sprite location.
	 *
	 * @param atlas The atlas name
	 * @param sprite The sprite located on that atlas.
	 * @return this, to allow chaining.
	 */
	public Slot setBackground(ResourceLocation atlas, ResourceLocation sprite) {
		this.backgroundPair = Pair.of(atlas, sprite);
		return (Slot) (Object) this;
	}
	
}
