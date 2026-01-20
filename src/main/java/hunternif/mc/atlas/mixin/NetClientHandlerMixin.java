package hunternif.mc.atlas.mixin;

import hunternif.mc.atlas.AntiqueAtlasItems;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.api.AtlasNetHandler;
import hunternif.mc.atlas.api.impl.TileApiImpl;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.Tile;
import hunternif.mc.atlas.ext.ExtBiomeData;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.DeleteMarkerPacket;
import hunternif.mc.atlas.network.MapDataPacket;
import hunternif.mc.atlas.network.MarkersPacket;
import hunternif.mc.atlas.network.PutBiomeTilePacket;
import hunternif.mc.atlas.network.TileNameIDPacket;
import hunternif.mc.atlas.network.TilesPacket;
import hunternif.mc.atlas.util.ShortVec2;

import java.util.Map;

import net.minecraft.src.Minecraft;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.WorldClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NetClientHandler.class)
public class NetClientHandlerMixin implements AtlasNetHandler {
    @Shadow
    private Minecraft mc;
    @Shadow
    private WorldClient worldClient;

    public void handleMapData(MapDataPacket pkt) {
        if (pkt.data != null) {
            AtlasData atlasData = AntiqueAtlasItems.itemAtlas.getAtlasData(pkt.atlasID, this.worldClient);
            atlasData.readFromPacket(pkt);
        }
    }

    public void handleMapData(PutBiomeTilePacket pkt) {
        AtlasData data = AntiqueAtlasItems.itemAtlas.getAtlasData(pkt.atlasID, this.mc.theWorld);
        data.setTile(pkt.dimension, pkt.x, pkt.z, new Tile(pkt.biomeID));
    }

    public void handleMapData(TilesPacket pkt) {
        ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();

        for(Map.Entry<ShortVec2, Integer> entry : pkt.biomeMap.entrySet()) {
            ShortVec2 key = (ShortVec2)entry.getKey();
            data.setBiomeIdAt(pkt.dimension, key.x, key.y, (Integer)entry.getValue());
        }

    }

    public void handleMapData(MarkersPacket pkt) {
        MarkersData markersData;
        if (pkt.isGlobal()) {
            markersData = AntiqueAtlasMod.globalMarkersData.getData();
        } else {
            markersData = AntiqueAtlasItems.itemAtlas.getMarkersData(pkt.atlasID, this.mc.theWorld);
        }

        MarkersData markersData2 = markersData;

        for(Marker marker : pkt.markersByType.values()) {
            markersData2.loadMarker(marker);
        }

    }

    public void handleMapData(DeleteMarkerPacket pkt) {
        MarkersData markersData;
        if (pkt.isGlobal()) {
            markersData = AntiqueAtlasMod.globalMarkersData.getData();
        } else {
            markersData = AntiqueAtlasItems.itemAtlas.getMarkersData(pkt.atlasID, this.mc.theWorld);
        }

        markersData.removeMarker(pkt.markerID);
    }

    public void handleMapData(TileNameIDPacket pkt) {
        for(Map.Entry<String, Integer> entry : pkt.nameToIdMap.entrySet()) {
            ExtTileIdMap.instance().setPseudoBiomeID((String)entry.getKey(), (Integer)entry.getValue());
        }

        TileApiImpl tileAPI = (TileApiImpl)AtlasAPI.getTileAPI();
        tileAPI.onTileIdRegistered(pkt.nameToIdMap);
    }
}
