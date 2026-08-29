package com.ttzplayz.phrixphrox.saveddata;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class CurseInstance  {


    public enum Curse {
        // Minor
        HollowVoice,
        SeveredThreads,
        CrookedPath,
        WaningFlame,
        // Moderate
        UnmournedGrave,
        BlunderStrike,
        BlindVisions,
        EternalWake,
        // Deadly
        SuddenStorm,
        SunBurning,
        BoundServant,
        ShiftingSands,
        BetrayedSiren,
        ThousandKnives,
        // Maiden
        Maiden
    }

    Integer curse_num;

    List<Long> targets;

    public CurseInstance(Integer curse_num, List<Long> targets) {
        this.curse_num = curse_num;
        this.targets = targets;
    }

    public static final Codec<CurseInstance> CODEC =
    RecordCodecBuilder.create(inner_instance -> inner_instance.group(
        Codec.intRange(0, 15).fieldOf("curse_num").forGetter(curse -> curse.curse_num),
        Codec.list(Codec.LONG).fieldOf("targets").forGetter(curse -> curse.targets)
    ).apply(inner_instance, CurseInstance::new));
}
