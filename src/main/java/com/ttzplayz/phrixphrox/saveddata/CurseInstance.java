package com.ttzplayz.phrixphrox.saveddata;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.UUIDUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

public class CurseInstance {

    public enum Curse {
        HollowVoice("hollow_voice", Items.ECHO_SHARD),
        SeveredThreads("severed_threads", Items.STRING),
        CrookedPath("crooked_path"),
        WaningFlame("waning_flame"),
        UnmournedGrave("unmourned_grave"),
        BlunderStrike("blunder_strike", Items.IRON_AXE),
        BlindVisions("blind_visions"),
        EternalWake("eternal_wake"),
        SuddenStorm("sudden_storm"),
        SunBurning("sun_burning"),
        BoundServant("bound_servant"),
        ShiftingSands("shifting_sands"),
        BetrayedSiren("betrayed_siren"),
        ThousandKnives("thousand_knives"),
        Maiden("maiden");

        private final String path;

        private final @Nullable Item reagent;

        Curse(String path) {
            this(path, null);
        }

        Curse(String path, @Nullable Item reagent) {
            this.path = path;
            this.reagent = reagent;
        }

        public String path() {
            return path;
        }

        public @Nullable Item reagent() {
            return reagent;
        }

        public static boolean isReagent(ItemStack stack) {
            if (stack.isEmpty()) return false;
            for (Curse curse : values()) {
                if (curse.reagent != null && stack.is(curse.reagent)) return true;
            }
            return false;
        }

        public String nameKey() {
            return "gui.phrixphrox.writing_desk.curse." + path;
        }

        public String inscriptionKey(int line) {
            return "gui.phrixphrox.writing_desk.inscription." + path + "." + line;
        }

        public static Curse byOrdinal(int ordinal) {
            Curse[] all = values();
            return ordinal >= 0 && ordinal < all.length ? all[ordinal] : null;
        }
    }

    public record CurseTarget(UUID id, String name) {
        public static final Codec<CurseTarget> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("id").forGetter(CurseTarget::id),
                Codec.STRING.fieldOf("name").forGetter(CurseTarget::name)
            ).apply(instance, CurseTarget::new));
    }

    private final Integer curse_num;

    private final List<CurseTarget> targets;

    private @Nullable CurseTarget curser;

    private boolean active;

    private long activatedAt;

    private boolean escalated;

    private boolean neutralized;

    public CurseInstance(Integer curse_num, List<CurseTarget> targets) {
        this(curse_num, targets, Optional.empty(), false, 0L, false, false);
    }

    public CurseInstance(Integer curse_num, List<CurseTarget> targets, Optional<CurseTarget> curser,
                         boolean active, long activatedAt, boolean escalated, boolean neutralized) {
        this.curse_num = curse_num;
        this.targets = new ArrayList<>(targets);
        this.curser = curser.orElse(null);
        this.active = active;
        this.activatedAt = activatedAt;
        this.escalated = escalated;
        this.neutralized = neutralized;
    }

    public Integer curseNum() {
        return curse_num;
    }

    public Curse curse() {
        return Curse.byOrdinal(curse_num);
    }

    public List<CurseTarget> targets() {
        return targets;
    }

    public @Nullable CurseTarget curser() {
        return curser;
    }

    public void setCurser(UUID id, String name) {
        this.curser = new CurseTarget(id, name);
    }

    public boolean isActive() {
        return active;
    }

    public long activatedAt() {
        return activatedAt;
    }

    public boolean isEscalated() {
        return escalated;
    }

    public boolean isNeutralized() {
        return neutralized;
    }

    public boolean applies() {
        return active && !neutralized;
    }

    public boolean activate(long gameTime) {
        if (active || neutralized) return false;
        this.active = true;
        this.activatedAt = gameTime;
        return true;
    }

    public boolean neutralize() {
        if (neutralized) return false;
        this.neutralized = true;
        return true;
    }

    public boolean escalate() {
        if (escalated) return false;
        this.escalated = true;
        return true;
    }

    public boolean targets(UUID id) {
        for (CurseTarget target : targets) {
            if (target.id().equals(id)) return true;
        }
        return false;
    }

    public boolean addTarget(UUID id, String name) {
        for (CurseTarget target : targets) {
            if (target.id().equals(id)) return false;
        }
        targets.add(new CurseTarget(id, name));
        return true;
    }

    public static final Codec<CurseInstance> CODEC =
    RecordCodecBuilder.create(inner_instance -> inner_instance.group(
        Codec.intRange(0, 15).fieldOf("curse_num").forGetter(CurseInstance::curseNum),
        Codec.list(CurseTarget.CODEC).fieldOf("targets").forGetter(CurseInstance::targets),
        CurseTarget.CODEC.optionalFieldOf("curser").forGetter(c -> Optional.ofNullable(c.curser)),
        Codec.BOOL.optionalFieldOf("active", false).forGetter(CurseInstance::isActive),
        Codec.LONG.optionalFieldOf("activated_at", 0L).forGetter(CurseInstance::activatedAt),
        Codec.BOOL.optionalFieldOf("escalated", false).forGetter(CurseInstance::isEscalated),
        Codec.BOOL.optionalFieldOf("neutralized", false).forGetter(CurseInstance::isNeutralized)
    ).apply(inner_instance, CurseInstance::new));
}
