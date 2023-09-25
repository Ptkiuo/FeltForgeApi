package net.feltmc.neoforge.patches.mixin.tags;

import net.feltmc.feltasm.asm.CreateStatic;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings("MissingUnique")
@Mixin(ItemTags.class)
public class ItemTagsMixin {
    @CreateStatic
    public TagKey<Item> create(ResourceLocation name) {
        return TagKey.create(Registries.ITEM, name);
    }
}
