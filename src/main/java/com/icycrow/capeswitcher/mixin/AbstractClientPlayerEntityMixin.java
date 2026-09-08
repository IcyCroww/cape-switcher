package com.icycrow.capeswitcher.mixin;

import com.icycrow.capeswitcher.cape.CapeEntry;
import com.icycrow.capeswitcher.cape.CapeManager;
import com.icycrow.capeswitcher.config.CapeConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin {

    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    private void capeswitcher_modifyCapeTexture(CallbackInfoReturnable<SkinTextures> cir) {
        CapeManager manager = CapeManager.getInstance();
        if (manager == null) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        AbstractClientPlayerEntity self = (AbstractClientPlayerEntity) (Object) this;
        if (client.player != self) return;

        SkinTextures original = cir.getReturnValue();

        // Preview cape takes priority (for GUI hover preview)
        CapeEntry previewCape = manager.getPreviewCape();
        if (previewCape != null) {
            AssetInfo.TextureAsset tex = new AssetInfo.TextureAssetInfo(previewCape.getTextureId());
            cir.setReturnValue(new SkinTextures(
                    original.body(),
                    tex, tex,
                    original.model(), original.secure()
            ));
            return;
        }

        CapeConfig config = manager.getConfig();
        String mode = config.getCapeMode();

        if ("account".equals(mode)) {
            return;
        }

        if ("none".equals(mode)) {
            cir.setReturnValue(new SkinTextures(
                    original.body(),
                    null, null,
                    original.model(), original.secure()
            ));
            return;
        }

        if ("local".equals(mode)) {
            Identifier capeTexture = manager.getCurrentCapeTexture();
            if (capeTexture == null) return;
            AssetInfo.TextureAsset cape = new AssetInfo.TextureAssetInfo(capeTexture);

            cir.setReturnValue(new SkinTextures(
                    original.body(),
                    cape, cape,
                    original.model(), original.secure()
            ));
        }
    }
}

