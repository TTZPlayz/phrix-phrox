package com.ttzplayz.phrixphrox.data;

import com.ttzplayz.phrixphrox.PhrixPhrox;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class PPAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, PhrixPhrox.MOD_ID);

    public static final Supplier<AttachmentType<Boolean>> CURSED_FLAMES = ATTACHMENT_TYPES.register(
            "cursed_flames", () -> AttachmentType.<Boolean>builder(() -> false)
                    .sync(ByteBufCodecs.BOOL)
                    .build());

    public static boolean hasCursedFlames(Entity entity) {
        return entity.getData(CURSED_FLAMES);
    }

    public static void setCursedFlames(Entity entity, boolean burning) {
        if (hasCursedFlames(entity) != burning) {
            entity.setData(CURSED_FLAMES, burning);
        }
    }

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
