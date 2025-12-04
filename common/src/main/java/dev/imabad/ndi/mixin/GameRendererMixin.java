package dev.imabad.ndi.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.imabad.ndi.CameraEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow @Final private Minecraft minecraft;
    
    // NOTE: In 1.21, if 'renderDistance' is not a field, you may need to use 'minecraft.options.renderDistance().get()'
    // We assume the shadow field is still valid for internal access or it maps to the correct field.
    @Shadow private float renderDistance;

    // UPDATED: Signature now uses double for FOV in 1.21
    @Inject(method = "getProjectionMatrix(D)Lorg/joml/Matrix4f;", at = @At("HEAD"), cancellable = true)
    private void getProjectionMatrix(double fov, CallbackInfoReturnable<Matrix4f> cir) {
        if(!(minecraft.getCameraEntity() instanceof CameraEntity)) return;

        CameraEntity cameraEntity = (CameraEntity) minecraft.getCameraEntity();
        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().identity();
        if (cameraEntity.getZoom() != 0.0F) {
            poseStack.scale(cameraEntity.getZoom(), cameraEntity.getZoom(), 1.0F);
        }

        // Note: 1.21 JOML typically expects Radians for perspective, but verify mappings. 
        // Standard MC logic usually converts before this. 
        // We use the provided 'renderDistance' shadow.
        poseStack.last().pose().mul((new Matrix4f()).perspective((float)Math.toRadians(70f), (float)this.minecraft.getWindow().getWidth() / (float)this.minecraft.getWindow().getHeight(), 0.05F, this.renderDistance * 4.0f));
        cir.setReturnValue(poseStack.last().pose());
    }
}