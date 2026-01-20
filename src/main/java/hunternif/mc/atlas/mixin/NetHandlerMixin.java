package hunternif.mc.atlas.mixin;

import hunternif.mc.atlas.api.AtlasNetHandler;
import hunternif.mc.atlas.network.AddMarkerPacket;
import hunternif.mc.atlas.network.DeleteMarkerPacket;
import hunternif.mc.atlas.network.MapDataPacket;
import hunternif.mc.atlas.network.MarkersPacket;
import hunternif.mc.atlas.network.PutBiomeTilePacket;
import hunternif.mc.atlas.network.RegisterTileIdPacket;
import hunternif.mc.atlas.network.TileNameIDPacket;
import hunternif.mc.atlas.network.TilesPacket;

import net.minecraft.src.NetHandler;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(NetHandler.class)
public class NetHandlerMixin implements AtlasNetHandler {
    public void handleMapData(MapDataPacket pkt) {
    }

    public void handleMapData(RegisterTileIdPacket pkt) {
    }

    public void handleMapData(PutBiomeTilePacket pkt) {
    }

    public void handleMapData(TilesPacket pkt) {
    }

    public void handleMapData(MarkersPacket pkt) {
    }

    public void handleMapData(AddMarkerPacket pkt) {
    }

    public void handleMapData(TileNameIDPacket pkt) {
    }

    public void handleMapData(DeleteMarkerPacket pkt) {
    }
}
