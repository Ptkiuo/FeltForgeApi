package net.feltmc.neoforge.patches.mixin.client.gui.screens.inventory;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.CreativeTabsScreenPage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@SuppressWarnings({ "MissingUnique", "AddedMixinMembersNamePattern" })
@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu> implements net.feltmc.neoforge.patches.interfaces.CreativeModeInventoryScreenInterface {
	@Shadow
	private static CreativeModeTab selectedTab;
	
	@Shadow
	private EditBox searchBox;
	
	public CreativeModeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu abstractContainerMenu, Inventory inventory, Component component) {
		super(abstractContainerMenu, inventory, component);
	}
	
	private final List<CreativeTabsScreenPage> pages = new java.util.ArrayList<>();
	private net.minecraftforge.client.gui.CreativeTabsScreenPage currentPage = new net.minecraftforge.client.gui.CreativeTabsScreenPage(new java.util.ArrayList<>());
	
	@WrapOperation(method = "refreshCurrentTabContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item" +
		"/CreativeModeTab;getType()Lnet/minecraft/world/item/CreativeModeTab$Type;"))
	private CreativeModeTab.Type refresh$searchBar(CreativeModeTab instance, Operation<CreativeModeTab.Type> operation) {
		return instance.hasSearchBar() ? CreativeModeTab.Type.SEARCH : operation.call(instance);
	}
	
	@Inject(method = "init", at = @At(value = "NEW", target = "(Lnet/minecraft/client/gui/Font;IIIILnet/minecraft/network/chat/Component;)Lnet/minecraft/client/gui/components/EditBox;"))
	private void thiz(CallbackInfo ci) {
		this.pages.clear();
		int tabIndex = 0;
		List<CreativeModeTab> currentPage = new java.util.ArrayList<>();
		
		for (CreativeModeTab sortedCreativeModeTab :
			net.minecraftforge.common.CreativeModeTabRegistry.getSortedCreativeModeTabs()) {
			currentPage.add(sortedCreativeModeTab);
			tabIndex++;
			
			if (tabIndex == 10) {
				this.pages.add(new net.minecraftforge.client.gui.CreativeTabsScreenPage(currentPage));
				currentPage = new java.util.ArrayList<>();
				tabIndex = 0;
			}
		}
		
		if (tabIndex != 0) {
			this.pages.add(new net.minecraftforge.client.gui.CreativeTabsScreenPage(currentPage));
		}
		
		if (this.pages.isEmpty()) {
			this.currentPage = new net.minecraftforge.client.gui.CreativeTabsScreenPage(new java.util.ArrayList<>());
		} else {
			this.currentPage = this.pages.get(0);
		}
		
		if (this.pages.size() > 1) {
			addRenderableWidget(net.minecraft.client.gui.components.Button.builder(Component.literal("<"),
				b -> setCurrentPage(this.pages.get(Math.max(this.pages.indexOf(this.currentPage) - 1, 0))))
				.pos(leftPos,  topPos - 50)
				.size(20, 20)
				.build());
			addRenderableWidget(net.minecraft.client.gui.components.Button.builder(Component.literal(">"),
				b -> setCurrentPage(this.pages.get(Math.min(this.pages.indexOf(this.currentPage) + 1,
					this.pages.size() - 1))))
				.pos(leftPos + imageWidth - 20, topPos - 50)
				.size(20, 20)
				.build());
		}
		
		this.currentPage = this.pages.stream().filter(page -> page.getVisibleTabs().contains(selectedTab)).findFirst().orElse(this.currentPage);
		
		if (!this.currentPage.getVisibleTabs().contains(selectedTab)) {
			selectedTab = this.currentPage.getVisibleTabs().get(0);
		}
	}
	
	@WrapOperation(method = {"charTyped", "keyPressed"}, at = @At(value = "INVOKE", 
		target = "Lnet/minecraft/world/item/CreativeModeTab;getType()Lnet/minecraft/world/item/CreativeModeTab$Type;"))
	private CreativeModeTab.Type charTyped$searchBar(CreativeModeTab instance, Operation<CreativeModeTab.Type> operation) {
		return !instance.hasSearchBar() ? CreativeModeTab.Type.INVENTORY : operation.call(instance);
	}
	
	@Inject(method = "refreshSearchResults", cancellable = true, at = @At("HEAD"))
	private void refreshSearchResult$hasSearchBar(CallbackInfo ci) {
		if (!selectedTab.hasSearchBar()) ci.cancel();
	}
	
	@ModifyArg(method = "refreshSearchResults", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;" +
		"getSearchTree(Lnet/minecraft/client/searchtree/SearchRegistry$Key;)" +
		"Lnet/minecraft/client/searchtree/SearchTree;", ordinal = 0))
	private SearchRegistry.Key<ItemStack> refreshSearchResults$getSearchTree1(SearchRegistry.Key<ItemStack> key) {
		return net.minecraftforge.client.CreativeModeTabSearchRegistry.getTagSearchKey(selectedTab);
	}
	
	@ModifyArg(method = "refreshSearchResults", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;" +
		"getSearchTree(Lnet/minecraft/client/searchtree/SearchRegistry$Key;)" +
		"Lnet/minecraft/client/searchtree/SearchTree;", ordinal = 1))
	private SearchRegistry.Key<ItemStack> refreshSearchResults$getSearchTree2(SearchRegistry.Key<ItemStack> key) {
		return net.minecraftforge.client.CreativeModeTabSearchRegistry.getNameSearchKey(selectedTab);
	}
	
	@Inject(method = "renderLabels", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)I"))
	private void renderLabels$disableBlend(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci) {
		com.mojang.blaze3d.systems.RenderSystem.disableBlend();
	}
	
	@ModifyArg(method = "renderLabels", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;" +
		"drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)I"), index = 4)
	private int renderLabels$color(int i) {
		return selectedTab.getLabelColor();
	}
	
	@Redirect(method = {"mouseClicked", "mouseReleased", "render", "renderBg"}, at = @At(value = "INVOKE", 
		target = "Lnet/minecraft/world/item/CreativeModeTabs;tabs()Ljava/util/List;"))
	private List<CreativeModeTab> m$getVisibleTabs() {
		return currentPage.getVisibleTabs();
	}
	
	// TODO felt: what in the hot crispy kentucky fried fuck was the original patch even supposed to do here??
//	@Inject(method = "selectTab", at = @At(value = "INVOKE", target = "Ljava/util/Set;clear()V", ordinal = 0))
//	private void selectTab$setColor(CreativeModeTab creativeModeTab, CallbackInfo ci) {
//		slotColor = creativeModeTab.getSlotColor();
//	}
	
	@WrapOperation(method = "selectTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item" +
		"/CreativeModeTab;getType()Lnet/minecraft/world/item/CreativeModeTab$Type;", ordinal = 4))
	private CreativeModeTab.Type selectTab$searchBar(CreativeModeTab instance, Operation<CreativeModeTab.Type> operation) {
		return instance.hasSearchBar() ? CreativeModeTab.Type.SEARCH : operation.call(instance);
	}
	
	@Inject(method = "selectTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;refreshSearchResults()V"))
	private void selectTab$search(CreativeModeTab creativeModeTab, CallbackInfo ci) {
		this.searchBox.setWidth(selectedTab.getSearchBarWidth());
		this.searchBox.setX(this.leftPos + (82 /*default left*/ + 89 /*default width*/) - this.searchBox.getWidth());
	}
	
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V"))
	private void render$page(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
		if (this.pages.size() != 1) {
			Component page = Component.literal(String.format("%d / %d", this.pages.indexOf(this.currentPage) + 1,
				this.pages.size()));
			guiGraphics.pose().pushPose();
			guiGraphics.pose().translate(0F, 0F, 300F);
			guiGraphics.drawString(font, page.getVisualOrderText(),
				leftPos + (imageWidth / 2) - (font.width(page) / 2),
				topPos - 44, -1);
			guiGraphics.pose().popPose();
		}
		
		com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	@WrapOperation(method = "getTooltipFromContainerItem", at = {
		@At(value = "INVOKE", target = "Lnet/minecraft/world/item" +
			"/CreativeModeTab;getType()Lnet/minecraft/world/item/CreativeModeTab$Type;", ordinal = 1),
		@At(value = "INVOKE", target = "Lnet/minecraft/world/item" +
			"/CreativeModeTab;getType()Lnet/minecraft/world/item/CreativeModeTab$Type;", ordinal = 2)
	})
	private CreativeModeTab.Type getTooltipFromContainerItem$searchBar(CreativeModeTab instance, Operation<CreativeModeTab.Type> operation) {
		return instance.hasSearchBar() ? CreativeModeTab.Type.SEARCH : operation.call(instance);
	}
	
	@ModifyArg(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit" +
		"(Lnet/minecraft/resources/ResourceLocation;IIIIII)V", ordinal = 0))
	private ResourceLocation renderBg$backgroundLocation1(ResourceLocation resourceLocation) {
		return selectedTab.getBackgroundLocation();
	}
	
	@ModifyArg(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit" +
		"(Lnet/minecraft/resources/ResourceLocation;IIIIII)V", ordinal = 1))
	private ResourceLocation renderBg$backgroundLocation2(ResourceLocation resourceLocation) {
		return selectedTab.getTabsImage();
	}
	
	@WrapWithCondition(method = "renderBg", at = @At(value = "INVOKE", 
		target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;renderTabButton" +
			"(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/item/CreativeModeTab;)V", ordinal = 1))
	private boolean renderBg$renderTabButton(CreativeModeInventoryScreen instance, GuiGraphics guiGraphics,
	                                         CreativeModeTab tab) {
		return currentPage.getVisibleTabs().contains(tab);
	}
	
	@Redirect(method = { "getTabX", "renderTabButton" }, at = @At(value = "INVOKE", 
		target = "Lnet/minecraft/world/item/CreativeModeTab;column()I"))
	private int m$getColumn(CreativeModeTab instance) {
		return currentPage.getColumn(instance);
	}
	
	@WrapOperation(method = { "getTabY", "renderTabButton" }, at = @At(value = "INVOKE", 
		target = "Lnet/minecraft/world/item/CreativeModeTab;row()Lnet/minecraft/world/item/CreativeModeTab$Row;"))
	private CreativeModeTab.Row m$isTop(CreativeModeTab instance, Operation<CreativeModeTab.Row> operation) {
		return currentPage.isTop(instance) ? CreativeModeTab.Row.TOP : operation.call(instance);
	}
	
	@Inject(method = "renderTabButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"))
	private void renderTabButton$enableBlend(GuiGraphics guiGraphics, CreativeModeTab creativeModeTab, CallbackInfo ci) {
		com.mojang.blaze3d.systems.RenderSystem.enableBlend(); //Forge: Make sure blend is enabled else tabs show a white border.
	}
	
	@ModifyArg(method = "renderTabButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"))
	private ResourceLocation renderTabButton$tabImage(ResourceLocation old, @Local(ordinal = 0) CreativeModeTab tab) {
		return tab.getTabsImage();
	}
	
	@Override
	public net.minecraftforge.client.gui.CreativeTabsScreenPage getCurrentPage() {
		return currentPage;
	}
	
	@Override
	public void setCurrentPage(net.minecraftforge.client.gui.CreativeTabsScreenPage currentPage) {
		this.currentPage = currentPage;
	}
}
