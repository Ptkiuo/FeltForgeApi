package net.feltmc.neoforge.patches.mixin.tags;

import net.feltmc.feltasm.asm.CreateStatic;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockTags.class)
public class BlockTagsMixin {
    @CreateStatic
    private static TagKey<Block> create(ResourceLocation name) {
        return TagKey.create(Registries.BLOCK, name);
    }
}
