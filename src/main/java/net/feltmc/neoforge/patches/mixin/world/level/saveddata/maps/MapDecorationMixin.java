package net.feltmc.neoforge.patches.mixin.world.level.saveddata.maps;

import net.feltmc.neoforge.patches.interfaces.MapDecorationInterface;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MapDecoration.class)
public class MapDecorationMixin implements MapDecorationInterface {
    @Override
    public boolean render(int index) {
        return false;
    }
}
