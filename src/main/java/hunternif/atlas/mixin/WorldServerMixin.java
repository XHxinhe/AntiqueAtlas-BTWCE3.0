package hunternif.atlas.mixin;

import hunternif.atlas.AntiqueAtlasMod;

import net.minecraft.src.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldServer.class)
public class WorldServerMixin {

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(CallbackInfo ci) {
        WorldServer world = (WorldServer) (Object) this;
        if (!world.isRemote) {
            if (world.getTotalWorldTime() % 100 == 0) {
                if (AntiqueAtlasMod.villageWatcher != null) {
                    AntiqueAtlasMod.villageWatcher.visitAllUnvisitedVillages(world);
                }
            }
        }
    }
}