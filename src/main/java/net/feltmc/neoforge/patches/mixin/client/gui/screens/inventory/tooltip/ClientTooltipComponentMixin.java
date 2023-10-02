package net.feltmc.neoforge.patches.mixin.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientTooltipComponent.class)
public class ClientTooltipComponentMixin {
	@Inject(method = "create(Lnet/minecraft/world/inventory/tooltip/TooltipComponent;)" +
		"Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;", at = @At(value = "NEW",
		target = "(Ljava/lang/String;)Ljava/lang/IllegalArgumentException;"), cancellable = true)
	private static void cancelException(TooltipComponent tooltipComponent,
	                                    CallbackInfoReturnable<ClientTooltipComponent> cir) {
		ClientTooltipComponent result = net.minecraftforge.client.gui.ClientTooltipComponentManager.createClientTooltipComponent(tooltipComponent);
		if (result != null) cir.setReturnValue(result);
	}
}
