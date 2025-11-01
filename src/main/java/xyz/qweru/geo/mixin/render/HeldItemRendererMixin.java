package xyz.qweru.geo.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
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
import xyz.qweru.geo.client.helper.entity.EntityHelper;
import xyz.qweru.geo.client.helper.player.PlayerHelper;
import xyz.qweru.geo.client.module.visual.ModuleViewModel;
import xyz.qweru.geo.core.system.Systems;
import xyz.qweru.geo.core.system.module.Modules;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    @Shadow @Final private MinecraftClient client;

    @Shadow protected abstract void swingArm(float swingProgress, float equipProgress, MatrixStack matrices, int armX, Arm arm);

    @Shadow private float lastEquipProgressMainHand;
    @Shadow private float equipProgressMainHand;
    @Shadow private float lastEquipProgressOffHand;
    @Shadow private float equipProgressOffHand;
    @Unique
    ModuleViewModel viewModel = null;

    @ModifyExpressionValue(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F", ordinal = 0))
    private float removeLerpPitch(float original, @Local(argsOnly = true) ClientPlayerEntity player, @Local(argsOnly = true) float td) {
        viewModel = Systems.INSTANCE.get(Modules.class).get(ModuleViewModel.class);
        return viewModel.getEnabled() && !viewModel.getHandInterp() ? player.getPitch(td) : original;
    }

    @ModifyExpressionValue(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F", ordinal = 1))
    private float removeLerpYaw(float original, @Local(argsOnly = true) ClientPlayerEntity player, @Local(argsOnly = true) float td) {
        return viewModel.getEnabled() && !viewModel.getHandInterp() ? player.getYaw(td) : original;
    }

    @Inject(method = "applyEquipOffset", at = @At("HEAD"), cancellable = true)
    private void noEquipOffset(MatrixStack matrices, Arm arm, float equipProgress, CallbackInfo ci) {
        if (!viewModel.getEnabled() || viewModel.getEquipOffset()) return;
        ci.cancel();
        int i = arm == Arm.RIGHT ? 1 : -1;
        matrices.translate((float)i * 0.56F, -0.52F, -0.72F);
//        matrices.translate((float)i * 0.56F, 0F, -0.72F);
    }

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", shift = At.Shift.BEFORE))
    private void onRenderItem(AbstractClientPlayerEntity player, float tickProgress, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!viewModel.getEnabled()) return;
        Vector3f scale = viewModel.getScale(hand);
        Vector3f off = viewModel.getOffset(hand);
        Vector3f rot = viewModel.getRot(hand);

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rot.x));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rot.y));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rot.z));

        matrices.scale(scale.x, scale.y, scale.z);
        matrices.translate(off.x, off.y, off.z);
    }

    @WrapMethod(method = "applySwingOffset")
    private void applySwingOffset(MatrixStack matrices, Arm arm, float swingProgress, Operation<Void> original) {
        if (!viewModel.getEnabled()) {
            original.call(matrices, arm, swingProgress);
            return;
        }
        int side = arm == Arm.RIGHT ? 1 : -1;
        float sqProgress = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float sqrtProgress = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)side * (viewModel.getSwingRPYOff() + sqProgress * viewModel.getSwingRPY())));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)side * sqrtProgress * viewModel.getSwingRPZ()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(sqrtProgress * viewModel.getSwingRPX()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)side * viewModel.getSwingRAY()));
    }

    @ModifyArgs(method = "swingArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"))
    private void changeSwingPos(Args args, @Local(ordinal = 0, argsOnly = true) float swingProgress) {
        if (!viewModel.getEnabled()) return;
        float x = viewModel.getSwingX() * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
        float y = viewModel.getSwingY() * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float)Math.PI * 2F));
        float z = viewModel.getSwingZ() * MathHelper.sin(swingProgress * (float)Math.PI);
        args.set(0, x);
        args.set(1, y);
        args.set(2, z);
    }

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyEatOrDrinkTransformation(Lnet/minecraft/client/util/math/MatrixStack;FLnet/minecraft/util/Arm;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;)V"))
    private void onEat(AbstractClientPlayerEntity player, float tickProgress, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        Arm arm = EntityHelper.INSTANCE.getArm(client.player, hand);
        if (viewModel.getEnabled() && viewModel.getBlockHit())
            swingArm(swingProgress, equipProgress, matrices, arm == Arm.RIGHT ? 1 : -1, arm);
    }

    @ModifyExpressionValue(method = "applyEatOrDrinkTransformation", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;abs(F)F", ordinal = 0))
    private float eatHeight(float original) {
        return (viewModel.getEnabled() ? viewModel.getEatJitter() : 1) * original;
    }

    @ModifyArgs(method = "applyEatOrDrinkTransformation", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", ordinal = 1))
    private void changePos(Args args, @Local(ordinal = 3) float progress, @Local int arm) {
        if (!viewModel.getEnabled()) return;
        args.set(0, viewModel.getEatX() * progress * arm);
        args.set(1, viewModel.getEatY() * progress);
        args.set(2, viewModel.getEatZ() * progress);
    }

    @ModifyArgs(method = "applyEatOrDrinkTransformation", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/RotationAxis;rotationDegrees(F)Lorg/joml/Quaternionf;", ordinal = 0))
    private void changeDegY(Args args, @Local(ordinal = 3) float progress, @Local int arm) {
        if (!viewModel.getEnabled()) return;
        args.set(0, viewModel.getEatRY() * arm * progress);
    }

    @ModifyArgs(method = "applyEatOrDrinkTransformation", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/RotationAxis;rotationDegrees(F)Lorg/joml/Quaternionf;", ordinal = 1))
    private void changeDegX(Args args, @Local(ordinal = 3) float progress, @Local int arm) {
        if (!viewModel.getEnabled()) return;
        args.set(0, viewModel.getEatRX() * progress);
    }

    @ModifyArgs(method = "applyEatOrDrinkTransformation", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/RotationAxis;rotationDegrees(F)Lorg/joml/Quaternionf;", ordinal = 2))
    private void changeDegZ(Args args, @Local(ordinal = 3) float progress, @Local int arm) {
        if (!viewModel.getEnabled()) return;
        args.set(0, viewModel.getEatRZ() * arm * progress);
    }
}
