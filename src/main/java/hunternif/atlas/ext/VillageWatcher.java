package hunternif.atlas.ext;

import hunternif.atlas.AntiqueAtlasMod;
import hunternif.atlas.api.AtlasAPI;
import hunternif.atlas.marker.Marker;
import hunternif.atlas.marker.MarkersData;
import net.minecraft.src.*;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class VillageWatcher {
    private final Set<Village> visited = new HashSet();

    private final Set<Long> scannedChunks = new HashSet();

    public void onWorldLoad(World world) {
        if (!world.isRemote) {
            this.visitAllUnvisitedVillages(world);
        }
    }

    public void onPopulateChunkPost(World world) {
        if (!world.isRemote) {
            this.visitAllUnvisitedVillages(world);
        }
    }

    public void visitAllUnvisitedVillages(World world) {
        // Original Village
        VillageCollection villageCollection = world.villageCollectionObj;
        if (villageCollection != null) {
            List villageList = villageCollection.getVillageList();
            if (villageList != null) {
                for(Object o : villageList) {
                    Village village = (Village)o;
                    if (!this.visited.contains(village)) {
                        this.visitVillage(world, village);
                    }
                }
            }
        }

        // BTW Village
        for (Object p : world.playerEntities) {
            if (p instanceof EntityPlayer) {
                scanSurroundingChunks(world, (EntityPlayer) p);
            }
        }
    }

    public void scanSurroundingChunks(World world, EntityPlayer player) {
        int pChunkX = MathHelper.floor_double(player.posX) >> 4;
        int pChunkZ = MathHelper.floor_double(player.posZ) >> 4;

        int range = 10;

        for (int x = pChunkX - range; x <= pChunkX + range; x++) {
            for (int z = pChunkZ - range; z <= pChunkZ + range; z++) {
                long chunkKey = ChunkCoordIntPair.chunkXZ2Int(x, z);

                if (!scannedChunks.contains(chunkKey)) {
                    scannedChunks.add(chunkKey);

                    if (isVillageChunk(world, x, z)) {

                        int centerX = x * 16 + 8;
                        int centerZ = z * 16 + 8;

                        addVillageMarker(world, centerX, centerZ);
                    }
                }
            }
        }
    }

    private boolean isVillageChunk(World world, int chunkX, int chunkZ) {
        int k = 32;
        int l = 8;
        int i1 = chunkX;
        int j1 = chunkZ;

        if (chunkX < 0) chunkX -= k - 1;
        if (chunkZ < 0) chunkZ -= k - 1;

        int k1 = chunkX / k;
        int l1 = chunkZ / k;

        // MapGenVillage
        long i2 = (long)k1 * 341873128712L + (long)l1 * 132897987541L + world.getSeed() + 10387312L;
        Random random = new Random(i2);

        k1 *= k;
        l1 *= k;
        k1 += random.nextInt(k - l);
        l1 += random.nextInt(k - l);

        if (i1 == k1 && j1 == l1) {

            boolean isViable = world.getWorldChunkManager().areBiomesViable(i1 * 16 + 8, j1 * 16 + 8, 0, MapGenVillage.villageSpawnBiomes);
            return isViable;
        }
        return false;
    }

    private void addVillageMarker(World world, int centerX, int centerZ) {
        int dim = world.provider.dimensionId;
        MarkersData markersData = AntiqueAtlasMod.globalMarkersData.getData();
        boolean foundMarker = false;

        int chunkX = centerX >> 4;
        int chunkZ = centerZ >> 4;
        int regionX = Math.floorDiv(chunkX, 4);
        int regionZ = Math.floorDiv(chunkZ, 4);
        List<Marker> markers = markersData.getMarkersAtChunk(dim, regionX, regionZ);
        if (markers != null) {
            for (Marker marker : markers) {
                if (marker.getType().equals("village")) {
                    foundMarker = true;
                    break;
                }
            }
        }

        if (!foundMarker) {

            String labelKey = "gui.antiqueatlas.marker.village";

            AtlasAPI.getMarkerAPI().putGlobalMarker(world, false, "village", labelKey, centerX, centerZ);

            AtlasAPI.getTileAPI().putCustomGlobalTile(world, "npcVillageTerritory", chunkX, chunkZ);
        }
    }

    public void visitVillage(World world, Village village) {

        int centerX = village.getCenter().posX;
        int centerZ = village.getCenter().posZ;
        addVillageMarker(world, centerX, centerZ);

        for(Object o : village.getVillageDoorInfoList()) {
            VillageDoorInfo door = (VillageDoorInfo)o;
            AtlasAPI.getTileAPI().putCustomGlobalTile(world, "npcVillageDoor", door.posX >> 4, door.posZ >> 4);
        }
        this.visited.add(village);
    }
}