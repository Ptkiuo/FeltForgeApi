package net.feltmc.neoforge.patches.mixin.client.gui.screens.inventory;

import fr.catcore.cursedmixinextensions.annotations.Public;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> {
	@Shadow
	public static void renderEntityInInventory(GuiGraphics guiGraphics, int i, int j, int k, Quaternionf quaternionf, @Nullable Quaternionf quaternionf2, LivingEntity livingEntity) {
	}
	
	public InventoryScreenMixin(InventoryMenu abstractContainerMenu, Inventory inventory, Component component) {
		super(abstractContainerMenu, inventory, component);
	}
	
	@Inject(method = "renderEntityInInventoryFollowsMouse", at = @At("HEAD"), cancellable = true)
	private static void renderEntityInInventoryFollowsMouse$override(GuiGraphics p_282802_, int p_275688_, int p_275245_, 
	                                                                 int p_275535_, float p_275604_, float p_275546_, LivingEntity p_275689_, CallbackInfo ci) {
		float f = (float)Math.atan((double)(p_275604_ / 40.0F));
		float f1 = (float)Math.atan((double)(p_275546_ / 40.0F));
		// Forge: Allow passing in direct angle components instead of mouse position
		renderEntityInInventoryFollowsAngle(p_282802_, p_275688_, p_275245_, p_275535_, f, f1, p_275689_);
		
		ci.cancel();
	}
	
	@Public
	private static void renderEntityInInventoryFollowsAngle(GuiGraphics guiGraphics, int i, int j, int k, float angleXComponent, float angleYComponent, LivingEntity livingEntity) {
		float h = angleXComponent;
		float l = angleYComponent;
		Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F);
		Quaternionf quaternionf2 = (new Quaternionf()).rotateX(l * 20.0F * 0.017453292F);
		quaternionf.mul(quaternionf2);
		float m = livingEntity.yBodyRot;
		float n = livingEntity.getYRot();
		float o = livingEntity.getXRot();
		float p = livingEntity.yHeadRotO;
		float q = livingEntity.yHeadRot;
		livingEntity.yBodyRot = 180.0F + h * 20.0F;
		livingEntity.setYRot(180.0F + h * 40.0F);
		livingEntity.setXRot(-l * 20.0F);
		livingEntity.yHeadRot = livingEntity.getYRot();
		livingEntity.yHeadRotO = livingEntity.getYRot();
		renderEntityInInventory(guiGraphics, i, j, k, quaternionf, quaternionf2, livingEntity);
		livingEntity.yBodyRot = m;
		livingEntity.setYRot(n);
		livingEntity.setXRot(o);
		livingEntity.yHeadRotO = p;
		livingEntity.yHeadRot = q;
	}
}
