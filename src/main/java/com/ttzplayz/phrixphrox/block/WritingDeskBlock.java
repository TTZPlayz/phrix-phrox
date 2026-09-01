package com.ttzplayz.phrixphrox.block;

import com.mojang.serialization.MapCodec;

import com.ttzplayz.phrixphrox.block.entity.WritingDeskBlockEntity;
import com.ttzplayz.phrixphrox.curse.hollow_voice.HollowVoiceCurse;
import com.ttzplayz.phrixphrox.items.PPItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

public class WritingDeskBlock extends BaseEntityBlock {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final VoxelShape SHAPE = Shapes.or(Block.box(0, 0, 0, 16, 2, 16),
            Block.box(4, 2, 4, 12, 14, 12));
    public static final MapCodec<WritingDeskBlock> CODEC = simpleCodec(WritingDeskBlock::new);

    public WritingDeskBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }


    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new WritingDeskBlockEntity(worldPosition, blockState);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player,
                                       ItemStack toolStack, boolean willHarvest, FluidState fluid) {
        if(level.getBlockEntity(pos) instanceof WritingDeskBlockEntity pedestalBlockEntity) {
            pedestalBlockEntity.drops();
            level.updateNeighbourForOutputSignal(pos, this);
        }
        return super.onDestroyedByPlayer(state, level, pos, player, toolStack, willHarvest, fluid);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        if (HollowVoiceCurse.isMute(player)) {
            if (player instanceof ServerPlayer serverPlayer) HollowVoiceCurse.notifySilenced(serverPlayer);
            return InteractionResult.SUCCESS;
        }

        if (level.getBlockEntity(pos) instanceof WritingDeskBlockEntity writingDeskBlockEntity) {
            boolean isWritingDeskEmpty = writingDeskBlockEntity.inventory
                    .getResource(WritingDeskBlockEntity.SLOT_TABLET).isEmpty();
            ItemStack main = player.getMainHandItem();
            ItemStack off = player.getOffhandItem();

            if(isWritingDeskEmpty && main.is(PPItems.CURSED_STYLUS) && off.is(PPItems.LEAD_TABLET)) {
                writingDeskBlockEntity.inventory.set(WritingDeskBlockEntity.SLOT_TABLET, ItemResource.of(off), 1);
                off.shrink(1);
                level.playSound(player, pos, SoundEvents.ANCIENT_DEBRIS_PLACE, SoundSource.BLOCKS, 1f, 2f);
                player.openMenu(writingDeskBlockEntity, buf -> buf.writeBlockPos(pos));
                return InteractionResult.SUCCESS;
            }

            if(isWritingDeskEmpty && itemStack.is(PPItems.LEAD_TABLET)) {
                writingDeskBlockEntity.inventory.set(WritingDeskBlockEntity.SLOT_TABLET, ItemResource.of(itemStack), 1);
                itemStack.shrink(1);
                level.playSound(player, pos, SoundEvents.ANCIENT_DEBRIS_PLACE, SoundSource.BLOCKS, 1f, 2f);
            }
            else if(!isWritingDeskEmpty && player.isShiftKeyDown()) {
                ItemStack stack = writingDeskBlockEntity.inventory
                        .getResource(WritingDeskBlockEntity.SLOT_TABLET).toStack();
                writingDeskBlockEntity.clearContents();

                if(!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }

                level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1f);
            } else {
                player.openMenu(writingDeskBlockEntity, buf -> buf.writeBlockPos(pos));
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.SUCCESS;
    }
}
