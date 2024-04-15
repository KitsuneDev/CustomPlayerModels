package com.tom.cpm.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.net.PlayerList;

import com.tom.cpm.common.ServerHandler;

@Mixin(value = PlayerList.class, remap = false)
public class PlayerManagerMixin {

	@Inject(at = @At("RETURN"), method = "playerLoggedIn(Lnet/minecraft/server/entity/player/EntityPlayerMP;)V")
	public void onLogin(EntityPlayerMP arg, CallbackInfo cbi) {
		ServerHandler.netHandler.onJoin(arg);
	}
}
