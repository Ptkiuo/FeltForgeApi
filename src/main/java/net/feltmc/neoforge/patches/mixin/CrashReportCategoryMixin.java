package net.feltmc.neoforge.patches.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.CrashReportCategory;
import net.minecraftforge.logging.CrashReportExtender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrashReportCategory.class)
public class CrashReportCategoryMixin {
	
	@Shadow
	private StackTraceElement[] stackTrace;
	
	@Inject(method = "fillInStackTrace", at = @At(value = "FIELD", target = "Lnet/minecraft/CrashReportCategory;" +
		"stackTrace:[Ljava/lang/StackTraceElement;", shift = At.Shift.BEFORE), cancellable = true)
	public void fillInStackTrace(int i, CallbackInfoReturnable<Integer> cir, @Local StackTraceElement[] stackTraceElements) {
		int len = stackTraceElements.length - 3 - i;
		if (len <= 0) len = stackTraceElements.length;
		this.stackTrace = new StackTraceElement[len];
		System.arraycopy(stackTraceElements, stackTraceElements.length - len, this.stackTrace, 0, this.stackTrace.length);
		
		cir.setReturnValue(this.stackTrace.length);
	}
	
	@Inject(method = "getDetails", at = @At(value = "FIELD", target = "Lnet/minecraft/CrashReportCategory;" +
		"stackTrace:[Ljava/lang/StackTraceElement;", ordinal = 2, shift = At.Shift.BEFORE), cancellable = true)
	public void getDetails$appendStackTrace(StringBuilder stringBuilder, CallbackInfo ci) {
		stringBuilder.append(CrashReportExtender.generateEnhancedStackTrace(this.stackTrace));
		ci.cancel();
	}
	
	@SuppressWarnings("MissingUnique")
	public void applyStackTrace(Throwable t) {
		this.stackTrace = t.getStackTrace();
	}
	
}
