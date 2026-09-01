package com.ttzplayz.phrixphrox.menu;

import com.mojang.blaze3d.platform.NativeImage;
import com.ttzplayz.phrixphrox.Config;
import com.ttzplayz.phrixphrox.PhrixPhrox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class RuneCarving {

    public enum Status { IDLE, IN_PROGRESS, COMPLETE, RUINED }

    public static final float BRUSH_PX = 1.5f;
    public static final float RUIN_RATIO = 0.40f;
    public static final float TOLERANCE_PX = 1.5f;

    private static final int MAX_GRID = 48;

    private static final int MAX_FLECKS = 220;
    private static final float FLECK_GRAVITY = 72f;
    private static final float FLECK_CHANCE = 0.60f;

    private static final int COL_GUIDE = 0x55A0A0A0;
    private static final int COL_CARVE = 0xFFCACACA;
    private static final int COL_DONE = 0xFFC8EA3C;
    private static final int COL_FLECK = 0xFFBFBFBF;
    private static final int COL_STREAK = 0x1A1A1A;

    private static final int STREAK_ALPHA_MIN = 0x40;
    private static final int STREAK_ALPHA_MAX = 0xE0;

    private record Mask(int width, int height, boolean[][] cells, int total) {}

    private static final Map<Identifier, Mask> MASK_CACHE = new HashMap<>();
    private static final Random RNG = new Random();

    private static final class Fleck {
        float x, y, vx, vy, size, age, life;
    }

    private final int tabletX0, tabletY0, tabletX1, tabletY1;
    private final int runeX, runeY, runeSize;

    private Mask mask;
    private boolean[][] carved = new boolean[0][0];
    private int carvedCount;

    private final int strayCols, strayRows;
    private final float strayCell;
    private boolean[][] strayed;
    private boolean[][] forgiven;
    private int strayCount;

    private final List<Fleck> flecks = new ArrayList<>();

    private boolean strokeActive;
    private double prevX, prevY;
    private long prevFrameNanos;

    private Status status = Status.IDLE;

    public RuneCarving(int tabletX0, int tabletY0, int tabletX1, int tabletY1, int runeX, int runeY, int runeSize) {
        this.tabletX0 = tabletX0;
        this.tabletY0 = tabletY0;
        this.tabletX1 = tabletX1;
        this.tabletY1 = tabletY1;
        this.runeX = runeX;
        this.runeY = runeY;
        this.runeSize = runeSize;

        this.strayCell = Math.max(1f, runeSize / (float) MAX_GRID);
        this.strayCols = Math.max(1, (int) Math.ceil((tabletX1 - tabletX0) / strayCell));
        this.strayRows = Math.max(1, (int) Math.ceil((tabletY1 - tabletY0) / strayCell));
        this.strayed = new boolean[strayCols][strayRows];
        this.forgiven = new boolean[strayCols][strayRows];
    }

    public void begin(Identifier runeTexture) {
        this.mask = loadMask(runeTexture);
        this.carved = mask == null ? new boolean[0][0] : new boolean[mask.width()][mask.height()];
        this.carvedCount = 0;
        this.strayed = new boolean[strayCols][strayRows];
        this.strayCount = 0;
        this.flecks.clear();
        this.strokeActive = false;
        this.prevFrameNanos = 0L;
        this.status = mask == null || mask.total() == 0 ? Status.IDLE : Status.IN_PROGRESS;
        buildTolerance();
    }

    public void reset() {
        this.mask = null;
        this.carved = new boolean[0][0];
        this.carvedCount = 0;
        this.strayed = new boolean[strayCols][strayRows];
        this.forgiven = new boolean[strayCols][strayRows];
        this.strayCount = 0;
        this.flecks.clear();
        this.strokeActive = false;
        this.status = Status.IDLE;
    }

    private void buildTolerance() {
        this.forgiven = new boolean[strayCols][strayRows];
        if (mask == null) return;

        float cw = runeSize / (float) mask.width();
        float ch = runeSize / (float) mask.height();
        float reach = TOLERANCE_PX + Math.max(cw, ch);
        float reach2 = reach * reach;
        int span = (int) Math.ceil(reach / strayCell) + 1;

        for (int gx = 0; gx < mask.width(); gx++) {
            float sxc = runeX + (gx + 0.5f) * cw;
            for (int gy = 0; gy < mask.height(); gy++) {
                if (!mask.cells()[gx][gy]) continue;
                float syc = runeY + (gy + 0.5f) * ch;
                int csx = (int) ((sxc - tabletX0) / strayCell);
                int csy = (int) ((syc - tabletY0) / strayCell);
                for (int sx = csx - span; sx <= csx + span; sx++) {
                    if (sx < 0 || sx >= strayCols) continue;
                    float pxc = tabletX0 + (sx + 0.5f) * strayCell;
                    for (int sy = csy - span; sy <= csy + span; sy++) {
                        if (sy < 0 || sy >= strayRows) continue;
                        if (forgiven[sx][sy]) continue;
                        float pyc = tabletY0 + (sy + 0.5f) * strayCell;
                        float dx = pxc - sxc, dy = pyc - syc;
                        if (dx * dx + dy * dy <= reach2) forgiven[sx][sy] = true;
                    }
                }
            }
        }
    }

    private static float completeFraction() {
        return (float) Config.RUNE_COMPLETION.getAsDouble();
    }

    public Status status() {
        return status;
    }

    public boolean active() {
        return status == Status.IN_PROGRESS;
    }

    public float progress() {
        if (mask == null || mask.total() == 0) return 0f;
        if (status == Status.COMPLETE) return 1f;
        return Math.min(1f, carvedCount / (mask.total() * completeFraction()));
    }

    public float ruinProgress() {
        if (mask == null || mask.total() == 0) return 0f;
        return Math.min(1f, strayCount / (mask.total() * RUIN_RATIO));
    }

    public boolean inTablet(double x, double y) {
        return x >= tabletX0 && x < tabletX1 && y >= tabletY0 && y < tabletY1;
    }

    public void stroke(double x, double y) {
        if (!active()) return;
        if (strokeActive) {
            int steps = (int) Math.max(1, Math.hypot(x - prevX, y - prevY) / 1.0);
            for (int i = 1; i < steps; i++) {
                double t = i / (double) steps;
                paintAt(prevX + (x - prevX) * t, prevY + (y - prevY) * t);
            }
        }
        paintAt(x, y);
        strokeActive = true;
        prevX = x;
        prevY = y;
        evaluate();
    }

    public void endStroke() {
        strokeActive = false;
    }

    private void paintAt(double mx, double my) {
        if (!inTablet(mx, my)) return;

        float cw = runeSize / (float) mask.width();
        float ch = runeSize / (float) mask.height();
        float br2 = BRUSH_PX * BRUSH_PX;

        int rgx = (int) Math.ceil(BRUSH_PX / cw) + 1;
        int rgy = (int) Math.ceil(BRUSH_PX / ch) + 1;
        int cgx = (int) ((mx - runeX) / cw);
        int cgy = (int) ((my - runeY) / ch);

        for (int gx = cgx - rgx; gx <= cgx + rgx; gx++) {
            if (gx < 0 || gx >= mask.width()) continue;
            float sxc = runeX + (gx + 0.5f) * cw;
            for (int gy = cgy - rgy; gy <= cgy + rgy; gy++) {
                if (gy < 0 || gy >= mask.height()) continue;
                if (!mask.cells()[gx][gy] || carved[gx][gy]) continue;
                float syc = runeY + (gy + 0.5f) * ch;
                float dx = (float) (mx - sxc), dy = (float) (my - syc);
                if (dx * dx + dy * dy <= br2) {
                    carved[gx][gy] = true;
                    carvedCount++;
                    if (RNG.nextFloat() < FLECK_CHANCE) spawnFleck(sxc, syc);
                }
            }
        }

        int rsx = (int) Math.ceil(BRUSH_PX / strayCell) + 1;
        int csx = (int) ((mx - tabletX0) / strayCell);
        int csy = (int) ((my - tabletY0) / strayCell);

        for (int sx = csx - rsx; sx <= csx + rsx; sx++) {
            if (sx < 0 || sx >= strayCols) continue;
            float pxc = tabletX0 + (sx + 0.5f) * strayCell;
            for (int sy = csy - rsx; sy <= csy + rsx; sy++) {
                if (sy < 0 || sy >= strayRows) continue;
                if (strayed[sx][sy]) continue;
                float pyc = tabletY0 + (sy + 0.5f) * strayCell;
                float dx = pxc - (float) mx, dy = pyc - (float) my;
                if (dx * dx + dy * dy > br2) continue;
                if (forgiven[sx][sy]) continue;
                strayed[sx][sy] = true;
                strayCount++;
                if (RNG.nextFloat() < FLECK_CHANCE) spawnFleck(pxc, pyc);
            }
        }
    }

    private void evaluate() {
        if (status != Status.IN_PROGRESS) return;
        if (strayCount >= mask.total() * RUIN_RATIO) {
            status = Status.RUINED;
            strokeActive = false;
        } else if (carvedCount >= mask.total() * completeFraction()) {
            status = Status.COMPLETE;
            strokeActive = false;
        }
    }

    public void extract(GuiGraphicsExtractor graphics) {
        if (status == Status.IDLE || mask == null) return;

        long now = System.nanoTime();
        float dt = prevFrameNanos == 0L ? 0f : Math.min(0.1f, (now - prevFrameNanos) / 1_000_000_000f);
        prevFrameNanos = now;
        updateFlecks(dt);

        int streakAlpha = (int) (STREAK_ALPHA_MIN + (STREAK_ALPHA_MAX - STREAK_ALPHA_MIN) * ruinProgress());
        int streakColor = (COL_STREAK & 0x00FFFFFF) | (streakAlpha << 24);
        for (int sx = 0; sx < strayCols; sx++) {
            int x0 = tabletX0 + Math.round(sx * strayCell);
            int x1 = Math.min(tabletX1, tabletX0 + Math.round((sx + 1) * strayCell));
            for (int sy = 0; sy < strayRows; sy++) {
                if (!strayed[sx][sy]) continue;
                int y0 = tabletY0 + Math.round(sy * strayCell);
                int y1 = Math.min(tabletY1, tabletY0 + Math.round((sy + 1) * strayCell));
                graphics.fill(x0, y0, x1, y1, streakColor);
            }
        }

        boolean done = status == Status.COMPLETE;
        for (int gx = 0; gx < mask.width(); gx++) {
            int x0 = runeX + Math.round(gx * (float) runeSize / mask.width());
            int x1 = runeX + Math.round((gx + 1) * (float) runeSize / mask.width());
            for (int gy = 0; gy < mask.height(); gy++) {
                if (!mask.cells()[gx][gy]) continue;
                int y0 = runeY + Math.round(gy * (float) runeSize / mask.height());
                int y1 = runeY + Math.round((gy + 1) * (float) runeSize / mask.height());
                int color = carved[gx][gy] ? (done ? COL_DONE : COL_CARVE) : COL_GUIDE;
                graphics.fill(x0, y0, x1, y1, color);
            }
        }

        for (Fleck f : flecks) {
            float lifeT = Math.min(1f, f.age / f.life);
            int alpha = (int) (0xC0 * (1f - lifeT));
            if (alpha <= 0) continue;
            int hs = Math.max(1, Math.round(f.size));
            int ix = Math.round(f.x), iy = Math.round(f.y);
            graphics.fill(ix - hs, iy - hs, ix + hs, iy + hs, (COL_FLECK & 0x00FFFFFF) | (alpha << 24));
        }
    }

    public void extractFinished(GuiGraphicsExtractor graphics, Identifier runeTexture) {
        Mask finished = loadMask(runeTexture);
        if (finished == null) return;

        for (int gx = 0; gx < finished.width(); gx++) {
            int x0 = runeX + Math.round(gx * (float) runeSize / finished.width());
            int x1 = runeX + Math.round((gx + 1) * (float) runeSize / finished.width());
            for (int gy = 0; gy < finished.height(); gy++) {
                if (!finished.cells()[gx][gy]) continue;
                int y0 = runeY + Math.round(gy * (float) runeSize / finished.height());
                int y1 = runeY + Math.round((gy + 1) * (float) runeSize / finished.height());
                graphics.fill(x0, y0, x1, y1, COL_DONE);
            }
        }
    }

    private void spawnFleck(float x, float y) {
        if (flecks.size() >= MAX_FLECKS) return;
        Fleck f = new Fleck();
        f.x = x;
        f.y = y;
        float a = RNG.nextFloat() * (float) Math.PI * 2f;
        float spd = 8f + RNG.nextFloat() * 18f;
        f.vx = (float) Math.cos(a) * spd;
        f.vy = -12f - RNG.nextFloat() * 16f;
        f.size = 0.5f + RNG.nextFloat() * 0.7f;
        f.life = 0.9f + RNG.nextFloat() * 0.8f;
        flecks.add(f);
    }

    private void updateFlecks(float dt) {
        for (Iterator<Fleck> it = flecks.iterator(); it.hasNext(); ) {
            Fleck f = it.next();
            f.vy += FLECK_GRAVITY * dt;
            f.x += f.vx * dt;
            f.y += f.vy * dt;
            f.age += dt;
            if (f.age >= f.life) it.remove();
        }
    }

    private static Mask loadMask(Identifier texture) {
        Mask cached = MASK_CACHE.get(texture);
        if (cached != null) return cached;

        Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(texture);
        if (resource.isEmpty()) {
            PhrixPhrox.LOGGER.warn("Rune template missing: {}", texture);
            return null;
        }

        try (InputStream in = resource.get().open(); NativeImage image = NativeImage.read(in)) {
            int w = image.getWidth(), h = image.getHeight();
            float scale = Math.min(1f, (float) MAX_GRID / Math.max(w, h));
            int gridW = Math.max(1, Math.round(w * scale));
            int gridH = Math.max(1, Math.round(h * scale));

            boolean[][] cells = new boolean[gridW][gridH];
            int total = 0;
            for (int gx = 0; gx < gridW; gx++) {
                int sx = Math.min(w - 1, (int) ((gx + 0.5f) / gridW * w));
                for (int gy = 0; gy < gridH; gy++) {
                    int sy = Math.min(h - 1, (int) ((gy + 0.5f) / gridH * h));
                    boolean rune = ((image.getPixel(sx, sy) >>> 24) & 0xFF) > 32;
                    cells[gx][gy] = rune;
                    if (rune) total++;
                }
            }

            if (total == 0) {
                PhrixPhrox.LOGGER.warn("Rune template has no opaque pixels: {}", texture);
                return null;
            }

            Mask mask = new Mask(gridW, gridH, cells, total);
            MASK_CACHE.put(texture, mask);
            return mask;
        } catch (Exception e) {
            PhrixPhrox.LOGGER.warn("Rune template load failed: {}", texture, e);
            return null;
        }
    }
}
