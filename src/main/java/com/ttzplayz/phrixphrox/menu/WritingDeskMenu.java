package com.ttzplayz.phrixphrox.menu;

import com.ttzplayz.phrixphrox.block.PPBlocks;
import com.ttzplayz.phrixphrox.block.entity.WritingDeskBlockEntity;
import com.ttzplayz.phrixphrox.items.PPItems;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class WritingDeskMenu extends AbstractContainerMenu {
    public enum DeskState { UNEQUIPPED, NO_STYLUS, WITH_TABLET, FINISHED }

    public static final int BUTTON_INSCRIBE = 0;

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

        addSlot(new ResourceHandlerSlot(handler, handler::set, WritingDeskBlockEntity.SLOT_TABLET, 0, 0) {
            @Override
            public boolean isActive() {
                return false;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(PPItems.LEAD_TABLET);
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
        if (!handler.getResource(WritingDeskBlockEntity.SLOT_OUTPUT).isEmpty()) return DeskState.FINISHED;
        if (handler.getResource(WritingDeskBlockEntity.SLOT_TABLET).isEmpty()) return DeskState.UNEQUIPPED;
        if (!holdingStylus()) return DeskState.NO_STYLUS;
        return DeskState.WITH_TABLET;
    }

    public boolean slotsVisible() {
        DeskState s = state();
        return s == DeskState.WITH_TABLET || s == DeskState.FINISHED;
    }

    private boolean holdingStylus() {
        return player.getMainHandItem().is(PPItems.CURSED_STYLUS)
                || player.getOffhandItem().is(PPItems.CURSED_STYLUS);
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId != BUTTON_INSCRIBE || state() != DeskState.WITH_TABLET) return false;

        handler.set(WritingDeskBlockEntity.SLOT_TABLET, ItemResource.EMPTY, 0);
        handler.set(WritingDeskBlockEntity.SLOT_OUTPUT, ItemResource.of(PPItems.DEFIXION.toStack()), 1);
        return true;
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

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 24 + l * 18, 110 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 24 + i * 18, 168));
        }
    }
}
