package xyz.qweru.geo.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.client.event.*;
import xyz.qweru.geo.client.module.move.ModuleNoSlow;
import xyz.qweru.geo.core.event.EventBus;
import xyz.qweru.geo.core.manager.movement.MovementState;
import xyz.qweru.geo.core.manager.movement.MovementTicker;
import xyz.qweru.geo.core.system.Systems;
import xyz.qweru.geo.core.system.module.Modules;
import xyz.qweru.geo.imixin.IClientPlayerEntity;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin implements IClientPlayerEntity {
    @Shadow @Final public ClientPlayNetworkHandler networkHandler;
    @Shadow protected abstract void sendSprintingPacket();
    @Shadow protected abstract void sendMovementPackets();

    @Unique int groundTicks = 0;
    @Unique int airTicks = 0;

    @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
    private void tickMovement(CallbackInfo ci) {
        if (!MovementTicker.INSTANCE.canTick()) ci.cancel();
        // reset all states computed after this
        MovementState.INSTANCE.setSlowedByBlock(false);
        MovementState.INSTANCE.setBounce(false);
    }


    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void preMovement(CallbackInfo ci) {
        EventBus.INSTANCE.post(PreMovementTickEvent.INSTANCE);
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void postMovement(CallbackInfo ci) {
        if (((Entity)(Object) this).isOnGround()) {
            groundTicks++;
            airTicks = 0;
        } else {
            airTicks++;
            groundTicks = 0;
        }

        Vec3d vel = ((Entity)(Object) this).getVelocity();
        PostMovementTickEvent.INSTANCE.setVelX(vel.x);
        PostMovementTickEvent.INSTANCE.setVelY(vel.y);
        PostMovementTickEvent.INSTANCE.setVelZ(vel.z);
        EventBus.INSTANCE.post(PostMovementTickEvent.INSTANCE);
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;tick()V", shift = At.Shift.AFTER))
    private void postInputTick(CallbackInfo ci) {
        EventBus.INSTANCE.post(PostInputTick.INSTANCE);
    }

    @ModifyExpressionValue(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;hasForwardMovement()Z"))
    private boolean hasForwardMovement_startSprint(boolean original) {
        ForwardMovementCheckEvent.INSTANCE.setHasForwardMovement(original);
        return ForwardMovementCheckEvent.INSTANCE.getHasForwardMovement();
    }

    @ModifyExpressionValue(method = "shouldStopSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;hasForwardMovement()Z"))
    private boolean hasForwardMovement_stopSprint(boolean original) {
        ForwardMovementCheckEvent.INSTANCE.setHasForwardMovement(original);
        return ForwardMovementCheckEvent.INSTANCE.getHasForwardMovement();
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasVehicle()Z"))
    private boolean modifyHasVehicle(boolean original) {
        if (MovementTicker.INSTANCE.getTickSpeed() == 20) return original;
        return false;
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendMovementPackets()V"))
    private void wrapSendMovePackets(ClientPlayerEntity instance, Operation<Void> original) {
        if (MovementTicker.INSTANCE.getTickSpeed() == 20) original.call(instance);
    }

    @Override
    public void geo_tickMovementPackets() {
        ClientPlayerEntity _this = (ClientPlayerEntity) (Object) this;
        if (_this.hasVehicle()) {
            this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(_this.getYaw(), _this.getPitch(), _this.isOnGround(), _this.horizontalCollision));
            Entity entity = _this.getRootVehicle();
            if (entity != _this && entity.isLogicalSideForUpdatingMovement()) {
                this.networkHandler.sendPacket(VehicleMoveC2SPacket.fromVehicle(entity));
                this.sendSprintingPacket();
            }
        } else {
            this.sendMovementPackets();
        }
    }

    @Inject(method = "sendMovementPackets", at = @At("HEAD"))
    private void preSendMove(CallbackInfo ci) {
        EventBus.INSTANCE.post(PreMoveSendEvent.INSTANCE);
    }

    @Inject(method = "sendMovementPackets", at = @At("TAIL"))
    private void postSendMove(CallbackInfo ci) {
        EventBus.INSTANCE.post(PostMoveSendEvent.INSTANCE);
    }

    @Override
    public int geo_getAirTicks() {
        return airTicks;
    }

    @Override
    public int geo_getGroundTicks() {
        return groundTicks;
    }

    @ModifyArg(method = "applyMovementSpeedFactors", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec2f;multiply(F)Lnet/minecraft/util/math/Vec2f;", ordinal = 1))
    private float changeItemUseFactor(float factor) {
        ModuleNoSlow noSlow = Systems.INSTANCE.get(Modules.class).get(ModuleNoSlow.class);
        if (noSlow.getEnabled() && noSlow.getItems()) {
            factor *= 5f;
            factor *= 1 - noSlow.getItemSpeed();
        }
        return factor;
    }
}
