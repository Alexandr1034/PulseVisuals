package com.pulse.visuals.mixin;

import com.pulse.visuals.PulseVisualsClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionMixin {

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void onAttackEntity(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null || client.crosshairTarget == null) {
            return;
        }

        if (client.crosshairTarget instanceof EntityHitResult hitResult) {
            Entity entity = hitResult.getEntity();

            if (entity instanceof LivingEntity livingEntity && entity != client.player) {
                boolean isCritical = checkIfCritical(livingEntity);

                PulseVisualsClient.getHitParticleRenderer()
                        .addHitParticle(entity.getPos(), isCritical);

                float damage = 5.0f;

                PulseVisualsClient.getDamageNumberRenderer()
                        .addDamageNumber(
                                entity.getPos().add(0, entity.getHeight(), 0),
                                damage,
                                isCritical
                        );
            }
        }
    }

    @Inject(method = "breakBlock", at = @At("HEAD"))
    private void onBreakBlock(
            BlockPos pos,
            CallbackInfoReturnable<Boolean> cir
    ) {
        Vec3d blockCenter = Vec3d.of(pos).add(0.5, 0.5, 0.5);

        PulseVisualsClient.getHitParticleRenderer()
                .addHitParticle(blockCenter, false);
    }

    private boolean checkIfCritical(LivingEntity entity) {
        return false;
    }
}
