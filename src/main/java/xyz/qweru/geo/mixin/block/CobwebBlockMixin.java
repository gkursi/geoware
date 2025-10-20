package xyz.qweru.geo.mixin.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.CobwebBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.geo.core.manager.movement.MovementState;

import static xyz.qweru.geo.core.Glob.mc;

@Mixin(CobwebBlock.class)
public class CobwebBlockMixin {

    @Inject(method = "onEntityCollision", at = @At("HEAD"))
    private void onSlow(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, CallbackInfo ci) {
        if (entity != mc.player) return;
        MovementState.INSTANCE.setSlowedByBlock(true);
    }

}
