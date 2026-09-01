package com.ttzplayz.phrixphrox.items;

import com.ttzplayz.phrixphrox.data.PPData;
import com.ttzplayz.phrixphrox.saveddata.CurseInstance;
import com.ttzplayz.phrixphrox.saveddata.PlayerCurseData;

import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

public class Defixion extends Item {

    public Defixion(Properties properties) {
        super(properties);
    }

    public static void appendCurseTooltip(ItemStack stack, Consumer<Component> builder) {
        Integer curseType = stack.get(PPData.CURSE_TYPE);
        if (curseType == null) return;

        CurseInstance.Curse curse = CurseInstance.Curse.byOrdinal(curseType);
        if (curse == null) return;

        builder.accept(Component.translatable("tooltip.phrixphrox.inscribed_curse",
                Component.translatable(curse.nameKey())).withStyle(ChatFormatting.GRAY));

        String target = stack.get(PPData.TARGET_NAME);
        Component targetName = target != null
                ? Component.literal(target)
                : Component.translatable("tooltip.phrixphrox.target.unbound");
        builder.accept(Component.translatable("tooltip.phrixphrox.target", targetName)
                .withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
        appendCurseTooltip(itemStack, builder);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (hand == InteractionHand.OFF_HAND) {
            ItemStack defixion = player.getItemInHand(hand);
            ItemStack carried = player.getMainHandItem();

            if (carried.getCount() != 1) {
                return InteractionResult.FAIL;
            }

            Long defixion_id = defixion.get(PPData.DEFIXION_ID);
            Integer curse_type = defixion.get(PPData.CURSE_TYPE);
            if (defixion_id == null) {
                defixion_id = new Random().nextLong();
                if (curse_type != null && level instanceof ServerLevel serverLevel) {
                    PlayerCurseData.get(serverLevel).newCurse(defixion_id, curse_type);
                }
            }

            carried.set(PPData.CURSED_ITEM_ID, defixion_id);

            ItemStack bound_defixion = PPItems.BOUND_DEFIXION.toStack();
            bound_defixion.set(PPData.DEFIXION_ID, defixion_id);

            if (curse_type != null) {
                bound_defixion.set(PPData.CURSE_TYPE, curse_type);
            }

            UUID target_id = defixion.get(PPData.TARGET_ID);
            if (target_id != null) {
                bound_defixion.set(PPData.TARGET_ID, target_id);
            }

            String target_name = defixion.get(PPData.TARGET_NAME);
            if (target_name != null) {
                bound_defixion.set(PPData.TARGET_NAME, target_name);
            }

            player.setItemSlot(EquipmentSlot.OFFHAND, bound_defixion);
            player.setItemSlot(EquipmentSlot.MAINHAND, carried);
        }

        return InteractionResult.CONSUME;
    }
}
