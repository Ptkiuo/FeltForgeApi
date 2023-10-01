package net.feltmc.neoforge.patches.mixin.core;

import net.feltmc.neoforge.patches.interfaces.Holder_ReferenceInterface;
import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(Holder.Reference.class)
public class Holder_ReferenceMixin<T> implements Holder_ReferenceInterface<T> {
	
	@Shadow @Final private Holder.Reference.Type type;
	
	@Override
	public Holder.Reference.Type getType() {
		return type;
	}
	
}
