package xyz.qweru.geo.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.PotionItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Shadow @Final private MinecraftClient client;

    @Shadow private int lastSelectedSlot;

    @Inject(method = "attackEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;attack(Lnet/minecraft/entity/Entity;)V"))
    private void attack(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (target instanceof EndCrystalEntity) {

        }
    }

    @Unique
    private float pitch = -99;

    @Inject(method = "interactItem", at = @At("HEAD"))
    private void preInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {

    }

    @Inject(method = "interactItem", at = @At("TAIL"))
    private void post(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (pitch != -99) {
            player.setPitch(pitch);
            pitch = -99;
        }
    }
}
