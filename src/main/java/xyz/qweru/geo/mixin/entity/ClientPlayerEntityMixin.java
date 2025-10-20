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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.client.event.VelocityTickEvent;
import xyz.qweru.geo.core.event.Events;
import xyz.qweru.geo.core.manager.movement.MovementState;
import xyz.qweru.geo.core.manager.movement.MovementTicker;
import xyz.qweru.geo.imixin.IClientPlayerEntity;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin implements IClientPlayerEntity {
    @Shadow @Final public ClientPlayNetworkHandler networkHandler;

    @Shadow protected abstract void sendSprintingPacket();

    @Shadow protected abstract void sendMovementPackets();

    @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
    private void tickMovement(CallbackInfo ci) {
        if (!MovementTicker.INSTANCE.canTick()) ci.cancel();
        // reset all states computed after this
        MovementState.INSTANCE.setSlowedByBlock(false);
        MovementState.INSTANCE.setBounce(false);
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void postMovement(CallbackInfo ci) {
        Vec3d vel = ((Entity)(Object) this).getVelocity();
        VelocityTickEvent.INSTANCE.setX(vel.x);
        VelocityTickEvent.INSTANCE.setY(vel.y);
        VelocityTickEvent.INSTANCE.setZ(vel.z);
        Events.INSTANCE.post(VelocityTickEvent.INSTANCE);
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
}
