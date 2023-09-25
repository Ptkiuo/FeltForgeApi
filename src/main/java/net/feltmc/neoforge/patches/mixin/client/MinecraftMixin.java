package net.feltmc.neoforge.patches.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.extensions.IForgeMinecraft;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(Minecraft.class)
public class MinecraftMixin implements IForgeMinecraft {
}
