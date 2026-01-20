package hunternif.atlas.mixin;

import hunternif.atlas.AntiqueAtlasMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.WorldServer;
import org.spongepowered. asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm. mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Shadow
    public WorldServer[] worldServers;

    @Inject(method = "loadAllWorlds", at = @At("RETURN"))
    private void onWorldsLoaded(CallbackInfo ci) {
        if (worldServers != null) {
            for (WorldServer world :  worldServers) {
                if (world != null) {
                    AntiqueAtlasMod.onWorldLoad(world);
                }
            }
        }
    }
}