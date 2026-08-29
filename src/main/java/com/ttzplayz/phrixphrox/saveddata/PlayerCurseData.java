package com.ttzplayz.phrixphrox.saveddata;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mojang.datafixers.util.Pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ttzplayz.phrixphrox.PhrixPhrox;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class PlayerCurseData extends SavedData {

    Map<Long, CurseInstance> curses;

    public static final SavedDataType<PlayerCurseData> ID = new SavedDataType<PlayerCurseData>(
        Identifier.fromNamespaceAndPath(PhrixPhrox.MOD_ID, "player_curse_data"),
        PlayerCurseData::new,
        RecordCodecBuilder.create(instance -> instance.group(Codec.unboundedMap(
            Codec.LONG,
            CurseInstance.CODEC
        ).fieldOf("curses").forGetter(sd -> sd.curses)).apply(instance, PlayerCurseData::new))
    );

    public PlayerCurseData() {
    }

    public PlayerCurseData(Map<Long, CurseInstance> curses) {
        this.curses = curses;
    }

    public void newCurse(Long curse_id, Integer curse_type) {
        curses.put(curse_id, new CurseInstance(curse_type, List.of()));
        this.setDirty();
    }

    public void addPlayerToCurse(Long curse_id, UUID player_id) {
        curses.get(curse_id).targets.add(player_id.getLeastSignificantBits());
        this.setDirty();
    }
}
