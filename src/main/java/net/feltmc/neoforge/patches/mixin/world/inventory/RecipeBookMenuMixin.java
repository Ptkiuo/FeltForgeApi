package net.feltmc.neoforge.patches.mixin.world.inventory;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@SuppressWarnings("MissingUnique")
@Mixin(RecipeBookMenu.class)
public abstract class RecipeBookMenuMixin {
	
	@Shadow
	public abstract RecipeBookType getRecipeBookType();
	
	public List<RecipeBookCategories> getRecipeBookCategories() {
		return RecipeBookCategories.getCategories(this.getRecipeBookType());
	}
	
}
