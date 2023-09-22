package net.feltmc.neoforge.patches.mixin;

import net.feltmc.neoforge.patches.interfaces.TagEntryInterface;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TagEntry.class)
public class TagEntryMixin implements TagEntryInterface {
    @Shadow @Final private ResourceLocation id;

    @Shadow @Final private boolean required;

    @Shadow @Final private boolean tag;

    public ResourceLocation getId() {
        return id;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isTag() {
        return tag;
    }
}
