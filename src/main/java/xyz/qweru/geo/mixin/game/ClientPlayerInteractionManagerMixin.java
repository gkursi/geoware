package xyz.qweru.geo.mixin.game;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qweru.geo.client.event.AttackEntityEvent;
import xyz.qweru.geo.client.event.PlaceBlockEvent;
import xyz.qweru.geo.core.event.EventBus;

@Mixin(MultiPlayerGameMode.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(method = "useItemOn", at = @At("HEAD"))
    private void preInteract(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        PlaceBlockEvent.INSTANCE.hit = hitResult;
        EventBus.INSTANCE.post(PlaceBlockEvent.INSTANCE);
    }

    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttack(Player player, Entity target, CallbackInfo ci) {
        AttackEntityEvent.INSTANCE.setPlayer(player);
        AttackEntityEvent.INSTANCE.setEntity(target);
        EventBus.INSTANCE.post(AttackEntityEvent.INSTANCE);
    }
}
