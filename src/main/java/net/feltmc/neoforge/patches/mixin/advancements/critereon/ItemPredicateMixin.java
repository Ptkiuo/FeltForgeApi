package net.feltmc.neoforge.patches.mixin.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.llamalad7.mixinextras.sugar.Local;
import fr.catcore.cursedmixinextensions.annotations.Public;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.Function;

@Mixin(ItemPredicate.class)
public class ItemPredicateMixin {
    private static final Map<ResourceLocation, Function<JsonObject, ItemPredicate>> custom_predicates = new java.util.HashMap<>();
    private static final Map<ResourceLocation, java.util.function.Function<JsonObject, ItemPredicate>> unmod_predicates = java.util.Collections.unmodifiableMap(custom_predicates);

    @Redirect(method = "matches", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;deserializeEnchantments(Lnet/minecraft/nbt/ListTag;)Ljava/util/Map;", ordinal = 0))
    private Map<Enchantment, Integer> deserializeEnchantments(ListTag listTag, @Local(ordinal = 0) ItemStack stack) {
        return stack.getAllEnchantments();
    }

    @Inject(method = "fromJson", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/MinMaxBounds$Ints;fromJson(Lcom/google/gson/JsonElement;)Lnet/minecraft/advancements/critereon/MinMaxBounds$Ints;", ordinal = 0))
    private static void fromKnownPredicates(JsonElement jsonElement, CallbackInfoReturnable<ItemPredicate> cir,
                                            @Local(ordinal = 0) JsonObject jsonObject) {
        if (jsonObject.has("type")) {
            final ResourceLocation rl = new ResourceLocation(GsonHelper.getAsString(jsonObject, "type"));
            if (custom_predicates.containsKey(rl)) cir.setReturnValue(custom_predicates.get(rl).apply(jsonObject));
            else throw new JsonSyntaxException("There is no ItemPredicate of type "+rl);
        }
    }

    @Public
    private static void register(ResourceLocation name, java.util.function.Function<JsonObject, ItemPredicate> deserializer) {
        custom_predicates.put(name, deserializer);
    }

    @Public
    private static Map<ResourceLocation, java.util.function.Function<JsonObject, ItemPredicate>> getPredicates() {
        return unmod_predicates;
    }
}
