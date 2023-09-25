package net.feltmc.neoforge.patches.mixin.data.tags;

import fr.catcore.cursedmixinextensions.annotations.NewConstructor;
import fr.catcore.cursedmixinextensions.annotations.ReplaceConstructor;
import fr.catcore.cursedmixinextensions.annotations.ShadowSuperConstructor;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BannerPatternTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.spongepowered.asm.mixin.Mixin;

import java.util.concurrent.CompletableFuture;

@Mixin(BannerPatternTagsProvider.class)
public abstract class BannerPatternTagsProviderMixin {
    @ShadowSuperConstructor
    public abstract void zuper(PackOutput p_256596_, ResourceKey<? extends Registry<BannerPattern>> p_255886_, CompletableFuture<HolderLookup.Provider> p_256513_, String modId, @org.jetbrains.annotations.Nullable net.minecraftforge.common.data.ExistingFileHelper existingFileHelper);

    @NewConstructor
    @ReplaceConstructor
    public void thiz(PackOutput p_256451_, CompletableFuture<HolderLookup.Provider> p_256420_, String modId, @org.jetbrains.annotations.Nullable net.minecraftforge.common.data.ExistingFileHelper existingFileHelper) {
        zuper(p_256451_, Registries.BANNER_PATTERN, p_256420_, modId, existingFileHelper);
    }
}
