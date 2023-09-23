package net.feltmc.neoforge.patches.mixin.advancements;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AdvancementRewards.class)
public class AdvancementRewardsMixin {
    @WrapOperation(method = "grant", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootParams$Builder;withParameter(Lnet/minecraft/world/level/storage/loot/parameters/LootContextParam;Ljava/lang/Object;)Lnet/minecraft/world/level/storage/loot/LootParams$Builder;"))
    private <T> LootParams.Builder addMoreToBuilder(LootParams.Builder builder,
                                                    LootContextParam<T> lootContextParam, T object,
                                                    Operation<LootParams.Builder> operation,
                                                    @Local(ordinal = 0) ServerPlayer player
                                                    ) {
        return operation.call(lootContextParam, object).withLuck(player.getLuck());
    }
}
