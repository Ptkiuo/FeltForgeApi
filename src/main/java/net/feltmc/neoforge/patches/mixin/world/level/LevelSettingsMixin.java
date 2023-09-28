package net.feltmc.neoforge.patches.mixin.world.level;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import fr.catcore.cursedmixinextensions.annotations.NewConstructor;
import fr.catcore.cursedmixinextensions.annotations.ReplaceConstructor;
import fr.catcore.cursedmixinextensions.annotations.ShadowConstructor;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("MissingUnique")
@Mixin(LevelSettings.class)
public abstract class LevelSettingsMixin {
	
	@Shadow @Final private String levelName;
	@Shadow @Final private GameType gameType;
	@Shadow @Final private boolean hardcore;
	@Shadow @Final private Difficulty difficulty;
	@Shadow @Final private boolean allowCommands;
	@Shadow @Final private GameRules gameRules;
	@Shadow @Final private WorldDataConfiguration dataConfiguration;
	
	@Shadow public abstract LevelSettings copy();
	
	@SuppressWarnings("FieldCanBeLocal")
	private com.mojang.serialization.Lifecycle lifecycle;
	
	@ShadowConstructor
	public abstract void thiz(String string, GameType gameType, boolean bl, Difficulty difficulty, boolean bl2,
	                 GameRules gameRules, WorldDataConfiguration worldDataConfiguration);
	
	@Inject(method = "<init>(Ljava/lang/String;Lnet/minecraft/world/level/GameType;ZLnet/minecraft/world/Difficulty;" +
		"ZLnet/minecraft/world/level/GameRules;Lnet/minecraft/world/level/WorldDataConfiguration;)V", at = @At("TAIL"))
	public void thiz(String string, GameType gameType, boolean bl, Difficulty difficulty, boolean bl2, 
	                 GameRules gameRules, WorldDataConfiguration worldDataConfiguration, CallbackInfo ci) {
		this.lifecycle = Lifecycle.stable();
	}
	
	@NewConstructor
	@ReplaceConstructor
	public void thiz(String string, GameType gameType, boolean bl, Difficulty difficulty, boolean bl2,
	                 GameRules gameRules, WorldDataConfiguration worldDataConfiguration, Lifecycle lifecycle) {
		thiz(string, gameType, bl, difficulty, bl2, gameRules, worldDataConfiguration);
		this.lifecycle = lifecycle;
	}
	
	@Inject(method = "parse", at = @At("TAIL"))
	private static void parse$TAIL(Dynamic<?> dynamic, WorldDataConfiguration worldDataConfiguration, 
	                               CallbackInfoReturnable<LevelSettings> cir) {
		cir.getReturnValue()
			.withLifecycle(ForgeHooks.parseLifecycle(dynamic.get("forgeLifecycle").asString("stable")));
	}
	
	@Inject(method = "withGameType", at = @At("TAIL"))
	public void withGameType$TAIL(GameType gameType, CallbackInfoReturnable<LevelSettings> cir) {
		cir.getReturnValue()
			.withLifecycle(this.lifecycle);
	}
	
	@Inject(method = "withDifficulty", at = @At("HEAD"))
	public void withDifficulty$HEAD(Difficulty difficulty, CallbackInfoReturnable<LevelSettings> cir) {
		ForgeHooks.onDifficultyChange(difficulty, this.difficulty);
	}
	
	@Inject(method = "withDifficulty", at = @At("TAIL"))
	public void withDifficulty$TAIL(Difficulty difficulty, CallbackInfoReturnable<LevelSettings> cir) {
		cir.getReturnValue()
			.withLifecycle(this.lifecycle);
	}
	
	@Inject(method = "withDataConfiguration", at = @At("TAIL"))
	public void withDataConfiguration$TAIL(WorldDataConfiguration worldDataConfiguration, 
	                                       CallbackInfoReturnable<LevelSettings> cir) {
		cir.getReturnValue()
			.withLifecycle(this.lifecycle);
	}
	
	@Inject(method = "copy", at = @At("HEAD"), cancellable = true)
	public void copy$HEAD(CallbackInfoReturnable<LevelSettings> cir) {
		cir.setReturnValue(new LevelSettings(this.levelName, this.gameType, this.hardcore, this.difficulty,
			this.allowCommands, this.gameRules, this.dataConfiguration, this.lifecycle));
	}
	
	public LevelSettings withLifecycle(Lifecycle lifecycle) {
		return new LevelSettings(this.levelName, this.gameType, this.hardcore, this.difficulty, this.allowCommands,
			this.gameRules, this.dataConfiguration, lifecycle);
	}
	
	public Lifecycle getLifecycle() {
		return this.lifecycle;
	}
	
}
