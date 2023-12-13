package net.feltmc.neoforge.patches.mixin.client.particle;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BreakingItemParticle.class)
public class BreakingItemParticleMixin {
	@Redirect(method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDDLnet/minecraft/world/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/BreakingItemParticle;setSprite(Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"))
	private void setSpriteInject(BreakingItemParticle instance, TextureAtlasSprite textureAtlasSprite, @Local ClientLevel level, @Local ItemStack stack) {
		var model = Minecraft.getInstance().getItemRenderer().getModel(stack, level, (LivingEntity)null, 0);
		
		instance.setSprite(model.getOverrides().resolve(model, stack, level, null, 0).getParticleIcon(ModelData.EMPTY));
	}
}
