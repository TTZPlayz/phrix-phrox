package com.ttzplayz.phrixphrox.menu;

import com.ttzplayz.phrixphrox.Config;
import com.ttzplayz.phrixphrox.block.PPBlocks;
import com.ttzplayz.phrixphrox.block.entity.WritingDeskBlockEntity;
import com.ttzplayz.phrixphrox.curse.hollow_voice.HollowVoiceCurse;
import com.ttzplayz.phrixphrox.data.PPData;
import com.ttzplayz.phrixphrox.items.PPItems;
import com.ttzplayz.phrixphrox.saveddata.CurseInstance;
import com.ttzplayz.phrixphrox.saveddata.PlayerCurseData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jspecify.annotations.Nullable;

import java.util.Random;
import java.util.UUID;

public class WritingDeskMenu extends AbstractContainerMenu {
    public enum DeskState { UNEQUIPPED, NO_STYLUS, WITH_TABLET, INSCRIBED, FINISHED }

    public static final int BUTTON_RUIN = 1;
    public static final int BUTTON_BIND = 2;
    public static final int BUTTON_RETRIEVE = 3;
    public static final int BUTTON_INSCRIBE_BASE = 10;

    public static final int TABLET_SLOT_X = 96;
    public static final int TABLET_SLOT_Y = 51;

    public static final int SLOT_INDEX_TABLET = 36;
    public static final int SLOT_INDEX_FOCUS = 37;

    public final WritingDeskBlockEntity blockEntity;
    private final Level level;
    private final Player player;
    private final ItemStacksResourceHandler handler;

    public WritingDeskMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    private WritingDeskMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        this(containerId, inv, blockEntity, ((WritingDeskBlockEntity) blockEntity).inventory);
    }

    public WritingDeskMenu(int containerId, Inventory inv, BlockEntity blockEntity, ItemStacksResourceHandler handler) {
        super(PPMenuTypes.WRITING_DESK_MENU.get(), containerId);
        this.blockEntity = (WritingDeskBlockEntity) blockEntity;
        this.level = inv.player.level();
        this.player = inv.player;
        this.handler = handler;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        addSlot(new ResourceHandlerSlot(handler, handler::set, WritingDeskBlockEntity.SLOT_TABLET,
                TABLET_SLOT_X, TABLET_SLOT_Y) {
            @Override
            public boolean isActive() {
                return tabletSlotVisible();
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(PPItems.LEAD_TABLET) || stack.is(PPItems.DEFIXION);
            }
        });

        addSlot(new ResourceHandlerSlot(handler, handler::set, WritingDeskBlockEntity.SLOT_FOCUS, 30, 19) {
            @Override
            public boolean isActive() {
                return slotsVisible();
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return isFocusable(stack) || CurseInstance.Curse.isReagent(stack);
            }
        });

        addSlot(new ResourceHandlerSlot(handler, handler::set, WritingDeskBlockEntity.SLOT_OUTPUT, 176, 50) {
            @Override
            public boolean isActive() {
                return slotsVisible();
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
    }

    public DeskState state() {
        ItemStack tablet = tabletStack();
        if (tablet.is(PPItems.DEFIXION)) return DeskState.INSCRIBED;
        if (!tablet.isEmpty()) return hasStylus() ? DeskState.WITH_TABLET : DeskState.NO_STYLUS;
        if (!handler.getResource(WritingDeskBlockEntity.SLOT_OUTPUT).isEmpty()) return DeskState.FINISHED;
        return DeskState.UNEQUIPPED;
    }

    public boolean slotsVisible() {
        DeskState s = state();
        return s == DeskState.WITH_TABLET || s == DeskState.INSCRIBED || s == DeskState.FINISHED;
    }

    public boolean tabletSlotVisible() {
        return switch (state()) {
            case UNEQUIPPED, FINISHED -> true;
            default -> false;
        };
    }

    public ItemStack tabletStack() {
        return handler.getResource(WritingDeskBlockEntity.SLOT_TABLET)
                .toStack(handler.getAmountAsInt(WritingDeskBlockEntity.SLOT_TABLET));
    }

    public ItemStack focusStack() {
        return handler.getResource(WritingDeskBlockEntity.SLOT_FOCUS)
                .toStack(handler.getAmountAsInt(WritingDeskBlockEntity.SLOT_FOCUS));
    }

    public ItemStack outputStack() {
        return handler.getResource(WritingDeskBlockEntity.SLOT_OUTPUT)
                .toStack(handler.getAmountAsInt(WritingDeskBlockEntity.SLOT_OUTPUT));
    }

    private boolean hasStylus() {
        if (player.getOffhandItem().is(PPItems.CURSED_STYLUS)) return true;
        for (int i = 0; i < Inventory.SELECTION_SIZE; i++) {
            if (player.getInventory().getItem(i).is(PPItems.CURSED_STYLUS)) return true;
        }
        return false;
    }

    public boolean bindable() {
        ItemStack defixion = tabletStack();
        return defixion.is(PPItems.DEFIXION)
                && defixion.get(PPData.CURSE_TYPE) != null
                && defixion.get(PPData.TARGET_ID) == null
                && isFocusable(focusStack());
    }

    public boolean retrievable() {
        return switch (state()) {
            case WITH_TABLET, NO_STYLUS, INSCRIBED -> !tabletStack().isEmpty();
            default -> false;
        };
    }

    private boolean bindTargetToInscribedDefixion(Player player) {
        ItemStack defixion = tabletStack();
        if (!defixion.is(PPItems.DEFIXION)) return false;
        if (defixion.get(PPData.CURSE_TYPE) == null) return false;
        if (defixion.get(PPData.TARGET_ID) != null) return false;

        NameAndId target = focusTarget();
        if (target == null) return false;
        UUID targetId = target.id();
        String targetName = target.name();

        defixion.set(PPData.TARGET_ID, targetId);
        defixion.set(PPData.TARGET_NAME, targetName);

        Long defixionId = defixion.get(PPData.DEFIXION_ID);
        if (defixionId != null && level instanceof ServerLevel serverLevel) {
            PlayerCurseData.get(serverLevel).addPlayerToCurse(defixionId, targetId, targetName);
        }

        handler.set(WritingDeskBlockEntity.SLOT_TABLET, ItemResource.EMPTY, 0);
        giveResult(player, defixion);
        playDeskSound(SoundEvents.AMETHYST_BLOCK_CHIME, 0.6F);
        return true;
    }

    private boolean retrieveTablet(Player player) {
        ItemStack tablet = tabletStack();
        if (tablet.isEmpty()) return false;

        handler.set(WritingDeskBlockEntity.SLOT_TABLET, ItemResource.EMPTY, 0);
        if (!player.getInventory().add(tablet)) {
            player.drop(tablet, false);
        }
        playDeskSound(SoundEvents.ITEM_PICKUP, 1.0F);
        return true;
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId == BUTTON_RETRIEVE) {
            return retrievable() && retrieveTablet(player);
        }

        if (HollowVoiceCurse.isMute(player) && buttonId != BUTTON_RUIN) {
            if (player instanceof ServerPlayer serverPlayer) HollowVoiceCurse.notifySilenced(serverPlayer);
            return false;
        }

        if (buttonId == BUTTON_BIND) {
            return state() == DeskState.INSCRIBED && bindTargetToInscribedDefixion(player);
        }

        if (state() != DeskState.WITH_TABLET) return false;

        if (buttonId == BUTTON_RUIN) {
            handler.set(WritingDeskBlockEntity.SLOT_TABLET, ItemResource.EMPTY, 0);
            giveResult(player, PPItems.LEAD_SCRAP.toStack());
            playDeskSound(SoundEvents.DECORATED_POT_SHATTER, 0.8F);
            return true;
        }

        int ordinal = buttonId - BUTTON_INSCRIBE_BASE;
        CurseInstance.Curse curse = CurseInstance.Curse.byOrdinal(ordinal);
        if (curse == null || !curse.selectable()) return false;
        if (curse.secret() && !holdsReagent(player, curse)) return false;

        long defixionId = new Random().nextLong();

        ItemStack defixion = PPItems.DEFIXION.toStack();
        defixion.set(PPData.CURSE_TYPE, ordinal);
        defixion.set(PPData.DEFIXION_ID, defixionId);

        NameAndId focusTarget = focusTarget();
        UUID targetId = focusTarget == null ? null : focusTarget.id();
        String targetName = focusTarget == null ? null : focusTarget.name();
        if (targetId != null && targetName != null) {
            defixion.set(PPData.TARGET_ID, targetId);
            defixion.set(PPData.TARGET_NAME, targetName);
        }

        if (level instanceof ServerLevel serverLevel) {
            PlayerCurseData data = PlayerCurseData.get(serverLevel);
            data.newCurse(defixionId, ordinal);
            data.setCurser(defixionId, player.getUUID(), player.getGameProfile().name());
            if (targetId != null && targetName != null) {
                data.addPlayerToCurse(defixionId, targetId, targetName);
            }
        }

        handler.set(WritingDeskBlockEntity.SLOT_TABLET, ItemResource.EMPTY, 0);
        giveResult(player, defixion);
        playDeskSound(SoundEvents.AMETHYST_BLOCK_CHIME, 0.6F);
        return true;
    }

    private void giveResult(Player player, ItemStack stack) {
        if (handler.getResource(WritingDeskBlockEntity.SLOT_OUTPUT).isEmpty()) {
            handler.set(WritingDeskBlockEntity.SLOT_OUTPUT, ItemResource.of(stack), stack.getCount());
            return;
        }
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    public boolean holdsReagent(Player player, CurseInstance.Curse curse) {
        Item reagent = curse.reagent();
        if (reagent == null) return false;
        return focusStack().is(reagent) || player.getInventory().contains(stack -> stack.is(reagent));
    }

    public static boolean isFocusable(ItemStack stack) {
        return stack.is(Items.PLAYER_HEAD) || (Config.EASY_CURSING.getAsBoolean() && stack.is(Items.NAME_TAG));
    }

    private @Nullable NameAndId focusTarget() {
        ItemStack focus = focusStack();
        if (focus.isEmpty()) return null;

        if (focus.is(Items.PLAYER_HEAD)) {
            ResolvableProfile profile = focus.get(DataComponents.PROFILE);
            if (profile == null) return null;
            UUID id = profile.partialProfile().id();
            String name = profile.name().orElse(null);
            return id == null || name == null ? null : new NameAndId(id, name);
        }

        if (!Config.EASY_CURSING.getAsBoolean() || !focus.is(Items.NAME_TAG)) return null;
        Component custom = focus.get(DataComponents.CUSTOM_NAME);
        return custom == null ? null : resolveByName(custom.getString().trim());
    }

    private @Nullable NameAndId resolveByName(String name) {
        if (name.isEmpty() || !(level instanceof ServerLevel serverLevel)) return null;

        ServerPlayer online = serverLevel.getServer().getPlayerList().getPlayerByName(name);
        if (online != null) return new NameAndId(online.getUUID(), online.getGameProfile().name());

        return serverLevel.getServer().services().nameToIdCache().get(name)
                .map(known -> new NameAndId(known.id(), known.name()))
                .orElse(null);
    }

    private record NameAndId(UUID id, String name) {}

    private void playDeskSound(SoundEvent sound, float pitch) {
        if (level.isClientSide()) return;
        level.playSound(null, blockEntity.getBlockPos(), sound, SoundSource.BLOCKS, 1.0F, pitch);
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int TE_INVENTORY_SLOT_COUNT = 3;

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, PPBlocks.WRITING_DESK.get());
    }

    private class PlayerSlot extends Slot {
        PlayerSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean mayPickup(Player player) {
            return !(getItem().is(PPItems.CURSED_STYLUS) && state() == DeskState.WITH_TABLET);
        }
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new PlayerSlot(playerInventory, l + i * 9 + 9, 24 + l * 18, 110 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new PlayerSlot(playerInventory, i, 24 + i * 18, 168));
        }
    }
}
