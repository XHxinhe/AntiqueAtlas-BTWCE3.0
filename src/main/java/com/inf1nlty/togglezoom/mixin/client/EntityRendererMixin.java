package com.inf1nlty.togglezoom.mixin.client;

import com.inf1nlty.togglezoom.util.KeyBindings;
import com.inf1nlty.togglezoom.util.ZoomStateAccessor;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin implements ZoomStateAccessor {
    @Shadow
    private Minecraft mc;
    @Shadow
    private double cameraZoom;
    @Shadow
    private float farPlaneDistance;
    @Shadow
    public ItemRenderer itemRenderer;
    @Unique
    private boolean ToggleZoomActive = false;
    @Unique
    private boolean ToggleToggleZoomKeyWasDown = false;
    @Unique
    private double targetZoom = 1.0D;
    //    @Unique private double lastZoomLevel = 4.0D;

    @Shadow
    protected abstract void hurtCameraEffect(float partialTicks);

    @Shadow
    protected abstract void setupViewBobbing(float partialTicks);

    @Shadow
    protected abstract void enableLightmap(double partialTicks);

    @Shadow
    protected abstract void disableLightmap(double partialTicks);

    @Shadow
    protected abstract float getFOVModifier(float partialTicks, boolean useFOVSetting);

    @Override
    public boolean zoom$isToggleZoomActive() {
        return ToggleZoomActive;
    }

    @Override
    public boolean zoom$isToggleZoomKeyHeld() {
        return Keyboard.isKeyDown(KeyBindings.ZoomToggle.keyCode);
    }

    @Inject(method = "updateCameraAndRender", at = @At("HEAD"))
    private void zoom$injectZoomCamera(float partialTicks, CallbackInfo ci) {
        if (mc == null || mc.thePlayer == null) return;

        boolean zoomKeyDown = Keyboard.isKeyDown(KeyBindings.ZoomToggle.keyCode);
        // toggle
        if (zoomKeyDown && !ToggleToggleZoomKeyWasDown && mc.currentScreen == null) {
            ToggleZoomActive = !ToggleZoomActive;
            targetZoom = ToggleZoomActive ? 4.0D : 1.0D;
        }
        ToggleToggleZoomKeyWasDown = zoomKeyDown;

        // Allow scroll wheel to set zoom only in the frame just activated
        if (ToggleZoomActive && zoomKeyDown) {
            int wheel = Mouse.getDWheel();
            if (wheel != 0 && mc.currentScreen == null) {
                double step;
                if (targetZoom >= 12.0D) {
                    step = 2.0D;
                } else if (targetZoom >= 8.0D) {
                    step = 1.5D;
                } else if (targetZoom >= 4.0D) {
                    step = 1.0D;
                } else if (targetZoom >= 1.5D) {
                    step = 0.25D;
                } else {
                    step = 0.1D;
                }
                if (wheel > 0) {
                    targetZoom += step;
                } else {
                    targetZoom -= step;
                }
                if (targetZoom < 1.0D) targetZoom = 1.0D;
                if (targetZoom > 32.0D) targetZoom = 32.0D;
//                lastZoomLevel = targetZoom;
            }
        }
        double absDelta = Math.abs(targetZoom - cameraZoom);
        if (targetZoom > 1.0D) {
            double lerpSpeed = Math.min(0.04, Math.max(0.008, absDelta * 0.02));
            if (absDelta > 0.01D) {
                cameraZoom += (targetZoom - cameraZoom) * lerpSpeed;
            } else {
                cameraZoom = targetZoom;
            }
        } else {
            double lerpSpeed = Math.min(0.14, Math.max(0.03, absDelta * 0.10));
            if (absDelta > 0.001D) {
                cameraZoom += (targetZoom - cameraZoom) * lerpSpeed;
            } else {
                cameraZoom = targetZoom;
            }
        }
    }

    @Inject(method = "renderHand(FI)V", at = @At(
            value = "INVOKE",
            target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V",
            shift = At.Shift.AFTER
    ))
    private void zoom$scaleViewmodel(float partialTicks, int pass, CallbackInfo ci) {
        float zoom = (float) cameraZoom;
        if (zoom != 1.0F) {
            GL11.glScalef(zoom, zoom, zoom);
        }
    }

    @Inject(method = "getFOVModifier(FZ)F", at = @At("RETURN"), cancellable = true)
    private void zoom$setZoomFOV(float par1, boolean par2, CallbackInfoReturnable<Float> cir) {
        float fov = cir.getReturnValue();
        float zoom = (float) cameraZoom;
        if (zoom != 1.0F) {
            cir.setReturnValue(fov / zoom);
        }
    }

    /**
     * Force render hand when zoomed by injecting after the zoom check
     */
    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glClear(I)V", shift = At.Shift.AFTER))
    private void zoom$forceRenderHandWhenZoomed(float partialTicks, long timeSlice, CallbackInfo ci) {
        // Check if we're in a zoom state and the normal hand rendering was skipped
        if (this.cameraZoom != 1.0D) {
            // Render hand manually when zoomed
            this.zoom$renderHandForZoom(partialTicks, 0);
        }
    }

    /**
     * Custom hand rendering method for zoom
     */
    @Unique
    private void zoom$renderHandForZoom(float partialTicks, int pass) {
        if (this.mc.gameSettings.thirdPersonView == 0 && !this.mc.renderViewEntity.isPlayerSleeping() && !this.mc.gameSettings.hideGUI && !this.mc.playerController.enableEverythingIsScrewedUpMode()) {
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            float var3 = 0.07F;
            if (this.mc.gameSettings.anaglyph) {
                GL11.glTranslatef((float) (-(pass * 2 - 1)) * var3, 0.0F, 0.0F);
            }

            if (this.cameraZoom != 1.0D) {
                GL11.glTranslatef((float) 0, (float) 0, 0.0F);
                GL11.glScaled(this.cameraZoom, this.cameraZoom, 1.0D);
            }

            org.lwjgl.util.glu.Project.gluPerspective(this.getFOVModifier(partialTicks, false), (float) this.mc.displayWidth / (float) this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);

            if (this.mc.playerController.enableEverythingIsScrewedUpMode()) {
                float var4 = 0.6666667F;
                GL11.glScalef(1.0F, var4, 1.0F);
            }

            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            if (this.mc.gameSettings.anaglyph) {
                GL11.glTranslatef((float) (pass * 2 - 1) * 0.1F, 0.0F, 0.0F);
            }

            GL11.glPushMatrix();
            this.hurtCameraEffect(partialTicks);
            if (this.mc.gameSettings.viewBobbing) {
                this.setupViewBobbing(partialTicks);
            }

            this.enableLightmap(partialTicks);
            this.itemRenderer.renderItemInFirstPerson(partialTicks);
            this.disableLightmap(partialTicks);

            GL11.glPopMatrix();

            if (!this.mc.renderViewEntity.isPlayerSleeping()) {
                this.itemRenderer.renderOverlays(partialTicks);
                this.hurtCameraEffect(partialTicks);
            }

            if (this.mc.gameSettings.viewBobbing) {
                this.setupViewBobbing(partialTicks);
            }
        }
    }
}