package xyz.qweru.geo.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import xyz.qweru.geo.client.module.visual.ModuleViewModel;
import xyz.qweru.geo.core.system.SystemCache;
import xyz.qweru.geo.core.system.Systems;
import xyz.qweru.geo.core.system.module.Modules;

@Mixin(ItemInHandRenderer.class)
public abstract class HeldItemRendererMixin {

    @Shadow @Final private Minecraft minecraft;

    @Shadow protected abstract void swingArm(float swingProgress, float equipProgress, PoseStack matrices, int armX, HumanoidArm arm);

    @Unique
    SystemCache.Cached<ModuleViewModel> cache = SystemCache.INSTANCE.getModule(ModuleViewModel.class);

    @Unique
    ModuleViewModel viewModel = null;

    @ModifyExpressionValue(method = "renderHandsWithItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F", ordinal = 0))
    private float removeLerpPitch(float original, @Local(argsOnly = true) LocalPlayer player, @Local(argsOnly = true) float td) {
        viewModel = cache.cast();
        return viewModel.getEnabled() && !viewModel.getHandInterpolation() ? player.getXRot(td) : original;
    }

    @ModifyExpressionValue(method = "renderHandsWithItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F", ordinal = 1))
    private float removeLerpYaw(float original, @Local(argsOnly = true) LocalPlayer player, @Local(argsOnly = true) float td) {
        return viewModel.getEnabled() && !viewModel.getHandInterpolation() ? player.getYRot(td) : original;
    }

    @Inject(method = "applyItemArmTransform", at = @At("HEAD"), cancellable = true)
    private void noEquipOffset(PoseStack matrices, HumanoidArm arm, float f, CallbackInfo ci) {
        if (!viewModel.getEnabled() || viewModel.getEquipOffset()) return;
        ci.cancel();
        int i = arm == HumanoidArm.RIGHT ? 1 : -1;
        matrices.translate((float)i * 0.56F, -0.52F, -0.72F);
//        matrices.translate((float)i * 0.56F, 0F, -0.72F);
    }

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    private void onRenderItem(AbstractClientPlayer abstractClientPlayer, float f, float g, InteractionHand hand, float h, ItemStack itemStack, float i, PoseStack matrices, MultiBufferSource multiBufferSource, int j, CallbackInfo ci) {
        if (!viewModel.getEnabled()) return;
        Vector3f scale = viewModel.getScale(hand);
        Vector3f off = viewModel.getOffset(hand);
        Vector3f rot = viewModel.getRot(hand);

        matrices.scale(scale.x, scale.y, scale.z);
        matrices.translate(off.x, off.y, off.z);

        matrices.mulPose(Axis.XP.rotationDegrees(rot.x));
        matrices.mulPose(Axis.YP.rotationDegrees(rot.y));
        matrices.mulPose(Axis.ZP.rotationDegrees(rot.z));
    }

    @WrapMethod(method = "applyItemArmAttackTransform")
    private void applySwingOffset(PoseStack matrices, HumanoidArm arm, float swingProgress, Operation<Void> original) {
        if (!viewModel.getEnabled()) {
            original.call(matrices, arm, swingProgress);
            return;
        }
        int side = arm == HumanoidArm.RIGHT ? 1 : -1;
        float sqProgress = Mth.sin(swingProgress * swingProgress * (float)Math.PI);
        float sqrtProgress = Mth.sin(Mth.sqrt(swingProgress) * (float)Math.PI);
        matrices.mulPose(Axis.YP.rotationDegrees((float)side * (viewModel.getSwingRPYOff() + sqProgress * viewModel.getSwingRPY())));
        matrices.mulPose(Axis.ZP.rotationDegrees((float)side * sqrtProgress * viewModel.getSwingRPZ()));
        matrices.mulPose(Axis.XP.rotationDegrees(sqrtProgress * viewModel.getSwingRPX()));
        matrices.mulPose(Axis.YP.rotationDegrees((float)side * viewModel.getSwingRAY()));
    }

    @ModifyArgs(method = "swingArm", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
    private void changeSwingPos(Args args, @Local(ordinal = 0, argsOnly = true) float swingProgress, @Local(argsOnly = true) int hand) {
        if (!viewModel.getEnabled()) return;
        float x = viewModel.getSwingX() * Mth.sin(Mth.sqrt(swingProgress) * (float)Math.PI) * hand;
        float y = viewModel.getSwingY() * Mth.sin(Mth.sqrt(swingProgress) * ((float)Math.PI * 2F));
        float z = viewModel.getSwingZ() * Mth.sin(swingProgress * (float)Math.PI);
        args.set(0, x);
        args.set(1, y);
        args.set(2, z);
    }

    @ModifyExpressionValue(method = "applyEatTransform", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;abs(F)F", ordinal = 0))
    private float eatHeight(float original) {
        return (viewModel.getEnabled() ? viewModel.getEatJitter() : 1) * original;
    }

    @ModifyArgs(method = "applyEatTransform", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", ordinal = 1))
    private void changePos(Args args, @Local(ordinal = 3) float progress, @Local int arm) {
        if (!viewModel.getEnabled()) return;
        args.set(0, viewModel.getEatX() * progress * arm);
        args.set(1, viewModel.getEatY() * progress);
        args.set(2, viewModel.getEatZ() * progress);
    }

    @ModifyArgs(method = "applyEatTransform", at = @At(value = "INVOKE", target = "Lcom/mojang/math/Axis;rotationDegrees(F)Lorg/joml/Quaternionf;", ordinal = 0))
    private void changeDegY(Args args, @Local(ordinal = 3) float progress, @Local int arm) {
        if (!viewModel.getEnabled()) return;
        args.set(0, viewModel.getEatRY() * arm * progress);
    }

    @ModifyArgs(method = "applyEatTransform", at = @At(value = "INVOKE", target = "Lcom/mojang/math/Axis;rotationDegrees(F)Lorg/joml/Quaternionf;", ordinal = 1))
    private void changeDegX(Args args, @Local(ordinal = 3) float progress, @Local int arm) {
        if (!viewModel.getEnabled()) return;
        args.set(0, viewModel.getEatRX() * progress);
    }

    @ModifyArgs(method = "applyEatTransform", at = @At(value = "INVOKE", target = "Lcom/mojang/math/Axis;rotationDegrees(F)Lorg/joml/Quaternionf;", ordinal = 2))
    private void changeDegZ(Args args, @Local(ordinal = 3) float progress, @Local int arm) {
        if (!viewModel.getEnabled()) return;
        args.set(0, viewModel.getEatRZ() * arm * progress);
    }

    @WrapMethod(method = "swingArm")
    private void swingArm(float f, float g, PoseStack poseStack, int i, HumanoidArm humanoidArm, Operation<Void> original) {
        if (!viewModel.getEnabled() || viewModel.getMode() != ModuleViewModel.Mode.NORMAL) {
            original.call(f, g, poseStack, i, humanoidArm);
        } else {
            viewModel.swing(f, g, poseStack, i, humanoidArm);
        }
    }
}
