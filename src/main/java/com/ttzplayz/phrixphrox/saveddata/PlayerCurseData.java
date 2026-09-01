package com.ttzplayz.phrixphrox.saveddata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ttzplayz.phrixphrox.PhrixPhrox;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jspecify.annotations.Nullable;

public class PlayerCurseData extends SavedData {

    private final Map<Long, CurseInstance> curses;

    private static final Codec<Long> CURSE_ID = Codec.STRING.comapFlatMap(
        key -> {
            try {
                return DataResult.success(Long.parseLong(key));
            } catch (NumberFormatException e) {
                return DataResult.error(() -> "Not a curse id: " + key);
            }
        },
        String::valueOf
    );

    public static final SavedDataType<PlayerCurseData> ID = new SavedDataType<PlayerCurseData>(
        Identifier.fromNamespaceAndPath(PhrixPhrox.MOD_ID, "player_curse_data"),
        PlayerCurseData::new,
        RecordCodecBuilder.create(instance -> instance.group(Codec.unboundedMap(
            CURSE_ID,
            CurseInstance.CODEC
        ).fieldOf("curses").forGetter(sd -> sd.curses)).apply(instance, PlayerCurseData::new))
    );

    public PlayerCurseData() {
        this.curses = new HashMap<>();
    }

    public PlayerCurseData(Map<Long, CurseInstance> curses) {
        this.curses = new HashMap<>(curses);
    }

    public static PlayerCurseData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(ID);
    }

    public @Nullable CurseInstance curse(Long curse_id) {
        return curses.get(curse_id);
    }

    public void newCurse(Long curse_id, Integer curse_type) {
        curses.putIfAbsent(curse_id, new CurseInstance(curse_type, List.of()));
        this.setDirty();
    }

    public void addPlayerToCurse(Long curse_id, UUID player_id, String player_name) {
        CurseInstance curse = curses.get(curse_id);
        if (curse == null) return;
        if (curse.addTarget(player_id, player_name)) {
            this.setDirty();
        }
    }

    public void setCurser(Long curse_id, UUID player_id, String player_name) {
        CurseInstance curse = curses.get(curse_id);
        if (curse == null) return;
        curse.setCurser(player_id, player_name);
        this.setDirty();
    }

    public boolean activate(Long curse_id, long gameTime) {
        CurseInstance curse = curses.get(curse_id);
        if (curse == null || !curse.activate(gameTime)) return false;
        this.setDirty();
        return true;
    }

    public boolean neutralize(Long curse_id) {
        CurseInstance curse = curses.get(curse_id);
        if (curse == null || !curse.neutralize()) return false;
        this.setDirty();
        return true;
    }

    public boolean escalate(Long curse_id) {
        CurseInstance curse = curses.get(curse_id);
        if (curse == null || !curse.escalate()) return false;
        this.setDirty();
        return true;
    }

    public void forEachAffliction(UUID player_id, BiConsumer<Long, CurseInstance> action) {
        for (Map.Entry<Long, CurseInstance> entry : curses.entrySet()) {
            CurseInstance curse = entry.getValue();
            if (curse.applies() && curse.targets(player_id)) {
                action.accept(entry.getKey(), curse);
            }
        }
    }

    public void forEachCurseBy(UUID curser_id, BiConsumer<Long, CurseInstance> action) {
        for (Map.Entry<Long, CurseInstance> entry : curses.entrySet()) {
            CurseInstance curse = entry.getValue();
            CurseInstance.CurseTarget curser = curse.curser();
            if (curse.applies() && curser != null && curser.id().equals(curser_id)) {
                action.accept(entry.getKey(), curse);
            }
        }
    }
}
