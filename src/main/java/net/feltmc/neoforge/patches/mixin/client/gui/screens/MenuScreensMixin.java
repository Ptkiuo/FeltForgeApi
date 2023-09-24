package net.feltmc.neoforge.patches.mixin.client.gui.screens;

import fr.catcore.cursedmixinextensions.annotations.Public;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(MenuScreens.class)
public abstract class MenuScreensMixin {

    @Shadow
    @Nullable
    private static <T extends AbstractContainerMenu> MenuScreens.ScreenConstructor<T, ?> getConstructor(MenuType<T> menuType) {
        return null;
    }

    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "create", cancellable = true, at = @At("HEAD"))
    private static <T extends AbstractContainerMenu> void overrideCreate(@Nullable MenuType<T> p_96202_, Minecraft p_96203_, int p_96204_, Component p_96205_, CallbackInfo ci) {
        getScreenFactory(p_96202_, p_96203_, p_96204_, p_96205_).ifPresent(f -> f.fromPacket(p_96205_, p_96202_, p_96203_, p_96204_));
        ci.cancel();
    }

    @Public
    private static <T extends AbstractContainerMenu> java.util.Optional<MenuScreens.ScreenConstructor<T, ?>> getScreenFactory(@Nullable MenuType<T> menuType, Minecraft minecraft, int i, Component component) {
        if (menuType == null) {
            LOGGER.warn("Trying to open invalid screen with name: {}", component.getString());
        } else {
            MenuScreens.ScreenConstructor<T, ?> screenConstructor = getConstructor(menuType);
            if (screenConstructor == null) {
                LOGGER.warn("Failed to create screen for menu type: {}", BuiltInRegistries.MENU.getKey(menuType));
            } else {
                return Optional.of(screenConstructor);
            }
        }

        return Optional.empty();
    }
}
