package com.ttzplayz.phrixphrox.curse;

import com.ttzplayz.phrixphrox.data.PPData;
import com.ttzplayz.phrixphrox.saveddata.PlayerCurseData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class CurseTargeting {

    private static final int SCAN_INTERVAL = 20;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.tickCount % SCAN_INTERVAL != 0) return;

        PlayerCurseData data = null;

        for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
            if (stack.isEmpty()) continue;
            Long curseId = stack.get(PPData.CURSED_ITEM_ID);
            if (curseId == null) continue;

            if (data == null) data = PlayerCurseData.get(player.level());
            data.addPlayerToCurse(curseId, player.getUUID(), player.getGameProfile().name());
        }
    }
}
