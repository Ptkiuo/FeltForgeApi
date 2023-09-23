package net.feltmc.neoforge.patches.interfaces;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface GuiGraphicsInterface {
    int drawString(Font font, @Nullable String string, float i, float j, int k, boolean bl);
    int drawString(Font font, FormattedCharSequence formattedCharSequence, float i, float j, int k, boolean bl);
    void blitRepeating(ResourceLocation resourceLocation, int i, int j, int k, int l, int m, int n, int o, int p, int textureWidth, int textureHeight);
    void renderTooltip(Font font, List<Component> textComponents, Optional<TooltipComponent> tooltipComponent, ItemStack stack, int mouseX, int mouseY);
    void renderComponentTooltip(Font font, List<? extends net.minecraft.network.chat.FormattedText> tooltips, int mouseX, int mouseY, ItemStack stack);
}
