package com.ttzplayz.phrixphrox.menu;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import com.ttzplayz.phrixphrox.client.CursedFlames;
import com.ttzplayz.phrixphrox.data.PPData;
import com.ttzplayz.phrixphrox.items.Defixion;
import com.ttzplayz.phrixphrox.items.PPItems;
import com.ttzplayz.phrixphrox.saveddata.CurseInstance;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class WritingDeskScreen extends AbstractContainerScreen<WritingDeskMenu> {
    private static final Identifier UNEQUIPPED = gui("writing_desk_unequipped.png");
    private static final Identifier NO_STYLUS = gui("writing_desk_tablet_no_stylus.png");
    private static final Identifier WITH_TABLET = gui("writing_desk_gui_with_tablet.png");
    private static final Identifier FINISHED = gui("writing_desk_gui_finished.png");

    private static final Identifier STYLUS = Identifier.fromNamespaceAndPath(PhrixPhrox.MOD_ID, "textures/item/cursed_stylus.png");
    private static final Identifier RUNE_TEMPLATE = Identifier.fromNamespaceAndPath(PhrixPhrox.MOD_ID, "textures/item/rune_template.png");
    private static final Identifier EMBLEM_GLOW = gui("sulis_icon_small_glowing.png");
    private static final Identifier TABLET_OUTLINE = gui("lead_tablet_outline.png");
    private static final Identifier SCROLL_BAR = gui("writing_desk_scroll_bar.png");

    private static final int TABLET_X0 = 73;
    private static final int TABLET_Y0 = 17;
    private static final int TABLET_X1 = 135;
    private static final int TABLET_Y1 = 98;

    private static final int RUNE_SIZE = 36;
    private static final int RUNE_X = TABLET_X0 + (TABLET_X1 - TABLET_X0 - RUNE_SIZE) / 2;
    private static final int RUNE_Y = TABLET_Y1 - 3 - RUNE_SIZE;

    private static final int INSCRIPTION_X = TABLET_X0 + 2;
    private static final int INSCRIPTION_Y = TABLET_Y0 + 2;
    private static final int INSCRIPTION_WIDTH = TABLET_X1 - TABLET_X0 - 4;
    private static final int INSCRIPTION_HEIGHT = RUNE_Y - 1 - INSCRIPTION_Y;
    private static final float INSCRIPTION_SCALE_MAX = 1.0f;
    private static final float SCALE_STEP = 0.01f;
    private static final float SCALE_FLOOR = 0.2f;

    private static final int READOUT_Y = TABLET_Y0 + 2;
    private static final int READOUT_HEIGHT = TABLET_Y1 - 2 - READOUT_Y;
    private static final float READOUT_SCALE_MAX = 0.6f;
    private static final int READOUT_COLOR = CursedFlames.CURSE_GREEN;
    private static final long READOUT_HOLD_NANOS = 1_000_000_000L;
    private static final long READOUT_FADE_NANOS = 400_000_000L;

    private static final int STYLUS_SIZE = 16;
    private static final int STYLUS_OFFSET_X = -1;
    private static final int STYLUS_OFFSET_Y = 0;

    private static final int LABEL_COLOR = -12566464;
    private static final int CARVED_COLOR = 0xFFFFFFFF;
    private static final int SELECTION_COLOR = 0x33000000;
    private static final int HOVER_COLOR = 0x1A000000;
    private static final int LOCKED_COLOR = 0x80202020;
    private static final int WANTED_COLOR = 0x5583E03B;

    private static final int BOX_X0 = 16;
    private static final int BOX_X1 = 58;
    private static final int BOX_Y0 = 49;
    private static final int BOX_PITCH = 11;
    private static final int BOX_HEIGHT = 11;
    private static final int ROW_TEXT_X = 17;
    private static final float ROW_SCALE = 0.4f;

    private static final int VISIBLE_ROWS = 4;

    private static final int FRAME_X = 13;
    private static final int FRAME_Y = BOX_Y0;
    private static final int FRAME_W = 50;
    private static final int FRAME_H = 48;

    private static final int TRACK_X0 = 58;
    private static final int TRACK_X1 = 62;
    private static final int TRACK_Y0 = BOX_Y0;
    private static final int TRACK_Y1 = BOX_Y0 + VISIBLE_ROWS * BOX_PITCH;
    private static final int THUMB_MIN_HEIGHT = 6;
    private static final int THUMB_COLOR = 0xAA827866;

    private static final float EMBLEM_CX = 37.5f;
    private static final float EMBLEM_CY = 27.5f;
    private static final float EMBLEM_R = 17f;
    private static final int GLOW_SIZE = 48;
    private static final int GLOW_X = 14;
    private static final int GLOW_Y = 3;

    private static final int OUTLINE_SIZE = 16;

    private static final long OUTPUT_DELAY_NANOS = 700_000_000L;

    private static final int INSCRIPTION_LINES = 5;
    private static final int NAMED_LINE = 2;

    private record CurseOption(CurseInstance.Curse curse) {
        Component name() {
            return Component.translatable(curse.nameKey());
        }
    }

    private record FittedText(float scale, List<String> lines, int chars) {}

    private static final List<CurseOption> CURSES = List.of(
            new CurseOption(CurseInstance.Curse.SeveredThreads),
            new CurseOption(CurseInstance.Curse.HollowVoice),
            new CurseOption(CurseInstance.Curse.BlunderStrike),
            new CurseOption(CurseInstance.Curse.SunBurning),
            new CurseOption(CurseInstance.Curse.EternalWake),
            new CurseOption(CurseInstance.Curse.Maiden));

    private static Identifier gui(String name) {
        return Identifier.fromNamespaceAndPath(PhrixPhrox.MOD_ID, "textures/gui/writing_desk/" + name);
    }

    private final RuneCarving carving = new RuneCarving(TABLET_X0, TABLET_Y0, TABLET_X1, TABLET_Y1, RUNE_X, RUNE_Y, RUNE_SIZE);

    private @Nullable CurseOption selected;
    private int scrollRow;
    private boolean draggingThumb;
    private boolean reagentReady;
    private boolean resultSent;
    private boolean cursorHidden;
    private long completedAtNanos;
    private long finishedAtNanos;
    private List<CurseOption> visible = CURSES;
    private final Map<CurseInstance.Curse, Identifier> runes = new EnumMap<>(CurseInstance.Curse.class);

    private @Nullable List<Component> cachedSource;
    private @Nullable FittedText cachedFit;
    private int cachedBoxW;
    private int cachedBoxH;
    private float cachedMaxScale;
    private float cachedFixedScale;
    private float inscriptionScale;

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
            case WITH_TABLET, INSCRIBED -> WITH_TABLET;
            case FINISHED -> FINISHED;
        };
    }

    private boolean selectable() {
        return menu.state() == WritingDeskMenu.DeskState.WITH_TABLET;
    }

    private boolean carvable() {
        return selectable() && selected != null && reagentReady;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        graphics.blit(RenderPipelines.GUI_TEXTURED, background(), leftPos, topPos, 0, 0,
                imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        int localX = xm - leftPos;
        int localY = ym - topPos;

        syncSelection();

        boolean onTablet = carvable() && carving.inTablet(localX, localY);
        setCursorHidden(onTablet);

        if (menu.state() == WritingDeskMenu.DeskState.UNEQUIPPED) extractTabletPrompt(graphics);

        if (!menu.slotsVisible()) return;

        graphics.text(font, title, titleLabelX, titleLabelY, LABEL_COLOR, false);

        extractCurseList(graphics, localX, localY, xm, ym);

        switch (menu.state()) {
            case WITH_TABLET -> {
                if (selected != null) {
                    extractInscription(graphics, inscriptionLines(selected.curse(), targetName()),
                            reagentReady ? carving.progress() : 0f);
                    if (reagentReady) {
                        carving.extract(graphics);
                    } else {
                        extractReagentPrompt(graphics, localX, localY, xm, ym);
                    }
                }
            }
            case INSCRIBED -> extractInscribedDefixion(graphics, localX, localY, xm, ym);
            case FINISHED -> extractReadout(graphics);
            default -> { }
        }

        if (onTablet) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, STYLUS,
                    localX + STYLUS_OFFSET_X, localY + STYLUS_OFFSET_Y, 0, 0,
                    STYLUS_SIZE, STYLUS_SIZE, STYLUS_SIZE, STYLUS_SIZE);
        }

        deliverResult();
    }

    private void syncSelection() {
        visible = computeVisible();
        trackFinished();

        if (selected != null && !visible.contains(selected)) forgetSelection();

        if (!selectable()) {
            forgetSelection();
            reagentReady = false;
            return;
        }

        boolean ready = selected != null && reagentSatisfied(selected.curse());
        if (ready == reagentReady) return;

        reagentReady = ready;
        completedAtNanos = 0L;
        resultSent = false;
        if (ready) {
            carving.begin(rune(selected.curse()));
        } else {
            carving.reset();
        }
    }

    private void forgetSelection() {
        if (selected == null) return;

        selected = null;
        resultSent = false;
        completedAtNanos = 0L;
        carving.reset();
    }

    private List<CurseOption> computeVisible() {
        List<CurseOption> shown = new ArrayList<>(CURSES.size());
        for (CurseOption option : CURSES) {
            if (option.curse().secret() && !holdsReagent(option.curse())) continue;
            shown.add(option);
        }
        return shown;
    }

    private boolean holdsReagent(CurseInstance.Curse curse) {
        Item reagent = curse.reagent();
        if (reagent == null) return false;
        if (menu.focusStack().is(reagent)) return true;
        return minecraft != null && minecraft.player != null
                && minecraft.player.getInventory().contains(stack -> stack.is(reagent));
    }

    private Identifier rune(CurseInstance.Curse curse) {
        return runes.computeIfAbsent(curse, WritingDeskScreen::resolveRune);
    }

    private static Identifier resolveRune(CurseInstance.Curse curse) {
        Identifier voxMagica = Identifier.fromNamespaceAndPath(PhrixPhrox.MOD_ID,
                "textures/item/" + curse.path() + "_vm.png");
        return Minecraft.getInstance().getResourceManager().getResource(voxMagica).isPresent()
                ? voxMagica
                : RUNE_TEMPLATE;
    }

    private void trackFinished() {
        if (menu.state() == WritingDeskMenu.DeskState.FINISHED) {
            if (finishedAtNanos == 0L) finishedAtNanos = System.nanoTime();
        } else {
            finishedAtNanos = 0L;
        }
    }

    private boolean reagentSatisfied(CurseInstance.Curse curse) {
        Item reagent = curse.reagent();
        return reagent != null && menu.focusStack().is(reagent);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractContents(graphics, mouseX, mouseY, a);
        if (minecraft == null || minecraft.player == null) return;

        Predicate<ItemStack> wanted = wantedItem();
        int litSlot = litSlot();

        graphics.pose().pushMatrix();
        graphics.pose().translate(leftPos, topPos);
        for (int i = 0; i < menu.slots.size(); i++) {
            Slot slot = menu.slots.get(i);
            if (!slot.isActive()) continue;
            if (dimmed(i, slot, wanted, litSlot)) {
                graphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, LOCKED_COLOR);
            } else if (wanted != null && i != litSlot && wanted.test(slot.getItem())) {
                graphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, WANTED_COLOR);
            }
        }
        graphics.pose().popMatrix();
    }

    private boolean dimmed(int index, Slot slot, @Nullable Predicate<ItemStack> wanted, int litSlot) {
        ItemStack stack = slot.getItem();

        if (!stack.isEmpty() && minecraft != null && minecraft.player != null
                && !slot.mayPickup(minecraft.player)) {
            return true;
        }
        if (wanted == null) return false;
        return index != litSlot && !wanted.test(stack);
    }

    private @Nullable Predicate<ItemStack> wantedItem() {
        if (selectable() && selected != null && !reagentReady) {
            Item reagent = selected.curse().reagent();
            return reagent == null ? stack -> false : stack -> stack.is(reagent);
        }
        if (menu.state() == WritingDeskMenu.DeskState.INSCRIBED
                && menu.tabletStack().get(PPData.TARGET_ID) == null) {
            return WritingDeskMenu::isFocusable;
        }
        if (menu.state() == WritingDeskMenu.DeskState.UNEQUIPPED) {
            return stack -> stack.is(PPItems.LEAD_TABLET) || stack.is(PPItems.DEFIXION);
        }
        return null;
    }

    private int litSlot() {
        return menu.state() == WritingDeskMenu.DeskState.UNEQUIPPED
                ? WritingDeskMenu.SLOT_INDEX_TABLET
                : WritingDeskMenu.SLOT_INDEX_FOCUS;
    }

    private void extractCurseList(GuiGraphicsExtractor graphics, int localX, int localY, int screenX, int screenY) {
        int wrapWidth = Math.round((BOX_X1 - 2 - ROW_TEXT_X) / ROW_SCALE);
        clampScroll();

        for (int row = 0; row < VISIBLE_ROWS; row++) {
            int index = scrollRow + row;
            if (index >= visible.size()) break;

            CurseOption option = visible.get(index);
            int y0 = BOX_Y0 + row * BOX_PITCH;
            boolean hovered = selectable() && inBox(localX, localY, y0);

            if (option == selected) {
                graphics.fill(BOX_X0, y0, BOX_X1, y0 + BOX_HEIGHT, SELECTION_COLOR);
            } else if (hovered) {
                graphics.fill(BOX_X0, y0, BOX_X1, y0 + BOX_HEIGHT, HOVER_COLOR);
            }

            List<FormattedCharSequence> wrapped = font.split(option.name(), wrapWidth);
            float blockHeight = wrapped.size() * font.lineHeight * ROW_SCALE;

            graphics.pose().pushMatrix();
            graphics.pose().translate(ROW_TEXT_X, y0 + (BOX_HEIGHT - blockHeight) / 2f);
            graphics.pose().scale(ROW_SCALE, ROW_SCALE);
            int color = option == selected ? CARVED_COLOR : LABEL_COLOR;
            for (int line = 0; line < wrapped.size(); line++) {
                graphics.text(font, wrapped.get(line), 0, line * font.lineHeight, color, false);
            }
            graphics.pose().popMatrix();

            if (hovered) {
                graphics.setTooltipForNextFrame(font, option.name(), screenX, screenY);
            }
        }

        extractScrollThumb(graphics);

        graphics.blit(RenderPipelines.GUI_TEXTURED, SCROLL_BAR, FRAME_X, FRAME_Y, 0, 0,
                FRAME_W, FRAME_H, FRAME_W, FRAME_H);
    }

    private void extractScrollThumb(GuiGraphicsExtractor graphics) {
        if (maxScroll() == 0) return;

        int thumbHeight = thumbHeight();
        int y0 = TRACK_Y0 + Math.round(thumbTravel() * scrollRow / (float) maxScroll());
        graphics.fill(TRACK_X0, y0, TRACK_X1, y0 + thumbHeight, THUMB_COLOR);
    }

    private int maxScroll() {
        return Math.max(0, visible.size() - VISIBLE_ROWS);
    }

    private int thumbHeight() {
        int track = TRACK_Y1 - TRACK_Y0;
        return Math.max(THUMB_MIN_HEIGHT, track * VISIBLE_ROWS / visible.size());
    }

    private int thumbTravel() {
        return TRACK_Y1 - TRACK_Y0 - thumbHeight();
    }

    private void clampScroll() {
        scrollRow = Mth.clamp(scrollRow, 0, maxScroll());
    }

    private void scrollTo(int row) {
        int clamped = Mth.clamp(row, 0, maxScroll());
        if (clamped != scrollRow) scrollRow = clamped;
    }

    private void scrollToThumb(double localY) {
        int travel = thumbTravel();
        if (travel <= 0) return;

        double fraction = (localY - TRACK_Y0 - thumbHeight() / 2.0) / travel;
        scrollTo((int) Math.round(fraction * maxScroll()));
    }

    private static boolean inList(double x, double y) {
        return x >= BOX_X0 && x < TRACK_X1 && y >= BOX_Y0 && y < TRACK_Y1;
    }

    private static boolean inTrack(double x, double y) {
        return x >= TRACK_X0 && x < TRACK_X1 && y >= TRACK_Y0 && y < TRACK_Y1;
    }

    private static boolean inBox(double x, double y, int boxY) {
        return x >= BOX_X0 && x < BOX_X1 && y >= boxY && y < boxY + BOX_HEIGHT;
    }

    private void extractReagentPrompt(GuiGraphicsExtractor graphics, int localX, int localY, int screenX, int screenY) {
        if (selected == null) return;
        Item reagent = selected.curse().reagent();
        if (reagent == null) return;

        graphics.blit(RenderPipelines.GUI_TEXTURED, EMBLEM_GLOW, GLOW_X, GLOW_Y, 0, 0,
                GLOW_SIZE, GLOW_SIZE, GLOW_SIZE, GLOW_SIZE, pulseTint());

        float dx = localX + 0.5f - EMBLEM_CX;
        float dy = localY + 0.5f - EMBLEM_CY;
        if (dx * dx + dy * dy <= EMBLEM_R * EMBLEM_R) {
            graphics.setTooltipForNextFrame(font, Component.translatable("tooltip.phrixphrox.needs",
                    Component.translatable(reagent.getDescriptionId())), screenX, screenY);
        }
    }

    private static int pulseTint() {
        float pulse = (float) (0.5 + 0.5 * Math.sin(System.nanoTime() / 4.0e8));
        return ((0x60 + Math.round(0x9F * pulse)) << 24) | 0xFFFFFF;
    }

    private void extractTabletPrompt(GuiGraphicsExtractor graphics) {
        if (minecraft == null || minecraft.player == null) return;
        if (!minecraft.player.getInventory().contains(
                stack -> stack.is(PPItems.LEAD_TABLET) || stack.is(PPItems.DEFIXION))) {
            return;
        }
        graphics.blit(RenderPipelines.GUI_TEXTURED, TABLET_OUTLINE,
                WritingDeskMenu.TABLET_SLOT_X, WritingDeskMenu.TABLET_SLOT_Y, 0, 0,
                OUTLINE_SIZE, OUTLINE_SIZE, OUTLINE_SIZE, OUTLINE_SIZE, pulseTint());
    }

    private void extractInscription(GuiGraphicsExtractor graphics, List<Component> source, float progress) {
        FittedText fit = fitted(source, INSCRIPTION_WIDTH, INSCRIPTION_HEIGHT, INSCRIPTION_SCALE_MAX, inscriptionScale());
        int carvedChars = Math.round(progress * fit.chars());
        drawFitted(graphics, fit, INSCRIPTION_X + INSCRIPTION_WIDTH / 2f, INSCRIPTION_Y, INSCRIPTION_HEIGHT, carvedChars);
    }

    private void extractInscribedDefixion(GuiGraphicsExtractor graphics, int localX, int localY,
                                          int screenX, int screenY) {
        ItemStack defixion = menu.tabletStack();
        Integer curseType = defixion.get(PPData.CURSE_TYPE);
        if (curseType == null) return;

        CurseInstance.Curse curse = CurseInstance.Curse.byOrdinal(curseType);
        if (curse == null) return;

        extractInscription(graphics, inscriptionLines(curse, defixion.get(PPData.TARGET_NAME)), 1f);
        carving.extractFinished(graphics, rune(curse));

        if (carving.inTablet(localX, localY)) {
            List<FormattedCharSequence> tooltip = new ArrayList<>();
            Defixion.appendCurseTooltip(defixion, line -> tooltip.add(line.getVisualOrderText()));
            graphics.setTooltipForNextFrame(font, tooltip, screenX, screenY);
        }
    }

    private void extractReadout(GuiGraphicsExtractor graphics) {
        int alpha = readoutAlpha();
        if (alpha == 0) return;

        ItemStack output = menu.outputStack();
        Integer curseType = output.get(PPData.CURSE_TYPE);
        if (curseType == null) return;

        CurseInstance.Curse curse = CurseInstance.Curse.byOrdinal(curseType);
        if (curse == null) return;

        String target = output.get(PPData.TARGET_NAME);
        List<Component> source = List.of(
                Component.translatable("tooltip.phrixphrox.inscribed_curse", Component.translatable(curse.nameKey()))
                        .withStyle(ChatFormatting.BOLD),
                Component.translatable("tooltip.phrixphrox.target", target != null
                        ? Component.literal(target)
                        : Component.translatable("tooltip.phrixphrox.target.unbound"))
                        .withStyle(ChatFormatting.BOLD));

        FittedText fit = fitted(source, INSCRIPTION_WIDTH, READOUT_HEIGHT, READOUT_SCALE_MAX, 0f);
        drawReadout(graphics, fit, INSCRIPTION_X + INSCRIPTION_WIDTH / 2f, READOUT_Y, READOUT_HEIGHT,
                ARGB.color(alpha, READOUT_COLOR));
    }

    private int readoutAlpha() {
        if (finishedAtNanos == 0L) return 0;

        long shown = System.nanoTime() - finishedAtNanos;
        if (shown <= READOUT_HOLD_NANOS) return 255;

        long faded = shown - READOUT_HOLD_NANOS;
        if (faded >= READOUT_FADE_NANOS) return 0;
        return Math.round(255f * (1f - faded / (float) READOUT_FADE_NANOS));
    }

    private void drawReadout(GuiGraphicsExtractor graphics, FittedText fit, float centerX, int boxY,
                             int boxH, int color) {
        float blockHeight = fit.lines().size() * font.lineHeight * fit.scale();
        float startY = boxY + (boxH - blockHeight) / 2f;

        graphics.pose().pushMatrix();
        graphics.pose().translate(centerX, startY);
        graphics.pose().scale(fit.scale(), fit.scale());

        for (int i = 0; i < fit.lines().size(); i++) {
            Component line = Component.literal(fit.lines().get(i)).withStyle(ChatFormatting.BOLD);
            graphics.text(font, line, -font.width(line) / 2, i * font.lineHeight, color, false);
        }

        graphics.pose().popMatrix();
    }

    private void drawFitted(GuiGraphicsExtractor graphics, FittedText fit, float centerX, int boxY, int boxH, int carvedChars) {
        float blockHeight = fit.lines().size() * font.lineHeight * fit.scale();
        float startY = boxY + (boxH - blockHeight) / 2f;

        graphics.pose().pushMatrix();
        graphics.pose().translate(centerX, startY);
        graphics.pose().scale(fit.scale(), fit.scale());

        int consumed = 0;
        for (int i = 0; i < fit.lines().size(); i++) {
            String line = fit.lines().get(i);
            int y = i * font.lineHeight;
            int x = -font.width(line) / 2;

            int carved = Mth.clamp(carvedChars - consumed, 0, line.length());
            if (carved > 0) {
                String head = line.substring(0, carved);
                graphics.text(font, head, x, y, CARVED_COLOR, false);
                x += font.width(head);
            }
            if (carved < line.length()) {
                graphics.text(font, line.substring(carved), x, y, LABEL_COLOR, false);
            }
            consumed += line.length();
        }

        graphics.pose().popMatrix();
    }

    private List<Component> inscriptionLines(CurseInstance.Curse curse, @Nullable String target) {
        List<Component> lines = new ArrayList<>(INSCRIPTION_LINES);
        for (int i = 1; i <= INSCRIPTION_LINES; i++) {
            if (i == NAMED_LINE && target != null) {
                lines.add(Component.translatable(curse.inscriptionKey(i) + ".named", target));
            } else {
                lines.add(Component.translatable(curse.inscriptionKey(i)));
            }
        }
        return lines;
    }

    private @Nullable String targetName() {
        String fromOutput = menu.outputStack().get(PPData.TARGET_NAME);
        if (fromOutput != null) return fromOutput;

        ItemStack focus = menu.focusStack();
        if (focus.isEmpty()) return null;
        ResolvableProfile profile = focus.get(DataComponents.PROFILE);
        return profile == null ? null : profile.name().orElse(null);
    }

    private float inscriptionScale() {
        if (inscriptionScale <= 0f) {
            inscriptionScale = shrinkToFit(inscriptionLines(CurseInstance.Curse.SeveredThreads, null),
                    INSCRIPTION_WIDTH, INSCRIPTION_HEIGHT, INSCRIPTION_SCALE_MAX).scale();
        }
        return inscriptionScale;
    }

    private FittedText fitted(List<Component> source, int boxW, int boxH, float maxScale, float fixedScale) {
        if (cachedFit != null && boxW == cachedBoxW && boxH == cachedBoxH
                && maxScale == cachedMaxScale && fixedScale == cachedFixedScale
                && source.equals(cachedSource)) {
            return cachedFit;
        }

        FittedText result = fixedScale > 0f
                ? wrapAt(source, boxW, fixedScale)
                : shrinkToFit(source, boxW, boxH, maxScale);

        cachedSource = source;
        cachedFit = result;
        cachedBoxW = boxW;
        cachedBoxH = boxH;
        cachedMaxScale = maxScale;
        cachedFixedScale = fixedScale;
        return result;
    }

    private FittedText shrinkToFit(List<Component> source, int boxW, int boxH, float maxScale) {
        FittedText result = null;
        for (float s = maxScale; s >= SCALE_FLOOR; s -= SCALE_STEP) {
            result = wrapAt(source, boxW, s);
            if (result.lines().size() * font.lineHeight * s <= boxH) break;
        }
        return result;
    }

    private FittedText wrapAt(List<Component> source, int boxW, float scale) {
        List<String> wrapped = wrap(source, Math.max(1, Math.round(boxW / scale)));
        return new FittedText(scale, wrapped, wrapped.stream().mapToInt(String::length).sum());
    }

    private List<String> wrap(List<Component> source, int width) {
        List<String> out = new ArrayList<>();
        for (Component line : source) {
            for (FormattedText part : font.splitIgnoringLanguage(line, width)) {
                out.add(part.getString());
            }
        }
        return out;
    }

    private void deliverResult() {
        if (resultSent || !carvable() || minecraft == null || minecraft.gameMode == null) return;

        int button = pendingButton();
        if (button < 0) return;

        resultSent = true;
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, button);
    }

    private int pendingButton() {
        return switch (carving.status()) {
            case COMPLETE -> {
                if (completedAtNanos == 0L) {
                    completedAtNanos = System.nanoTime();
                    yield -1;
                }
                yield System.nanoTime() - completedAtNanos < OUTPUT_DELAY_NANOS
                        ? -1
                        : WritingDeskMenu.BUTTON_INSCRIBE_BASE + selected.curse().ordinal();
            }
            case RUINED -> WritingDeskMenu.BUTTON_RUIN;
            default -> -1;
        };
    }

    private void sendButton(int button) {
        if (minecraft == null || minecraft.gameMode == null) return;
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, button);
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    private void setCursorHidden(boolean hidden) {
        if (hidden == cursorHidden) return;
        long window = GLFW.glfwGetCurrentContext();
        if (window == 0L) return;
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, hidden ? GLFW.GLFW_CURSOR_HIDDEN : GLFW.GLFW_CURSOR_NORMAL);
        cursorHidden = hidden;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double rx = event.x() - leftPos;
        double ry = event.y() - topPos;

        if (menu.slotsVisible() && inTrack(rx, ry)) {
            draggingThumb = true;
            scrollToThumb(ry);
            return true;
        }

        if (selectable()) {
            for (int row = 0; row < VISIBLE_ROWS; row++) {
                int index = scrollRow + row;
                if (index < visible.size() && inBox(rx, ry, BOX_Y0 + row * BOX_PITCH)) {
                    selectCurse(visible.get(index));
                    return true;
                }
            }
        }

        if (carving.inTablet(rx, ry)) {
            if (event.hasShiftDown() && menu.retrievable()) {
                sendButton(WritingDeskMenu.BUTTON_RETRIEVE);
                return true;
            }
            if (menu.bindable()) {
                sendButton(WritingDeskMenu.BUTTON_BIND);
                return true;
            }
            if (carvable()) {
                carving.stroke(rx, ry);
                return true;
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        if (menu.slotsVisible() && inList(x - leftPos, y - topPos) && scrollY != 0) {
            scrollTo(scrollRow - (int) Math.signum(scrollY));
            return true;
        }
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (draggingThumb) {
            scrollToThumb(event.y() - topPos);
            return true;
        }
        if (carvable() && carving.active()) {
            double rx = event.x() - leftPos;
            double ry = event.y() - topPos;
            if (carving.inTablet(rx, ry)) {
                carving.stroke(rx, ry);
            } else {
                carving.endStroke();
            }
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        draggingThumb = false;
        carving.endStroke();
        return super.mouseReleased(event);
    }

    private void selectCurse(CurseOption option) {
        if (option == selected) return;
        selected = option;
        reagentReady = false;
        resultSent = false;
        completedAtNanos = 0L;
        carving.reset();
        if (minecraft != null) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    @Override
    public void removed() {
        setCursorHidden(false);
        super.removed();
    }
}
