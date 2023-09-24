package net.feltmc.neoforge.patches.mixin.client.gui.components;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {
    @Inject(method = "getSystemInformation", at = @At(value = "INVOKE_ASSIGN", ordinal = 10,
            target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private void listEntityTags(CallbackInfoReturnable<List<String>> cir,
                              @Local(ordinal = 0) List<String> list, @Local(ordinal = 0) Entity entity) {
        entity.getType().builtInRegistryHolder().tags().forEach(t -> list.add("#" + t.location()));
    }
}
