package net.feltmc.neoforge.patches.mixin.tags;

import net.feltmc.feltasm.asm.CreateStatic;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FluidTags.class)
public class FluidTagsMixin {
    @CreateStatic
    private static TagKey<Fluid> create(ResourceLocation name) {
        return TagKey.create(Registries.FLUID, name);
    }
}
