package net.feltmc.neoforge.patches.mixin.client.resources.model;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.client.extensions.IForgeBakedModel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BakedModel.class)
public class BakedModelMixin implements IForgeBakedModel {
}