package net.feltmc.neoforge.patches.mixin.server.advancements;

import fr.catcore.cursedmixinextensions.annotations.Public;
import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.feltmc.feltasm.asm.CreateStatic;
import net.minecraft.advancements.Advancement;
import net.minecraft.server.advancements.AdvancementVisibilityEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Predicate;

@Mixin(AdvancementVisibilityEvaluator.class)
public abstract class AdvancementVisibilityEvaluatorMixin {
    @Shadow
    protected static boolean evaluateVisibility(Advancement advancement, Stack<AdvancementVisibilityEvaluator.VisibilityRule> stack, Predicate<Advancement> predicate, AdvancementVisibilityEvaluator.Output output) {
        throw new RuntimeException("what?");
    }

    @CreateStatic @Public
    private static boolean isVisible(Advancement advancement, Predicate<Advancement> test) {
        Stack<AdvancementVisibilityEvaluator.VisibilityRule> stack = new ObjectArrayList<>();
        for(int i = 0; i <= 2; ++i) {
            stack.push(AdvancementVisibilityEvaluator.VisibilityRule.NO_CHANGE);
        }
        return evaluateVisibility(advancement.getRoot(), stack, test, (adv, visible) -> {});
    }
}
