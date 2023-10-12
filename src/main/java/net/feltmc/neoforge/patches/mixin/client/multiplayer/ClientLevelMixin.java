package net.feltmc.neoforge.patches.mixin.client.multiplayer;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.storage.WritableLevelData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level implements net.minecraftforge.common.extensions.IForgeLevel {
	@Shadow
	public abstract DimensionSpecialEffects effects();
	
	protected ClientLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l, int i) {
		super(writableLevelData, resourceKey, registryAccess, holder, supplier, bl, bl2, l, i);
	}
	
	@Inject(method = "method_23778", at = @At("RETURN"))
	private void registerBlockTint(Object2ObjectArrayMap object2ObjectArrayMap, CallbackInfo ci) {
		net.minecraftforge.client.ColorResolverManager.registerBlockTintCaches(
			(ClientLevel)(Object) this, object2ObjectArrayMap);
	}
	
	private final it.unimi.dsi.fastutil.ints.Int2ObjectMap<net.minecraftforge.entity.PartEntity<?>> partEntities = new it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap<>();
	private final net.minecraftforge.client.model.data.ModelDataManager modelDataManager = new net.minecraftforge.client.model.data.ModelDataManager(this);
	
	@Inject(method = "<init>(Lnet/minecraft/client/multiplayer/ClientPacketListener;" +
		"Lnet/minecraft/client/multiplayer/ClientLevel$ClientLevelData;Lnet/minecraft/resources/ResourceKey;" +
		"Lnet/minecraft/core/Holder;IILjava/util/function/Supplier;Lnet/minecraft/client/renderer/LevelRenderer;ZJ)V",
		at = @At("RETURN"))
	private void thiz(ClientPacketListener clientPacketListener, ClientLevel.ClientLevelData clientLevelData, ResourceKey resourceKey, Holder holder, int i, int j, Supplier supplier, LevelRenderer levelRenderer, boolean bl, long l, CallbackInfo ci) {
		this.gatherCapabilities();
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.level.LevelEvent.Load(this));
	}
	
	@WrapWithCondition(method = "tickNonPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"))
	private boolean tickNonPassenger$ifUpdatable(Entity entity) {
		return entity.canUpdate();
	}
	
	@WrapWithCondition(method = "addEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;removeEntity(ILnet/minecraft/world/entity/Entity$RemovalReason;)V"))
	private boolean addEntity$cancelRemove(ClientLevel instance, int i, Entity.RemovalReason reason, 
	                                       @Local(ordinal = 0) Entity entity) {
		return net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityJoinLevelEvent(entity, instance));
	}
	
	@Inject(method = "addEntity", at = @At("RETURN"))
	private void addEntity$event(int i, Entity entity, CallbackInfo ci) {
		entity.onAddedToWorld();
	}
	
	@Inject(method = "playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/core/Holder;" +
		"Lnet/minecraft/sounds/SoundSource;FFJ)V", at = @At("HEAD"), cancellable = true)
	private void playSeededSound$event(@Nullable Player player, double d, double e, double f,
	                                   Holder<SoundEvent> holder, SoundSource soundSource,
	                                   float g, float h, long l, CallbackInfo ci,
	                                   @Local(ordinal = 0) LocalRef<@Nullable Player> p_263381_,
	                                   @Local(ordinal = 0) LocalDoubleRef p_263372_,
	                                   @Local(ordinal = 1) LocalDoubleRef p_263404_,
	                                   @Local(ordinal = 2) LocalDoubleRef p_263365_,
	                                   @Local(ordinal = 0) LocalRef<Holder<SoundEvent>> p_263335_,
	                                   @Local(ordinal = 0) LocalRef<SoundSource> p_263417_,
	                                   @Local(ordinal = 0) LocalFloatRef p_263416_,
	                                   @Local(ordinal = 1) LocalFloatRef p_263349_,
	                                   @Local(ordinal = 0) LocalLongRef p_263408_) {
		net.minecraftforge.event.PlayLevelSoundEvent.AtPosition event = net.minecraftforge.event.ForgeEventFactory.onPlaySoundAtPosition(
			this, p_263372_.get(), p_263404_.get(),
			p_263365_.get(), p_263335_.get(), p_263417_.get(),
			p_263416_.get(), p_263349_.get()
		);
		
		if (event.isCanceled() || event.getSound() == null) ci.cancel();
		else {
			p_263335_.set(event.getSound());
			p_263417_.set(event.getSource());
			p_263416_.set(event.getNewVolume());
			p_263349_.set(event.getNewPitch());
		}
	}
	
	@Inject(method = "playSeededSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V", at = @At("HEAD"), cancellable = true)
	private void playSeededSound$event(@Nullable Player player, Entity entity, Holder<SoundEvent> holder,
	                                   SoundSource soundSource, float f, float g, long l, CallbackInfo ci,
	                                   @Local(ordinal = 0) LocalRef<@Nullable Player> p_263514_,
	                                   @Local(ordinal = 0) LocalRef<Entity> p_263536_, 
	                                   @Local(ordinal = 0) LocalRef<Holder<SoundEvent>> p_263518_,
	                                   @Local(ordinal = 0) LocalRef<SoundSource> p_263487_,
	                                   @Local(ordinal = 0) LocalFloatRef p_263538_,
	                                   @Local(ordinal = 1) LocalFloatRef p_263524_,
	                                   @Local(ordinal = 0) LocalLongRef p_263509_) {
		net.minecraftforge.event.PlayLevelSoundEvent.AtEntity event = net.minecraftforge.event.ForgeEventFactory.onPlaySoundAtEntity(
			p_263536_.get(), 
			p_263518_.get(), 
			p_263487_.get(), 
			p_263538_.get(), 
			p_263524_.get()
		);
		
		if (event.isCanceled() || event.getSound() == null) ci.cancel();
		else {
			p_263518_.set(event.getSound());
			p_263487_.set(event.getSource());
			p_263538_.set(event.getNewVolume());
			p_263524_.set(event.getNewPitch());
		}
	}
	
	@Mixin(ClientLevel.ClientLevelData.class)
	public static class ClientLevelDataMixin {
		@Shadow
		private Difficulty difficulty;
		
		@Inject(method = "setDifficulty", at = @At("HEAD"))
		private void onDifficultyChange(Difficulty difficulty, CallbackInfo ci) {
			net.minecraftforge.common.ForgeHooks.onDifficultyChange(difficulty, this.difficulty);
		}
	}
	
	@Mixin(ClientLevel.EntityCallbacks.class)
	public static abstract class EntityCallbacksMixin implements LevelCallback<Entity> {
		@Shadow
		@Final
		ClientLevel field_27735;
		
		@Inject(method = "onTrackingStart(Lnet/minecraft/world/entity/Entity;)V", at = @At("RETURN"))
		private void onTrackingStart$event(Entity entity, CallbackInfo ci) {
			if (entity.isMultipartEntity()) {
				for (net.minecraftforge.entity.PartEntity<?> part : entity.getParts()) {
					this.field_27735.partEntities.put(part.getId(), part);
				}
			}
		}
		
		@Inject(method = "onTrackingEnd(Lnet/minecraft/world/entity/Entity;)V", at = @At("RETURN"))
		private void onTrackingEnd$event(Entity entity, CallbackInfo ci) {
			entity.onRemovedFromWorld();
			net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityLeaveLevelEvent(
				entity, this.field_27735));
			
			if (entity.isMultipartEntity()) {
				for (net.minecraftforge.entity.PartEntity<?> part : entity.getParts()) {
					this.field_27735.partEntities.remove(part.getId());
				}
			}
		}
	}
	
	@Override
	public java.util.Collection<net.minecraftforge.entity.PartEntity<?>> getPartEntities() {
		return this.partEntities.values();
	}
	
	@Override
	public net.minecraftforge.client.model.data.ModelDataManager getModelDataManager() {
		return modelDataManager;
	}
	
	@Override
	public float getShade(float normalX, float normalY, float normalZ, boolean shade) {
		boolean constantAmbientLight = this.effects().constantAmbientLight();
		if (!shade) return constantAmbientLight ? 0.9F : 1.0F;
		return net.minecraftforge.client.model.lighting.QuadLighter.calculateShade(normalX, normalY, normalZ, constantAmbientLight);
	}
}
