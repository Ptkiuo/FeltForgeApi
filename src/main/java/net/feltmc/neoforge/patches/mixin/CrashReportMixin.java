package net.feltmc.neoforge.patches.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.CrashReport;
import net.minecraft.SystemReport;
import net.minecraftforge.logging.CrashReportExtender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrashReport.class)
public class CrashReportMixin {
	
	@Shadow
	@Final
	private SystemReport systemReport;
	
	@Shadow
	private StackTraceElement[] uncategorizedStackTrace;
	
	@ModifyVariable(method = "getDetails(Ljava/lang/StringBuilder;)V", at = @At(value = "STORE"))
	public StackTraceElement[] getDetails$uncategorizedStackTrace(StackTraceElement[] uncategorizedStackTrace) {
		return new StackTraceElement[] {};
	}
	
	@ModifyArg(method = "getDetails(Ljava/lang/StringBuilder;)V", at = @At(
		value = "INVOKE", 
		target = "Ljava/lang/StringBuilder;append(Ljava/lang/String;)Ljava/lang/StringBuilder;", 
		ordinal = 4))
	public String getDetails$append$4(String str) {
		return "Stacktrace:";
	}
	
	@Inject(method = "getDetails(Ljava/lang/StringBuilder;)V", at = @At(
		value = "INVOKE", 
		target = "Ljava/lang/StringBuilder;append(Ljava/lang/String;)Ljava/lang/StringBuilder;", 
		ordinal = 4, 
		shift = At.Shift.AFTER))
	public void getDetails$append$4$AFTER(StringBuilder stringBuilder, CallbackInfo ci) {
		CrashReportExtender.generateEnhancedStackTrace(this.uncategorizedStackTrace);
	}
	
	@Redirect(method = "getDetails(Ljava/lang/StringBuilder;)V", at = @At(
		value = "INVOKE", 
		target = "Ljava/lang/StringBuilder;append(Ljava/lang/String;)Ljava/lang/StringBuilder;", 
		ordinal = 8))
	public StringBuilder getDetails$append$8(StringBuilder instance, String s) {
		return instance;
	}
	
	@Inject(method = "getDetails(Ljava/lang/StringBuilder;)V", at = @At(
		value = "INVOKE", 
		target = "Lnet/minecraft/SystemReport;appendToCrashReportString(Ljava/lang/StringBuilder;)V", 
		shift = At.Shift.BEFORE))
	public void getDetails$appendToCrashReportString(StringBuilder stringBuilder, CallbackInfo ci) {
		CrashReportExtender.extendSystemReport(systemReport);
	}
	
	@Inject(method = "getExceptionMessage", at = @At("RETURN"), cancellable = true)
	public void getExceptionMessage$RETURN(CallbackInfoReturnable<String> cir, @Local Throwable throwable) {
		cir.setReturnValue(CrashReportExtender.generateEnhancedStackTrace(throwable));
	}
	
	@Inject(method = "getFriendlyReport", at = @At(
		value = "INVOKE", 
		target = "Ljava/lang/StringBuilder;append(Ljava/lang/String;)Ljava/lang/StringBuilder;", 
		ordinal = 0, 
		shift = At.Shift.AFTER))
	public void getFriendlyReport$addCrashReportHeader(CallbackInfoReturnable<String> cir,
	                                                   @Local StringBuilder stringBuilder) {
		CrashReportExtender.addCrashReportHeader(stringBuilder, (CrashReport) (Object) this);
	}
	
}
