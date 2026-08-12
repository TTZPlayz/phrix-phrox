package com.ttzplayz.phrixphrox.items;

import com.ttzplayz.phrixphrox.data.PPData;
import java.util.Random;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class Defixion extends Item {

    public Defixion(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        // We want the server to handle this, not the client
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (hand == InteractionHand.OFF_HAND) {
            ItemStack carried = player.getMainHandItem();

            if (carried.getCount() != 1) {
                return InteractionResult.FAIL;
            }

            Long defixion_id = new Random().nextLong();

            carried.set(PPData.CURSED_ITEM_ID, defixion_id);

            ItemStack bound_defixion = PPItems.BOUND_DEFIXION.toStack();
            bound_defixion.set(PPData.DEFIXION_ID, defixion_id);

            player.setItemSlot(EquipmentSlot.OFFHAND, bound_defixion);
            player.setItemSlot(EquipmentSlot.MAINHAND, carried);
        }

        return InteractionResult.CONSUME;
    }
}
