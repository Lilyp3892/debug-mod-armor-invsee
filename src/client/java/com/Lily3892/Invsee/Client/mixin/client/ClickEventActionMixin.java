package com.Lily3892.Invsee.Client.mixin.client;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import net.minecraft.text.ClickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClickEvent.Action.class)
public class ClickEventActionMixin {
	@Inject(at = @At("HEAD"), method = "validate", cancellable = true)
	private static void cancelValidation(ClickEvent.Action action, CallbackInfoReturnable<DataResult<ClickEvent.Action>> cir) {
		cir.setReturnValue(DataResult.success(action, Lifecycle.stable()));
	}
}