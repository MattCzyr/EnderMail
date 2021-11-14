package com.chaosthedude.endermail.blocks;

import java.util.List;
import java.util.Random;

import com.chaosthedude.endermail.blocks.te.PackageTileEntity;
import com.chaosthedude.endermail.config.ConfigHandler;
import com.chaosthedude.endermail.data.LockerWorldData;
import com.chaosthedude.endermail.entity.EnderMailmanEntity;
import com.chaosthedude.endermail.gui.ScreenWrapper;
import com.chaosthedude.endermail.items.PackageControllerItem;
import com.chaosthedude.endermail.registry.EnderMailBlocks;
import com.chaosthedude.endermail.registry.EnderMailEntities;
import com.chaosthedude.endermail.registry.EnderMailItems;
import com.chaosthedude.endermail.util.ControllerState;
import com.chaosthedude.endermail.util.ItemUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class PackageBlock extends ContainerBlock {

	public static final String NAME = "package";

	public static final int INVENTORY_SIZE = 5;

	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	public static final BooleanProperty STAMPED = BooleanProperty.create("stamped");

	public PackageBlock() {
		super(Properties.create(Material.WOOD).hardnessAndResistance(1.0F).sound(SoundType.WOOD));
		setDefaultState(getDefaultState().with(FACING, Direction.NORTH).with(STAMPED, false));
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(FACING);
		builder.add(STAMPED);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (player.isSpectator()) {
			return ActionResultType.SUCCESS;
		}
		boolean holdingStamp = ItemUtils.isHolding(player, EnderMailItems.STAMP);
		boolean holdingPackageController = ItemUtils.isHolding(player, EnderMailItems.PACKAGE_CONTROLLER);
		if (world.isRemote() && !isStamped(state) && player.getHeldItem(hand).getItem() == EnderMailItems.STAMP) {
			ScreenWrapper.openStampScreen(world, player, pos);
			return ActionResultType.SUCCESS;
		} else if (world.isRemote() && ((!isStamped(state) && !player.isSneaking()) || (isStamped(state) && (player.isCrouching() || (player.getHeldItem(hand) == ItemStack.EMPTY && !holdingStamp && !holdingPackageController))))) {
			return ActionResultType.SUCCESS;
		} else if (!world.isRemote()) {
			if (isStamped(state) && player.getHeldItem(hand).getItem() == EnderMailItems.PACKAGE_CONTROLLER) {
				ItemStack stack = ItemUtils.getHeldItem(player, EnderMailItems.PACKAGE_CONTROLLER);
				PackageControllerItem packageController = (PackageControllerItem) stack.getItem();
				BlockPos deliveryPos = getDeliveryPos(world, pos);
				String lockerID = getLockerID(world, pos);
				if (deliveryPos != null) {
					packageController.setDeliveryPos(stack, deliveryPos);
					int distanceToDelivery = (int) Math.sqrt(pos.distanceSq(deliveryPos));
					if (lockerID != null && !LockerWorldData.get(world).lockerExists(lockerID) && !hasDeliveryLocation(world, pos)) {
						packageController.setState(stack, ControllerState.INVALID_LOCKER);
						packageController.setLockerID(stack, lockerID);
					} else if (ConfigHandler.GENERAL.maxDeliveryDistance.get() > -1 && distanceToDelivery > ConfigHandler.GENERAL.maxDeliveryDistance.get()) {
						packageController.setState(stack, ControllerState.TOOFAR);
						packageController.setDeliveryDistance(stack, distanceToDelivery);
						packageController.setMaxDistance(stack, ConfigHandler.GENERAL.maxDeliveryDistance.get());
					} else {
						packageController.setState(stack, ControllerState.DELIVERING);
						EnderMailmanEntity enderMailman = new EnderMailmanEntity(EnderMailEntities.ENDER_MAILMAN_TYPE, world, pos, deliveryPos, lockerID, stack);
						world.addEntity(enderMailman);
					}
				}
				return ActionResultType.SUCCESS;
			} else if (isStamped(state) && (player.isCrouching() || (player.getHeldItem(hand) == ItemStack.EMPTY && !holdingStamp && !holdingPackageController))) {
				setState(false, world, pos);
				return ActionResultType.SUCCESS;
			} else if (!isStamped(state) && !player.isCrouching() && !holdingStamp) {
				TileEntity te = world.getTileEntity(pos);
 				if (te != null && te instanceof PackageTileEntity) {
 					NetworkHooks.openGui((ServerPlayerEntity) player, (PackageTileEntity) te, pos);
 				}
				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (stack.hasDisplayName()) {
			TileEntity tileentity = world.getTileEntity(pos);
			if (tileentity instanceof PackageTileEntity) {
				((PackageTileEntity) tileentity).setCustomName(stack.getDisplayName());
			}
		}
	}

	@Override
	public void harvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack stack) {
		player.addStat(Stats.BLOCK_MINED.get(this));
		player.addExhaustion(0.005F);

		if (te != null && te instanceof PackageTileEntity) {
			PackageTileEntity tePackage = (PackageTileEntity) te;
			ItemStack stackPackage = new ItemStack(EnderMailItems.PACKAGE);
			CompoundNBT stackTag = new CompoundNBT();
			CompoundNBT itemTag = tePackage.writeItems(new CompoundNBT());
			if (!itemTag.isEmpty()) {
				stackTag.put("BlockEntityTag", itemTag);
			}
			if (!stackTag.isEmpty()) {
				stackPackage.setTag(stackTag);
			}

			if (tePackage.hasCustomName()) {
				stackPackage.setDisplayName(tePackage.getDisplayName());
			}

			spawnAsEntity(world, pos, stackPackage);

			world.updateComparatorOutputLevel(pos, state.getBlock());
		}
	}

	@Override
	public ItemStack getItem(IBlockReader world, BlockPos pos, BlockState state) {
		ItemStack stack = super.getItem(world, pos, state);
		TileEntity te = world.getTileEntity(pos);
		if (te != null && te instanceof PackageTileEntity) {
			PackageTileEntity tePackage = (PackageTileEntity) te;
			CompoundNBT stackTag = new CompoundNBT();
			CompoundNBT itemTag = tePackage.writeItems(new CompoundNBT());
			if (!itemTag.isEmpty()) {
				stackTag.put("BlockEntityTag", itemTag);
			}
			if (!stackTag.isEmpty()) {
				stack.setTag(stackTag);
			}

			if (tePackage.hasCustomName()) {
				stack.setDisplayName(tePackage.getDisplayName());
			}
		}

		return stack;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		super.addInformation(stack, world, tooltip, flag);
		if (Screen.hasShiftDown()) {
			CompoundNBT temp = stack.getTag();
			if (temp != null && temp.contains("BlockEntityTag", 10)) {
				CompoundNBT tag = temp.getCompound("BlockEntityTag");
				if (tag.contains("Items", 9)) {
					NonNullList<ItemStack> content = NonNullList.<ItemStack>withSize(INVENTORY_SIZE, ItemStack.EMPTY);
					ItemStackHelper.loadAllItems(tag, content);
					for (ItemStack contentStack : content) {
						if (!contentStack.isEmpty()) {
							IFormattableTextComponent textComponent = contentStack.getDisplayName().copyRaw();
							textComponent.appendString(" x").appendString(String.valueOf(contentStack.getCount())).mergeStyle(TextFormatting.GRAY);
							tooltip.add(textComponent);
						}
					}
				}
			}
		} else {
			tooltip.add(new StringTextComponent(I18n.format("string.endermail.holdShift")).mergeStyle(TextFormatting.ITALIC, TextFormatting.GRAY));
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
		return new PackageTileEntity();
	}

	public BlockState getStampedState() {
		return getDefaultState().with(STAMPED, true);
	}
	
	public BlockState getRandomlyRotatedStampedState() {
		Direction[] directions = { Direction.NORTH, Direction.EAST, Direction.WEST, Direction.SOUTH };
		return getDefaultState().with(STAMPED, true).with(FACING, directions[new Random().nextInt(4)]);
	}

	public boolean isStamped(BlockState state) {
		return state.get(STAMPED).booleanValue();
	}

	public static void stampPackage(World world, BlockPos packagePos, BlockPos deliveryPos, String lockerID, boolean hasDeliveryPos) {
		setState(true, world, packagePos);
		TileEntity te = world.getTileEntity(packagePos);
		if (te != null && te instanceof PackageTileEntity) {
			PackageTileEntity tePackage = (PackageTileEntity) te;
			tePackage.setDeliveryPos(deliveryPos, hasDeliveryPos);
			tePackage.setLockerID(lockerID);
		}
	}

	public static BlockPos getDeliveryPos(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te != null && te instanceof PackageTileEntity) {
			PackageTileEntity tePackage = (PackageTileEntity) te;
			return tePackage.getDeliveryPos();
		}
		return null;
	}
	
	public static String getLockerID(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te != null && te instanceof PackageTileEntity) {
			PackageTileEntity tePackage = (PackageTileEntity) te;
			return tePackage.getLockerID();
		}
		return null;
	}
	
	public static boolean hasDeliveryLocation(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te != null && te instanceof PackageTileEntity) {
			PackageTileEntity tePackage = (PackageTileEntity) te;
			return tePackage.hasDeliveryLocation();
		}
		return false;
	}

	public static void setState(boolean stamped, World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		world.setBlockState(pos, state.with(STAMPED, stamped), 3);
	}

}
