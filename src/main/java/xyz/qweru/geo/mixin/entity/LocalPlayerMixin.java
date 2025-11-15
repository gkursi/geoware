package xyz.qweru.geo.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.abstraction.network.ClientConnection;
import xyz.qweru.geo.client.event.*;
import xyz.qweru.geo.client.module.move.ModuleNoSlow;
import xyz.qweru.geo.core.event.EventBus;
import xyz.qweru.geo.core.manager.movement.MovementState;
import xyz.qweru.geo.core.manager.movement.MovementTicker;
import xyz.qweru.geo.core.system.Systems;
import xyz.qweru.geo.core.system.module.Modules;
import xyz.qweru.geo.imixin.ILocalPlayer;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin implements ILocalPlayer {

    @Shadow protected abstract void sendPosition();

    @Shadow protected abstract void sendIsSprintingIfNeeded();

    @Unique int groundTicks = 0;
    @Unique int airTicks = 0;

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    private void tickMovement(CallbackInfo ci) {
        if (!MovementTicker.INSTANCE.canTick()) ci.cancel();
        // reset all states computed after this
        MovementState.INSTANCE.setSlowedByBlock(false);
        MovementState.INSTANCE.setBounce(false);
    }


    @Inject(method = "aiStep", at = @At("HEAD"))
    private void preMovement(CallbackInfo ci) {
        EventBus.INSTANCE.post(PreMovementTickEvent.INSTANCE);
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void postMovement(CallbackInfo ci) {
        if (((Entity)(Object) this).onGround()) {
            groundTicks++;
            airTicks = 0;
        } else {
            airTicks++;
            groundTicks = 0;
        }

        Vec3 vel = ((Entity)(Object) this).getDeltaMovement();
        PostMovementTickEvent.INSTANCE.setVelX(vel.x);
        PostMovementTickEvent.INSTANCE.setVelY(vel.y);
        PostMovementTickEvent.INSTANCE.setVelZ(vel.z);
        EventBus.INSTANCE.post(PostMovementTickEvent.INSTANCE);
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/ClientInput;tick()V", shift = At.Shift.AFTER))
    private void postInputTick(CallbackInfo ci) {
        EventBus.INSTANCE.post(PostInputTick.INSTANCE);
    }

    @ModifyExpressionValue(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/ClientInput;hasForwardImpulse()Z"))
    private boolean hasForwardMovement_startSprint(boolean original) {
        ForwardMovementCheckEvent.INSTANCE.setHasForwardMovement(original);
        return ForwardMovementCheckEvent.INSTANCE.getHasForwardMovement();
    }

    @ModifyExpressionValue(method = "shouldStopRunSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/ClientInput;hasForwardImpulse()Z"))
    private boolean hasForwardMovement_stopSprint(boolean original) {
        ForwardMovementCheckEvent.INSTANCE.setHasForwardMovement(original);
        return ForwardMovementCheckEvent.INSTANCE.getHasForwardMovement();
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isPassenger()Z"))
    private boolean modifyHasVehicle(boolean original) {
        if (MovementTicker.INSTANCE.getTickSpeed() == 20) return original;
        return false;
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;sendPosition()V"))
    private void wrapSendMovePackets(LocalPlayer instance, Operation<Void> original) {
        if (MovementTicker.INSTANCE.getTickSpeed() == 20) original.call(instance);
    }

    @Override
    public void geo_tickMovementPackets() {
        LocalPlayer _this = (LocalPlayer) (Object) this;
        if (_this.isPassenger()) {
            ClientConnection.INSTANCE.sendPacket(new ServerboundMovePlayerPacket.Rot(_this.getYRot(), _this.getXRot(), _this.onGround(), _this.horizontalCollision));
            Entity entity = _this.getRootVehicle();
            if (entity != _this) {
                ClientConnection.INSTANCE.sendPacket(ServerboundMoveVehiclePacket.fromEntity(entity));
                this.sendIsSprintingIfNeeded();
            }
        } else {
            this.sendPosition();
        }
    }

    @Inject(method = "sendPosition", at = @At("HEAD"))
    private void preSendMove(CallbackInfo ci) {
        EventBus.INSTANCE.post(PreMoveSendEvent.INSTANCE);
    }

    @Inject(method = "sendPosition", at = @At("TAIL"))
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

    @ModifyArg(method = "modifyInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec2;scale(F)Lnet/minecraft/world/phys/Vec2;", ordinal = 1))
    private float changeItemUseFactor(float factor) {
        ModuleNoSlow noSlow = Systems.INSTANCE.get(Modules.class).get(ModuleNoSlow.class);
        if (noSlow.getEnabled() && noSlow.getItems()) {
            factor *= 5f;
            factor *= 1 - noSlow.getItemSpeed();
        }
        return factor;
    }
}
