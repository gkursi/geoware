package xyz.qweru.geo.mixin.block;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BedBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.core.manager.movement.MovementState;

import static xyz.qweru.geo.core.Global.mc;

@Mixin(BedBlock.class)
public class BedBlockMixin {

    @Inject(method = "updateEntityMovementAfterFallOn", at = @At("HEAD"))
    private void onBounce(BlockGetter blockGetter, Entity entity, CallbackInfo ci) {
        if (entity != mc.player) return;
        MovementState.INSTANCE.setBounce(true);
    }

}
