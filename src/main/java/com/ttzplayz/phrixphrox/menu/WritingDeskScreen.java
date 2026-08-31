package com.ttzplayz.phrixphrox.menu;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class WritingDeskScreen extends AbstractContainerScreen<WritingDeskMenu> {
    private static final Identifier UNEQUIPPED = gui("writing_desk_unequipped.png");
    private static final Identifier NO_STYLUS = gui("writing_desk_tablet_no_stylus.png");
    private static final Identifier WITH_TABLET = gui("writing_desk_gui_with_tablet.png");
    private static final Identifier FINISHED = gui("writing_desk_gui_finished.png");

    private static final int TABLET_X0 = 73;
    private static final int TABLET_Y0 = 17;
    private static final int TABLET_X1 = 135;
    private static final int TABLET_Y1 = 98;

    private static final int LABEL_COLOR = -12566464;

    private static final int[] TEXT_ROWS = { 52, 63, 74, 85 };
    private static final String[] TEST_LINES = { "test 1", "test 2", "test 3", "test 4" };

    private static Identifier gui(String name) {
        return Identifier.fromNamespaceAndPath(PhrixPhrox.MOD_ID, "textures/gui/writing_desk/" + name);
    }

    public WritingDeskScreen(WritingDeskMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 208, 192);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    private Identifier background() {
        return switch (menu.state()) {
            case UNEQUIPPED -> UNEQUIPPED;
            case NO_STYLUS -> NO_STYLUS;
            case WITH_TABLET -> WITH_TABLET;
            case FINISHED -> FINISHED;
        };
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        graphics.blit(RenderPipelines.GUI_TEXTURED, background(), leftPos, topPos, 0, 0,
                imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        if (!menu.slotsVisible()) return;

        graphics.text(font, title, titleLabelX, titleLabelY, LABEL_COLOR, false);

        for (int i = 0; i < TEXT_ROWS.length; i++) {
            graphics.text(font, Component.literal(TEST_LINES[i]), 16, TEXT_ROWS[i], LABEL_COLOR, false);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (menu.state() == WritingDeskMenu.DeskState.WITH_TABLET) {
            double rx = event.x() - leftPos;
            double ry = event.y() - topPos;
            if (rx >= TABLET_X0 && rx < TABLET_X1 && ry >= TABLET_Y0 && ry < TABLET_Y1) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, WritingDeskMenu.BUTTON_INSCRIBE);
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }
}
