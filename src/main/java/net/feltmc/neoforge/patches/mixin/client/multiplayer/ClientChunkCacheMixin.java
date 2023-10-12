package net.feltmc.neoforge.patches.mixin.client.multiplayer;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(ClientChunkCache.class)
public abstract class ClientChunkCacheMixin extends ChunkSource {
	@Inject(method = "drop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientChunkCache$Storage;replace(ILnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/chunk/LevelChunk;)Lnet/minecraft/world/level/chunk/LevelChunk;"))
	private void chunkUnloadEvent(int i, int j, CallbackInfo ci, 
	                              @Local(ordinal = 0)LevelChunk levelchunk) {
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.level.ChunkEvent.Unload(levelchunk));
	}
	
	@Inject(method = "replaceWithPacketData", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/multiplayer/ClientLevel;onChunkLoaded(Lnet/minecraft/world/level/ChunkPos;)V"))
	private void chunkLoadEvent(int i, int j, FriendlyByteBuf friendlyByteBuf, CompoundTag compoundTag,
	                            Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> consumer, 
	                            CallbackInfoReturnable<@Nullable LevelChunk> cir,
	                            @Local(ordinal = 0)LevelChunk levelchunk) {
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.level.ChunkEvent.Load(levelchunk, false));
	}
}
