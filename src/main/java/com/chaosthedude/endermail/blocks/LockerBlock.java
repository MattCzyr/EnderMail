package com.chaosthedude.endermail.blocks;

import com.chaosthedude.endermail.blocks.te.LockerTileEntity;
import com.chaosthedude.endermail.registry.EnderMailBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class LockerBlock extends ContainerBlock {

	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	public static final BooleanProperty FILLED = BooleanProperty.create("filled");

	public static final String NAME = "locker";

	public static final int INVENTORY_SIZE = 3;
	public static final int MAX_ID_LENGTH = 12;

	public LockerBlock() {
		super(Properties.create(Material.IRON).hardnessAndResistance(2.0F).sound(SoundType.METAL));
		setDefaultState(getDefaultState().with(FACING, Direction.NORTH).with(FILLED, false));
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(FACING);
		builder.add(FILLED);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
		if (world.isRemote()) {
			return ActionResultType.SUCCESS;
		}
		if (!player.isCrouching()) {
			TileEntity te = world.getTileEntity(pos);
			if (te != null && te instanceof LockerTileEntity) {
				LockerTileEntity lockerTe = (LockerTileEntity) te;
				NetworkHooks.openGui((ServerPlayerEntity) player, lockerTe, buf -> buf.writeBlockPos(pos).writeString(lockerTe.getLockerID()));
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (stack.hasDisplayName()) {
			TileEntity tileentity = world.getTileEntity(pos);

			if (tileentity instanceof LockerTileEntity) {
				((LockerTileEntity) tileentity).setLockerID(stack.getDisplayName().getFormattedText());
			}
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate((Direction) state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.toRotation((Direction) state.get(FACING)));
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new LockerTileEntity();
	}

	public static void setFilled(boolean filled, World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() == EnderMailBlocks.LOCKER) {
			if ((state.get(FILLED) && !filled) || (!state.get(FILLED) && filled)) {
				TileEntity tileentity = world.getTileEntity(pos);
				world.setBlockState(pos, EnderMailBlocks.LOCKER.getDefaultState().with(FACING, state.get(FACING)).with(FILLED, filled), 3);
				if (tileentity != null) {
					tileentity.validate();
					world.setTileEntity(pos, tileentity);
				}
			}
		}
	}

}
