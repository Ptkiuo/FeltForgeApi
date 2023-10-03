package net.feltmc.neoforge.patches.mixin.client.gui.screens.packs;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;
import java.util.stream.Stream;

@Mixin(PackSelectionScreen.class)
public class PackSelectionScreenMixin extends Screen {
	protected PackSelectionScreenMixin(Component component) {
		super(component);
	}
	
	@WrapOperation(method = "updateList", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;forEach(Ljava/util/function/Consumer;)V"))
	private void filterUpdateList(Stream<PackSelectionModel.Entry> stream,
	                              Consumer<PackSelectionModel.Entry> consumer, Operation<Void> operation) {
		operation.call(stream.filter(PackSelectionModel.Entry::notHidden), consumer);
	}
}
