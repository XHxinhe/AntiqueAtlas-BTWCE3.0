package hunternif.atlas.ext;

import hunternif.atlas.AntiqueAtlasMod;
import hunternif.atlas.api.AtlasAPI;
import hunternif.atlas.marker. Marker;
import hunternif.atlas.marker. MarkersData;
import hunternif.atlas.util.Log;
import net.minecraft.src.*;

import java.util.HashSet;
import java.util. List;
import java.util.Set;

public class VillageWatcher {
    private final Set<Village> visited = new HashSet();

    public void onWorldLoad(World world) {
        if (! world.isRemote) {
            this.visitAllUnvisitedVillages(world);
        }
    }

    public void onPopulateChunkPost(World world) {
    }

    public void visitAllUnvisitedVillages(World world) {
        VillageCollection villageCollection = world.villageCollectionObj;

        if (villageCollection != null) {
            List villageList = villageCollection.getVillageList();

            for(Object o : villageList) {
                Village village = (Village)o;

                if (!this.visited.contains(village)) {
                    this.visitVillage(world, village);
                }
            }
        }
    }

    public void visitVillage(World world, Village village) {
        Log.info("Found village at ({}, {})", village.getCenter().posX, village.getCenter().posZ);

        int dim = world.provider.dimensionId;
        MarkersData markersData = AntiqueAtlasMod.globalMarkersData.getData();

        if (markersData == null) {
            Log.error("GlobalMarkersData is NULL!");
            return;
        }

        boolean foundMarker = false;
        int centerX = village.getCenter().posX;
        int centerZ = village.getCenter().posZ;

        for(int dx = -village.getVillageRadius(); dx <= village.getVillageRadius(); dx += 16) {
            for(int dz = -village.getVillageRadius(); dz <= village.getVillageRadius(); dz += 16) {
                if (dx * dx + dz * dz <= village.getVillageRadius() * village.getVillageRadius()) {
                    int chunkX = (centerX + dx) >> 4;
                    int chunkZ = (centerZ + dz) >> 4;
                    AtlasAPI.getTileAPI().putCustomGlobalTile(world, "npcVillageTerritory", chunkX, chunkZ);

                    List<Marker> markers = markersData.getMarkersAtChunk(dim, chunkX / 4, chunkZ / 4);
                    if (markers != null) {
                        for(Marker marker : markers) {
                            if(marker. getType().equals("village")) {
                                foundMarker = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (!foundMarker) {
            String label = I18n.getString("gui.antiqueatlas.marker.village");
            if (label == null || label.isEmpty()) {
                label = "Village";
            }
            Log.info("Creating village marker with label: {}", label);
            AtlasAPI.getMarkerAPI().putGlobalMarker(world, false, "village", label, centerX, centerZ);
        }

        for(Object o : village.getVillageDoorInfoList()) {
            VillageDoorInfo door = (VillageDoorInfo)o;
            AtlasAPI.getTileAPI().putCustomGlobalTile(world, "npcVillageDoor", door.posX >> 4, door.posZ >> 4);
        }

        this.visited.add(village);
    }
}