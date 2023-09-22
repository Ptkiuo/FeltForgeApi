package net.feltmc.neoforge.patches.mixin;

import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.common.extensions.IForgeCommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CommandSourceStack.class)
public class CommandSourceStackMixin implements IForgeCommandSourceStack {
}
