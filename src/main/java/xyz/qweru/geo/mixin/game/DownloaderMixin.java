package xyz.qweru.geo.mixin.game;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.packs.DownloadQueue;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.nio.file.Path;
import java.util.UUID;

import static xyz.qweru.geo.core.Core.mc;

/**
 * @see <a href="https://github.com/CCBlueX/LiquidBounce/blob/nextgen/src/main/java/net/ccbluex/liquidbounce/injection/mixins/minecraft/util/MixinDownloader.java">MixinDownloader.java</a>
 */
@Mixin(DownloadQueue.class)
public class DownloaderMixin {
    @Shadow @Final private Path cacheDir;

    @ModifyExpressionValue(method = "method_55485", at = @At(value = "INVOKE", target = "Ljava/nio/file/Path;resolve(Ljava/lang/String;)Ljava/nio/file/Path;"))
    private Path hookResolve(Path original, @Local(argsOnly = true) UUID id) {
        UUID accountId = mc.getUser().getProfileId();
        return cacheDir.resolve(accountId.toString()).resolve(id.toString());
    }
}
