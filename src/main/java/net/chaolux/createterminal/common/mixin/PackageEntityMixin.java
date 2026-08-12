package net.chaolux.createterminal.common.mixin;

import com.simibubi.create.content.logistics.box.PackageEntity;
import net.chaolux.createterminal.common.event.TerminalProtocolDisc;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PackageEntity.class,remap = false)
public abstract class PackageEntityMixin {
    @Inject(method = "destroy",at = @At("HEAD"),remap = false)
    private void createterminal$onPackageDestroyed(DamageSource damageSource, CallbackInfo callbackInfo) {
        if(damageSource.getEntity() instanceof ServerPlayer serverPlayer) TerminalProtocolDisc.onPackageDestroyed(serverPlayer);
    }
}
