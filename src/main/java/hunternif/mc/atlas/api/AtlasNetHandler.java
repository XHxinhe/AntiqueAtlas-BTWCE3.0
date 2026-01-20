package hunternif.mc.atlas.api;

import hunternif.mc.atlas.network.AddMarkerPacket;
import hunternif.mc.atlas.network.DeleteMarkerPacket;
import hunternif.mc.atlas.network.MapDataPacket;
import hunternif.mc.atlas.network.MarkersPacket;
import hunternif.mc.atlas.network.PutBiomeTilePacket;
import hunternif.mc.atlas.network.RegisterTileIdPacket;
import hunternif.mc.atlas.network.TileNameIDPacket;
import hunternif.mc.atlas.network.TilesPacket;

public interface AtlasNetHandler {
    default void handleMapData(MapDataPacket pkt) {
    }

    default void handleMapData(RegisterTileIdPacket pkt) {
    }

    default void handleMapData(PutBiomeTilePacket pkt) {
    }

    default void handleMapData(TilesPacket pkt) {
    }

    default void handleMapData(MarkersPacket pkt) {
    }

    default void handleMapData(AddMarkerPacket pkt) {
    }

    default void handleMapData(TileNameIDPacket pkt) {
    }

    default void handleMapData(DeleteMarkerPacket pkt) {
    }
}
