package net.feltmc.neoforge.patches.mixin;

import net.minecraft.server.packs.PackResources;
import net.minecraftforge.common.extensions.IForgePackResources;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PackResources.class)
public class PackResourcesMixin implements IForgePackResources {
}
