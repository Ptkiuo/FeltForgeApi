package net.feltmc.neoforge.patches.mixin.client.gui.screens;

import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {
    @Shadow @Final private Screen lastScreen;

    protected OptionsScreenMixin(Component component) {
        super(component);
    }

    @Override
    public void onClose() {
        // We need to consider 2 potential parent screens here:
        // 1. From the main menu, in which case display the main menu
        // 2. From the pause menu, in which case exit back to game
        this.minecraft.setScreen(this.lastScreen instanceof PauseScreen ? null : this.lastScreen);
    }
}
