package com.chaosthedude.endermail.block;

import com.chaosthedude.endermail.block.entity.LockerBlockEntity;
import com.chaosthedude.endermail.registry.EnderMailBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class LockerBlock extends BaseEntityBlock {

	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty FILLED = BooleanProperty.create("filled");

	public static final String NAME = "locker";

	public static final int INVENTORY_SIZE = 3;
	public static final int MAX_ID_LENGTH = 12;

	public LockerBlock() {
		super(Properties.of(Material.METAL).strength(2.0F).sound(SoundType.METAL));
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(FILLED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, FILLED);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		if (!player.isCrouching()) {
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity != null && blockEntity instanceof LockerBlockEntity) {
				LockerBlockEntity lockerBlockEntity = (LockerBlockEntity) blockEntity;
				NetworkHooks.openGui((ServerPlayer) player, lockerBlockEntity, buf -> buf.writeBlockPos(pos).writeUtf(lockerBlockEntity.getLockerID()));
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (stack.hasCustomHoverName()) {
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity instanceof LockerBlockEntity) {
				((LockerBlockEntity) blockEntity).setLockerID(stack.getDisplayName().getString());
			}
		}
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity instanceof LockerBlockEntity) {
				LockerBlockEntity lockerBlockEntity = (LockerBlockEntity) blockEntity;
				lockerBlockEntity.removeData();
				Containers.dropContents(level, pos, lockerBlockEntity);
				level.updateNeighbourForOutputSignal(pos, this);
			}
            super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate((Direction) state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation((Direction) state.getValue(FACING)));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new LockerBlockEntity(pos, state);
	}

	public static void setFilled(boolean filled, Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (state.getBlock() == EnderMailBlocks.LOCKER.get()) {
			if (state.getValue(FILLED) != filled) {
				level.setBlock(pos, state.setValue(FILLED, filled), 3);
			}
		}
	}

}
