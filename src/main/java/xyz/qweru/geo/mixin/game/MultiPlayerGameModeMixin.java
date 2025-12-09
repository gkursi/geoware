package xyz.qweru.geo.mixin.game;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qweru.geo.client.event.AttackBlockEvent;
import xyz.qweru.geo.client.event.AttackEntityEvent;
import xyz.qweru.geo.client.event.PostPlaceBlockEvent;
import xyz.qweru.geo.core.event.EventBus;
import xyz.qweru.geo.imixin.IMultiplayerGameMode;

import static xyz.qweru.geo.core.Core.mc;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin implements IMultiplayerGameMode {
    @Shadow protected abstract void startPrediction(ClientLevel clientLevel, PredictiveAction predictiveAction);

    @Inject(method = "useItemOn", at = @At("TAIL"))
    private void onInteract(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        PostPlaceBlockEvent.INSTANCE.setHit(hitResult);
        PostPlaceBlockEvent.INSTANCE.setPos(hitResult.getBlockPos());
        EventBus.INSTANCE.post(PostPlaceBlockEvent.INSTANCE);
    }

    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttack(Player player, Entity target, CallbackInfo ci) {
        AttackEntityEvent.INSTANCE.setPlayer(player);
        AttackEntityEvent.INSTANCE.setEntity(target);
        EventBus.INSTANCE.post(AttackEntityEvent.INSTANCE);
    }

    @Override
    public void geo_sequencedPacket(PredictiveAction action) {
        startPrediction(mc.level, action);
    }

    @Inject(method = "startDestroyBlock", at = @At("HEAD"), cancellable = true)
    private void onAttackBlock(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        AttackBlockEvent.INSTANCE.setPos(blockPos);
        AttackBlockEvent.INSTANCE.setDirection(direction);
        if (EventBus.INSTANCE.post(AttackBlockEvent.INSTANCE).getCancelled())
            cir.setReturnValue(false);
    }
}
