package net.feltmc.neoforge.patches.mixin.core;

import com.mojang.math.Constants;
import fr.catcore.cursedmixinextensions.annotations.Public;
import net.feltmc.feltasm.asm.CreateStatic;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static net.minecraft.core.Direction.NORTH;


@Mixin(Direction.class)
public class DirectionMixin {
    @Shadow @Final private static Direction[] VALUES;

    @SuppressWarnings("MissingUnique")
    @CreateStatic
    public Direction getNearestStable(float x, float y, float z) {
        Direction direction = NORTH;
        float f = Float.MIN_VALUE;
        for(Direction direction1 : VALUES) {
            float f1 = x * (float)direction1.getNormal().getX() + y * (float)direction1.getNormal().getY() + z * (float)direction1.getNormal().getZ();
            if (f1 > f + Constants.EPSILON) {
                f = f1;
                direction = direction1;
            }
        }
        return direction;
        }
}
