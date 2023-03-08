package fr.alexdoru.megawallsenhancementsmod.gui.guiapi;

import fr.alexdoru.megawallsenhancementsmod.config.ConfigHandler;
import net.minecraft.client.gui.ScaledResolution;

public interface IRenderer {

    /**
     * Returns the height of the HUD.
     *
     * @return The height in pixel.
     */
    int getHeight();

    /**
     * Returns the width of the HUD.
     *
     * @return The width in pixel.
     */
    int getWidth();

    /**
     * Renders the HUD.
     *
     * @param resolution - Screen's resolution
     */
    void render(ScaledResolution resolution);

    /**
     * Renders the dummy HUD, which is
     * used in the configuration screen
     * where you can move it around.
     */
    void renderDummy();

    /**
     * Can be used to disable the renderer
     * more conveniently than unregistering
     * it from the API.
     */
    boolean isEnabled(long currentTimeMillis);

    /**
     * Is called for each HUD when the configuration screen is closed
     * in order to save the now Gui position to the settings.
     */
    default void save() {
        ConfigHandler.saveConfig();
    }

    /**
     * Returns the GuiPosition of the Renderer
     */
    GuiPosition getGuiPosition();

}
