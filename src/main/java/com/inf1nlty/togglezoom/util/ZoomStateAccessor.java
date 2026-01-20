package com.inf1nlty.togglezoom.util;

/**
 * Original interface kept unchanged to satisfy existing mixins (e.g. MinecraftMixin).
 */
public interface ZoomStateAccessor {
    boolean zoom$isToggleZoomActive();
    boolean zoom$isToggleZoomKeyHeld();
}