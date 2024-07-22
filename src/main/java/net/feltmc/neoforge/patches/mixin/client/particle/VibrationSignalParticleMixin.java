package net.feltmc.neoforge.patches.mixin.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.VibrationSignalParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VibrationSignalParticle.class)
public abstract class VibrationSignalParticleMixin extends TextureSheetParticle {
	
	protected VibrationSignalParticleMixin(ClientLevel clientLevel, double d, double e, double f) {
		super(clientLevel, d, e, f);
	}
	
	@Inject(method = "tick()V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle" +
		"/VibrationSignalParticle;z:D", shift = At.Shift.AFTER))
	private void tickInject(CallbackInfo ci){
		this.setPos(this.x, this.y, this.z);
	}
}