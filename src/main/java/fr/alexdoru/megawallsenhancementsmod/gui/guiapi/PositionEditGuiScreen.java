package fr.alexdoru.megawallsenhancementsmod.gui.guiapi;

import fr.alexdoru.megawallsenhancementsmod.utils.DelayedTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class PositionEditGuiScreen extends GuiScreen {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private final IRenderer renderer;
    private final GuiPosition guiPosition;
    private final GuiScreen parent;
    private int prevX, prevY;

    public PositionEditGuiScreen(IRenderer renderer, GuiScreen parent) {
        this.renderer = renderer;
        this.guiPosition = renderer.getGuiPosition();
        this.parent = parent;
        adjustBounds();
    }

    @Override
    public void drawScreen(int x, int y, float partialTicks) {
        super.drawDefaultBackground();
        renderer.renderDummy();
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        this.prevX = x;
        this.prevY = y;
    }

    @Override
    protected void mouseClickMove(int x, int y, int button, long time) {
        moveSelectedRendererBy(x - prevX, y - prevY);
        this.prevX = x;
        this.prevY = y;
    }

    private void moveSelectedRendererBy(int offsetX, int offsetY) {
        final ScaledResolution res = new ScaledResolution(mc);
        this.guiPosition.updateAbsolutePosition(res);
        this.guiPosition.setAbsolutePosition(res, this.guiPosition.getAbsoluteRenderX() + offsetX, this.guiPosition.getAbsoluteRenderY() + offsetY);
        //adjustBounds();
    }

    @Override
    public void onGuiClosed() {
        renderer.save();
        new DelayedTask(() -> mc.displayGuiScreen(parent), 0);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    /**
     * Makes sure the HUD can't get out of the screen
     */
    private void adjustBounds() {
        final ScaledResolution res = new ScaledResolution(mc);
        final int screenWidth = res.getScaledWidth();
        final int screenHeight = res.getScaledHeight();
        final int absoluteX = Math.max(0, Math.min((int) (guiPosition.getRelativeX() * res.getScaledWidth()), Math.max(screenWidth - this.renderer.getWidth(), 0)));
        final int absoluteY = Math.max(0, Math.min((int) (guiPosition.getRelativeY() * res.getScaledHeight()), Math.max(screenHeight - this.renderer.getHeight(), 0)));
        this.guiPosition.setAbsolutePosition(res, absoluteX, absoluteY);
    }

}
