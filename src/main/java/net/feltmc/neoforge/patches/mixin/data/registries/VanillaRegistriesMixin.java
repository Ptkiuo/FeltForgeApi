package net.feltmc.neoforge.patches.mixin.data.registries;

import net.feltmc.feltasm.asm.CreateStatic;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@SuppressWarnings("MissingUnique")
@Mixin(VanillaRegistries.class)
public class VanillaRegistriesMixin {
    
    @Shadow @Final
    private static RegistrySetBuilder BUILDER;
    
    @CreateStatic
    private static List<? extends ResourceKey<? extends Registry<?>>> DATAPACK_REGISTRY_KEYS;
    
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void ztatic(CallbackInfo ci) {
        DATAPACK_REGISTRY_KEYS = BUILDER.getEntryKeys();
    }
    
}
