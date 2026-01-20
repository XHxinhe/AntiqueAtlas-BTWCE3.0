package btw.community.antiqueatlas;

import api.AddonHandler;
import api.BTWAddon;

import hunternif.mc.atlas.AntiqueAtlasItems;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.AntiqueAtlasRecipes;
import hunternif.mc.atlas.network.AntiqueAtlasNetwork;

public class AntiqueAtlasAddon extends BTWAddon {

    @Override
    public void initialize() {
        AddonHandler.logMessage(getName() + " v" + getVersionString() + " Initializing...");

        AntiqueAtlasMod.initialize(this);

        AntiqueAtlasItems.registerItems();

        AntiqueAtlasRecipes.registerRecipes();

        AntiqueAtlasNetwork.register(this);

        AddonHandler.logMessage(getName() + " initialized successfully!");
    }
}