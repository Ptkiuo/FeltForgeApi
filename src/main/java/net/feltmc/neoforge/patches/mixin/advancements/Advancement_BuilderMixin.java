package net.feltmc.neoforge.patches.mixin.advancements;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import fr.catcore.cursedmixinextensions.annotations.Public;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.Map;

@Mixin(Advancement.Builder.class)
public class Advancement_BuilderMixin implements net.minecraftforge.common.extensions.IForgeAdvancementBuilder{
    @Inject(method = "fromJson", at = @At("HEAD"), cancellable = true)
    private static void whyForgeWhy(JsonObject jsonObject, DeserializationContext deserializationContext, CallbackInfoReturnable<Advancement.Builder> cir) {
        cir.setReturnValue(fromJson(jsonObject, deserializationContext, net.minecraftforge.common.crafting.conditions.ICondition.IContext.EMPTY));
    }

    @Public
    private static Advancement.Builder fromJson(JsonObject jsonObject, DeserializationContext deserializationContext, net.minecraftforge.common.crafting.conditions.ICondition.IContext context) {
        if ((jsonObject = net.minecraftforge.common.crafting.ConditionalAdvancement.processConditional(jsonObject, context)) == null) return null;

        ResourceLocation resourceLocation = jsonObject.has("parent") ? new ResourceLocation(GsonHelper.getAsString(jsonObject, "parent")) : null;
        DisplayInfo displayInfo = jsonObject.has("display") ? DisplayInfo.fromJson(GsonHelper.getAsJsonObject(jsonObject, "display")) : null;
        AdvancementRewards advancementRewards = jsonObject.has("rewards") ? AdvancementRewards.deserialize(GsonHelper.getAsJsonObject(jsonObject, "rewards")) : AdvancementRewards.EMPTY;
        Map<String, Criterion> map = Criterion.criteriaFromJson(GsonHelper.getAsJsonObject(jsonObject, "criteria"), deserializationContext);
        if (map.isEmpty()) {
            throw new JsonSyntaxException("Advancement criteria cannot be empty");
        } else {
            JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "requirements", new JsonArray());
            String[][] strings = new String[jsonArray.size()][];

            int i;
            int j;
            for(i = 0; i < jsonArray.size(); ++i) {
                JsonArray jsonArray2 = GsonHelper.convertToJsonArray(jsonArray.get(i), "requirements[" + i + "]");
                strings[i] = new String[jsonArray2.size()];

                for(j = 0; j < jsonArray2.size(); ++j) {
                    strings[i][j] = GsonHelper.convertToString(jsonArray2.get(j), "requirements[" + i + "][" + j + "]");
                }
            }

            if (strings.length == 0) {
                strings = new String[map.size()][];
                i = 0;

                String string;
                for(Iterator var16 = map.keySet().iterator(); var16.hasNext(); strings[i++] = new String[]{string}) {
                    string = (String)var16.next();
                }
            }

            String[][] var17 = strings;
            int var18 = strings.length;

            int var13;
            for(j = 0; j < var18; ++j) {
                String[] strings2 = var17[j];
                if (strings2.length == 0 && map.isEmpty()) {
                    throw new JsonSyntaxException("Requirement entry cannot be empty");
                }

                String[] var12 = strings2;
                var13 = strings2.length;

                for(int var14 = 0; var14 < var13; ++var14) {
                    String string2 = var12[var14];
                    if (!map.containsKey(string2)) {
                        throw new JsonSyntaxException("Unknown required criterion '" + string2 + "'");
                    }
                }
            }

            Iterator var19 = map.keySet().iterator();

            String string3;
            boolean bl;
            do {
                if (!var19.hasNext()) {
                    boolean bl2 = GsonHelper.getAsBoolean(jsonObject, "sends_telemetry_event", false);
                    return new Advancement.Builder(resourceLocation, displayInfo, advancementRewards, map, strings, bl2);
                }

                string3 = (String)var19.next();
                bl = false;
                String[][] var23 = strings;
                int var25 = strings.length;

                for(var13 = 0; var13 < var25; ++var13) {
                    String[] strings3 = var23[var13];
                    if (ArrayUtils.contains(strings3, string3)) {
                        bl = true;
                        break;
                    }
                }
            } while(bl);

            throw new JsonSyntaxException("Criterion '" + string3 + "' isn't a requirement for completion. This isn't supported behaviour, all criteria must be required.");
        }
    }
}
