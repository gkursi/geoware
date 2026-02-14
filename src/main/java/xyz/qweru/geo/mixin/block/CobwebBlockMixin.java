package xyz.qweru.geo.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.core.game.movement.MovementState;

import static xyz.qweru.geo.core.Core.mc;

@Mixin(WebBlock.class)
public class CobwebBlockMixin {

    @Inject(method = "entityInside", at = @At("HEAD"))
    private void onSlow(BlockState blockState, Level level, BlockPos blockPos, Entity entity, InsideBlockEffectApplier insideBlockEffectApplier, boolean bl, CallbackInfo ci) {
        if (entity != mc.player) return;
        MovementState.INSTANCE.setSlowedByBlock(true);
    }

}
