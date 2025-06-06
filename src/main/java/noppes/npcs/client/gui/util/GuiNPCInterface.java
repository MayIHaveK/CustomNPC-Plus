package noppes.npcs.client.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.TextBlockClient;
import noppes.npcs.entity.EntityNPCInterface;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class GuiNPCInterface extends GuiScreen {
    public EntityClientPlayerMP player;
    public boolean drawDefaultBackground = true;
    public EntityNPCInterface npc;
    protected HashMap<Integer, GuiNpcButton> buttons = new HashMap<Integer, GuiNpcButton>();
    protected HashMap<Integer, GuiMenuTopButton> topbuttons = new HashMap<Integer, GuiMenuTopButton>();
    protected HashMap<Integer, GuiMenuSideButton> sidebuttons = new HashMap<Integer, GuiMenuSideButton>();
    protected HashMap<Integer, GuiNpcTextField> textfields = new HashMap<Integer, GuiNpcTextField>();
    protected HashMap<Integer, GuiNpcLabel> labels = new HashMap<Integer, GuiNpcLabel>();
    protected HashMap<Integer, GuiCustomScroll> scrolls = new HashMap<Integer, GuiCustomScroll>();
    protected HashMap<Integer, GuiNpcSlider> sliders = new HashMap<Integer, GuiNpcSlider>();
    protected HashMap<Integer, GuiScreen> extra = new HashMap<Integer, GuiScreen>();
    protected HashMap<Integer, GuiScrollWindow> scrollWindows = new HashMap<>();
    protected HashMap<Integer, GuiDiagram> diagrams = new HashMap<>();

    public static boolean resizingActive = false;

    public String title;
    private ResourceLocation background = null;
    public boolean closeOnEsc = false;
    public int guiLeft, guiTop, xSize, ySize;
    private SubGuiInterface subgui;
    public int mouseX, mouseY, mouseScroll;
    public float bgScale = 1;
    public float bgScaleX = 1;
    public float bgScaleY = 1;
    public float bgScaleZ = 1;
    public long timeClosedSubGui;

    public GuiNPCInterface(EntityNPCInterface npc) {
        this.player = Minecraft.getMinecraft().thePlayer;
        this.npc = npc;
        title = "";
        xSize = 200;
        ySize = 222;
    }

    public GuiNPCInterface() {
        this(null);
    }

    public void setBackground(String texture) {
        background = new ResourceLocation("customnpcs", "textures/gui/" + texture);
    }

    public ResourceLocation getResource(String texture) {
        return new ResourceLocation("customnpcs", "textures/gui/" + texture);
    }

    @Override
    public void initGui() {
        super.initGui();
        GuiNpcTextField.unfocus();
        if (subgui != null) {
            subgui.setWorldAndResolution(mc, width, height);
            subgui.initGui();
        }
        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;
        buttonList.clear();
        labels.clear();
        textfields.clear();
        buttons.clear();
        sidebuttons.clear();
        topbuttons.clear();
        scrolls.clear();
        sliders.clear();
        scrollWindows.clear();
        diagrams.clear();
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void updateScreen() {
        if (subgui != null)
            subgui.updateScreen();
        else {
            for (GuiNpcTextField tf : textfields.values()) {
                if (tf.enabled)
                    tf.updateCursorCounter();
            }
            super.updateScreen();
        }
    }

    public void addExtra(GuiHoverText gui) {
        gui.setWorldAndResolution(mc, 350, 250);
        extra.put(gui.id, gui);
    }

    public void addScrollableGui(int id, GuiScrollWindow gui) {
        gui.setWorldAndResolution(mc, width, height);
        gui.initGui();
        scrollWindows.put(id, gui);
    }

    /**
     * Adds a GuiDiagram to the interface.
     * The diagram is initialized with the current resolution and cached.
     */
    public void addDiagram(int id, GuiDiagram diagram) {
        diagram.invalidateCache();
        diagrams.put(id, diagram);
    }

    public GuiDiagram getDiagram(int id) {
        return diagrams.get(id);
    }

    public void mouseClicked(int i, int j, int k) {
        if (subgui != null)
            subgui.mouseClicked(i, j, k);
        else {
            for (GuiNpcTextField tf : new ArrayList<GuiNpcTextField>(textfields.values()))
                if (tf.enabled)
                    tf.mouseClicked(i, j, k);

            for (GuiScrollWindow guiScrollableComponent : scrollWindows.values()) {
                guiScrollableComponent.mouseClicked(i, j, k);
            }

            if (k == 0) {
                for (GuiCustomScroll scroll : new ArrayList<GuiCustomScroll>(scrolls.values())) {
                    scroll.mouseClicked(i, j, k);
                }
            }
            // Process diagram mouse clicks
            for (GuiDiagram diagram : diagrams.values()) {
                if (diagram.isWithin(i, j)) {
                    if (diagram.mouseClicked(i, j, k))
                        return;
                }
            }
            mouseEvent(i, j, k);
            super.mouseClicked(i, j, k);
        }
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (subgui != null) {
            subgui.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
            return;
        }
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int state) {
        if (subgui != null) {
            subgui.mouseMovedOrUp(mouseX, mouseY, state);
            return;
        }
        super.mouseMovedOrUp(mouseX, mouseY, state);
    }

    public void mouseEvent(int i, int j, int k) {
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (subgui != null)
            subgui.buttonEvent(guibutton);
        else {
            buttonEvent(guibutton);
        }
    }

    public void buttonEvent(GuiButton guibutton) {
    }

    @Override
    public void keyTyped(char c, int i) {
        if (subgui != null)
            subgui.keyTyped(c, i);
        for (GuiNpcTextField tf : textfields.values())
            tf.textboxKeyTyped(c, i);

        for (GuiScrollWindow guiScrollableComponent : scrollWindows.values()) {
            guiScrollableComponent.keyTyped(c, i);
        }

        // Fixes closing sub with escape closes all of its parents
        boolean enoughTimeSinceSubClosed = Minecraft.getSystemTime() - timeClosedSubGui > 50;

        if (closeOnEsc && enoughTimeSinceSubClosed && (i == 1 || !GuiNpcTextField.isFieldActive() && isInventoryKey(i))) {
            close();
        }
    }

    public void onGuiClosed() {
        GuiNpcTextField.unfocus();
    }

    public void close() {
        Keyboard.enableRepeatEvents(false);
        displayGuiScreen(null);
        mc.setIngameFocus();
        save();
    }

    public void addButton(GuiNpcButton button) {
        buttons.put(button.id, button);
        buttonList.add(button);
    }

    public void addTopButton(GuiMenuTopButton button) {
        topbuttons.put(button.id, button);
        buttonList.add(button);
    }

    public void addSideButton(GuiMenuSideButton button) {
        sidebuttons.put(button.id, button);
        buttonList.add(button);
    }

    public GuiNpcButton getButton(int i) {
        return buttons.get(i);
    }

    public GuiMenuSideButton getSideButton(int i) {
        return sidebuttons.get(i);
    }

    public GuiMenuTopButton getTopButton(int i) {
        return topbuttons.get(i);
    }

    public void addTextField(GuiNpcTextField tf) {
        textfields.put(tf.id, tf);
    }

    public GuiNpcTextField getTextField(int i) {
        return textfields.get(i);
    }

    public void addLabel(GuiNpcLabel label) {
        labels.put(label.id, label);
    }

    public GuiNpcLabel getLabel(int i) {
        return labels.get(i);
    }

    public GuiScrollWindow getScrollableGui(int i) {
        return scrollWindows.get(i);
    }

    public void addSlider(GuiNpcSlider slider) {
        sliders.put(slider.id, slider);
        buttonList.add(slider);
    }

    public GuiNpcSlider getSlider(int i) {
        return sliders.get(i);
    }

    public void addScroll(GuiCustomScroll scroll) {
        scroll.setWorldAndResolution(mc, 350, 250);
        scrolls.put(scroll.id, scroll);
    }

    public GuiCustomScroll getScroll(int id) {
        return scrolls.get(id);
    }

    public abstract void save();

    @Override
    public void drawScreen(int i, int j, float f) {
        mouseX = i;
        mouseY = j;
        if (drawDefaultBackground && subgui == null)
            drawDefaultBackground();

        if (background != null && mc.renderEngine != null) {
            drawBackground();
        }

        boolean subGui = hasSubGui();
        drawCenteredString(fontRendererObj, title, width / 2, guiTop + 4, 0xffffff);
        for (GuiNpcLabel label : labels.values())
            label.drawLabel(this, fontRendererObj);
        for (GuiNpcTextField tf : textfields.values()) {
            tf.drawTextBox(i, j);
        }
        for (GuiCustomScroll scroll : scrolls.values()) {
            scroll.updateSubGUI(subGui);
            scroll.drawScreen(i, j, f, !subGui && scroll.isMouseOver(i, j) ? Mouse.getDWheel() : 0);
        }
        for (GuiScreen gui : extra.values())
            gui.drawScreen(i, j, f);
        // Draw scrollable windows.
        for (GuiScrollWindow guiScrollableComponent : scrollWindows.values()) {
            guiScrollableComponent.drawScreen(i, j, f, !subGui && guiScrollableComponent.isMouseOver(i, j) ? Mouse.getDWheel() : 0);
        }
        for (GuiDiagram diagram : diagrams.values()) {
            diagram.drawDiagram(i, j, subGui);
        }
        super.drawScreen(i, j, f);
        for (GuiCustomScroll scroll : scrolls.values())
            if (scroll.hoverableText) {
                scroll.drawHover(i, j);
            }
        for (GuiNpcButton button : buttons.values()) {
            button.updateSubGUI(subGui);
            if (!button.hoverableText.isEmpty()) {
                button.drawHover(i, j, subGui);
            }
        }

        if (subgui != null) {
            subgui.drawScreen(i, j, f);
        }
    }

    protected void drawBackground() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPushMatrix();
        GL11.glTranslatef(guiLeft, guiTop, 0);
        GL11.glScalef(bgScale * bgScaleX, bgScale * bgScaleY, bgScale * bgScaleZ);
        mc.renderEngine.bindTexture(background);
        if (xSize > 256) {
            drawTexturedModalRect(0, 0, 0, 0, 250, ySize);
            drawTexturedModalRect(250, 0, 256 - (xSize - 250), 0, xSize - 250, ySize);
        } else
            drawTexturedModalRect(0, 0, 0, 0, xSize, ySize);
        GL11.glPopMatrix();
    }

    protected void drawTextBlock(String text, int x, int y, int lineWidth) {
        TextBlockClient block = new TextBlockClient(StatCollector.translateToLocal(text), lineWidth, true, player);
        for (int line = 0; line < block.lines.size(); line++) {
            String lineText = block.lines.get(line).getFormattedText();
            fontRendererObj.drawString(lineText, x, y + (line * fontRendererObj.FONT_HEIGHT), CustomNpcResourceListener.DefaultTextColor);
        }
    }

    public FontRenderer getFontRenderer() {
        return this.fontRendererObj;
    }

    public void elementClicked() {
        if (subgui != null)
            subgui.elementClicked();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public void doubleClicked() {
    }

    public boolean isInventoryKey(int i) {
        return i == mc.gameSettings.keyBindInventory.getKeyCode(); // inventory key
    }

    @Override
    public void drawDefaultBackground() {
        super.drawDefaultBackground();
    }

    public void displayGuiScreen(GuiScreen gui) {
        mc.displayGuiScreen(gui);
    }

    public void setSubGui(SubGuiInterface gui) {
        subgui = gui;
        subgui.setWorldAndResolution(mc, width, height);
        subgui.parent = this;
        initGui();
    }

    public void closeSubGui(SubGuiInterface gui) {
        subgui = null;
        timeClosedSubGui = Minecraft.getSystemTime();
        initGui();
    }

    public boolean hasSubGui() {
        return subgui != null;
    }

    public SubGuiInterface getSubGui() {
        if (hasSubGui() && subgui.hasSubGui())
            return subgui.getSubGui();
        return subgui;
    }

    public void drawNpc(int x, int y) {
        drawNpc(npc, x, y, 1, 0);
    }

    public void drawNpc(EntityLivingBase entity, int x, int y, float zoomed, int rotation) {
        EntityNPCInterface npc = null;
        if (entity instanceof EntityNPCInterface)
            npc = (EntityNPCInterface) entity;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (npc != null)
            npc.isDrawn = true;
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef(guiLeft + x, guiTop + y, 50F);
        float scale = 1;
        if (entity.height > 2.4)
            scale = 2 / npc.height;

        GL11.glScalef(-30 * scale * zoomed, 30 * scale * zoomed, 30 * scale * zoomed);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

        float f2 = entity.renderYawOffset;
        float f3 = entity.rotationYaw;
        float f4 = entity.rotationPitch;
        float f7 = entity.rotationYawHead;
        float f5 = (float) (guiLeft + x) - mouseX;
        float f6 = (guiTop + y) - 50 * scale * zoomed - mouseY;
        int orientation = 0;
        if (npc != null) {
            orientation = npc.ais.orientation;
            npc.ais.orientation = rotation;
        }

        GL11.glRotatef(135F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-(float) Math.atan(f6 / 40F) * 20F, 1.0F, 0.0F, 0.0F);
        entity.renderYawOffset = rotation;
        entity.rotationYaw = (float) Math.atan(f5 / 80F) * 40F + rotation;
        entity.rotationPitch = -(float) Math.atan(f6 / 40F) * 20F;
        entity.rotationYawHead = entity.rotationYaw;
        GL11.glTranslatef(0.0F, entity.yOffset, 0.0F);
        RenderManager.instance.playerViewY = 180F;
        RenderManager.instance.renderEntityWithPosYaw(entity, 0, 0, 0, 0, 1);
        entity.prevRenderYawOffset = entity.renderYawOffset = f2;
        entity.prevRotationYaw = entity.rotationYaw = f3;
        entity.prevRotationPitch = entity.rotationPitch = f4;
        entity.prevRotationYawHead = entity.rotationYawHead = f7;
        if (npc != null) {
            npc.ais.orientation = orientation;
        }
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        if (npc != null)
            npc.isDrawn = false;
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public void renderHoveringText(List textLines, int x, int y, FontRenderer font) {
        this.drawHoveringText(textLines, x, y, font);
    }

    public void openLink(String link) {
        try {
            Class oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null);
            oclass.getMethod("browse", new Class[]{URI.class}).invoke(object, new URI(link));
        } catch (Throwable throwable) {
        }
    }
}
