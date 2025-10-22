package xyz.qweru.geo.mixin.game;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qweru.geo.client.event.AttackEntityEvent;
import xyz.qweru.geo.client.event.PlaceBlockEvent;
import xyz.qweru.geo.core.event.EventBus;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(method = "interactBlockInternal", at = @At("HEAD"))
    private void preInteract(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        PlaceBlockEvent.INSTANCE.hit = hitResult;
        EventBus.INSTANCE.post(PlaceBlockEvent.INSTANCE);
    }

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void onAttack(PlayerEntity player, Entity target, CallbackInfo ci) {
        AttackEntityEvent.INSTANCE.setPlayer(player);
        AttackEntityEvent.INSTANCE.setEntity(target);
        EventBus.INSTANCE.post(AttackEntityEvent.INSTANCE);
    }
}
