package hunternif.mc.atlas;

import hunternif.mc. atlas.item.ItemAtlas;
import hunternif.mc. atlas.item.ItemEmptyAtlas;

public class AntiqueAtlasItems {

    public static ItemEmptyAtlas emptyAtlas;
    public static ItemAtlas itemAtlas;

    public static void registerItems() {

        emptyAtlas = new ItemEmptyAtlas(23601);

        itemAtlas = new ItemAtlas(23602);
    }
}