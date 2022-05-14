package com.chaosthedude.endermail.block;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.chaosthedude.endermail.block.entity.PackageBlockEntity;
import com.chaosthedude.endermail.config.ConfigHandler;
import com.chaosthedude.endermail.data.LockerWorldData;
import com.chaosthedude.endermail.entity.EnderMailmanEntity;
import com.chaosthedude.endermail.gui.ScreenWrapper;
import com.chaosthedude.endermail.item.PackageControllerItem;
import com.chaosthedude.endermail.registry.EnderMailBlocks;
import com.chaosthedude.endermail.registry.EnderMailEntities;
import com.chaosthedude.endermail.registry.EnderMailItems;
import com.chaosthedude.endermail.util.ControllerState;
import com.chaosthedude.endermail.util.ItemUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class PackageBlock extends BaseEntityBlock {

	public static final String NAME = "package";

	public static final int INVENTORY_SIZE = 5;

	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty STAMPED = BooleanProperty.create("stamped");

	public PackageBlock() {
		super(Properties.of(Material.WOOD).strength(1.0F).sound(SoundType.WOOD));
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(STAMPED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, STAMPED);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (player.isSpectator()) {
			return InteractionResult.SUCCESS;
		}
		boolean holdingStamp = ItemUtils.isHolding(player, EnderMailItems.STAMP);
		boolean holdingPackageController = ItemUtils.isHolding(player, EnderMailItems.PACKAGE_CONTROLLER);
		if (level.isClientSide() && !isStamped(state) && player.getItemInHand(hand).getItem() == EnderMailItems.STAMP) {
			ScreenWrapper.openStampScreen(level, player, pos);
			return InteractionResult.SUCCESS;
		} else if (level.isClientSide() && ((!isStamped(state) && !player.isCrouching()) || (isStamped(state) && (player.isCrouching() || (player.getItemInHand(hand) == ItemStack.EMPTY && !holdingStamp && !holdingPackageController))))) {
			return InteractionResult.SUCCESS;
		} else if (!level.isClientSide() && level instanceof ServerLevel) {
			ServerLevel serverLevel = (ServerLevel) level;
			if (isStamped(state) && player.getItemInHand(hand).getItem() == EnderMailItems.PACKAGE_CONTROLLER) {
				ItemStack stack = ItemUtils.getHeldItem(player, EnderMailItems.PACKAGE_CONTROLLER);
				PackageControllerItem packageController = (PackageControllerItem) stack.getItem();
				BlockPos deliveryPos = getDeliveryPos(level, pos);
				String lockerID = getLockerID(level, pos);
				if (deliveryPos != null) {
					packageController.setDeliveryPos(stack, deliveryPos);
					int distanceToDelivery = (int) Math.sqrt(pos.distSqr(deliveryPos));
					if (lockerID != null && !LockerWorldData.get(serverLevel).lockerExists(lockerID) && !hasDeliveryLocation(level, pos)) {
						packageController.setState(stack, ControllerState.INVALID_LOCKER);
						packageController.setLockerID(stack, lockerID);
					} else if (ConfigHandler.GENERAL.maxDeliveryDistance.get() > -1 && distanceToDelivery > ConfigHandler.GENERAL.maxDeliveryDistance.get()) {
						packageController.setState(stack, ControllerState.TOOFAR);
						packageController.setDeliveryDistance(stack, distanceToDelivery);
						packageController.setMaxDistance(stack, ConfigHandler.GENERAL.maxDeliveryDistance.get());
					} else {
						packageController.setState(stack, ControllerState.DELIVERING);
						EnderMailmanEntity enderMailman = new EnderMailmanEntity(EnderMailEntities.ENDER_MAILMAN_TYPE, level, pos, deliveryPos, lockerID, stack);
						level.addFreshEntity(enderMailman);
					}
				}
				return InteractionResult.SUCCESS;
			} else if (isStamped(state) && (player.isCrouching() || (player.getItemInHand(hand) == ItemStack.EMPTY && !holdingStamp && !holdingPackageController))) {
				setState(false, level, pos);
				return InteractionResult.SUCCESS;
			} else if (!isStamped(state) && !player.isCrouching() && !holdingStamp) {
				BlockEntity te = level.getBlockEntity(pos);
				if (te != null && te instanceof PackageBlockEntity) {
					NetworkHooks.openGui((ServerPlayer) player, (PackageBlockEntity) te, pos);
				}
				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (stack.hasCustomHoverName()) {
			BlockEntity tileentity = world.getBlockEntity(pos);
			if (tileentity instanceof PackageBlockEntity) {
				((PackageBlockEntity) tileentity).setCustomName(stack.getDisplayName());
			}
		}
	}

	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		BlockEntity te = level.getBlockEntity(pos);
		if (te != null && te instanceof PackageBlockEntity) {
			PackageBlockEntity tePackage = (PackageBlockEntity) te;
			if (!level.isClientSide() && !player.isCreative()) {
				ItemStack stackPackage = new ItemStack(EnderMailItems.PACKAGE);
				CompoundTag stackTag = new CompoundTag();
				CompoundTag itemTag = tePackage.writeItems(new CompoundTag());
				if (!itemTag.isEmpty()) {
					stackTag.put("BlockEntityTag", itemTag);
				}
				if (!stackTag.isEmpty()) {
					stackPackage.setTag(stackTag);
				}
	
				if (tePackage.hasCustomName()) {
					stackPackage.setHoverName(tePackage.getDisplayName());
				}
	
				ItemEntity itemEntity = new ItemEntity(level, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, stackPackage);
	            itemEntity.setDefaultPickUpDelay();
	            level.addFreshEntity(itemEntity);
			}
		}
		super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
		ItemStack stack = super.getCloneItemStack(world, pos, state);
		BlockEntity te = world.getBlockEntity(pos);
		if (te != null && te instanceof PackageBlockEntity) {
			PackageBlockEntity tePackage = (PackageBlockEntity) te;
			CompoundTag stackTag = new CompoundTag();
			CompoundTag itemTag = tePackage.writeItems(new CompoundTag());
			if (!itemTag.isEmpty()) {
				stackTag.put("BlockEntityTag", itemTag);
			}
			if (!stackTag.isEmpty()) {
				stack.setTag(stackTag);
			}

			if (tePackage.hasCustomName()) {
				stack.setHoverName(tePackage.getDisplayName());
			}
		}

		return stack;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, world, tooltip, flag);
		if (Screen.hasShiftDown()) {
			CompoundTag temp = stack.getTag();
			if (temp != null && temp.contains("BlockEntityTag", 10)) {
				CompoundTag tag = temp.getCompound("BlockEntityTag");
				if (tag.contains("Items", 9)) {
					NonNullList<ItemStack> content = NonNullList.<ItemStack>withSize(INVENTORY_SIZE, ItemStack.EMPTY);
					ContainerHelper.loadAllItems(tag, content);
					for (ItemStack contentStack : content) {
						if (!contentStack.isEmpty()) {
							MutableComponent textComponent = contentStack.getDisplayName().copy();
							textComponent.append(" x").append(String.valueOf(contentStack.getCount())).withStyle(ChatFormatting.GRAY);
							tooltip.add(textComponent);
						}
					}
				}
			}
		} else {
			tooltip.add(new TranslatableComponent("string.endermail.holdShift").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	 public RenderShape getRenderShape(BlockState p_56255_) {
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
		return new PackageBlockEntity(pos, state);
	}

	public BlockState getStampedState() {
		return defaultBlockState().setValue(STAMPED, true);
	}

	public BlockState getRandomlyRotatedStampedState() {
		Direction[] directions = { Direction.NORTH, Direction.EAST, Direction.WEST, Direction.SOUTH };
		return defaultBlockState().setValue(STAMPED, true).setValue(FACING, directions[new Random().nextInt(4)]);
	}

	public boolean isStamped(BlockState state) {
		return state.getValue(STAMPED).booleanValue();
	}

	public static void stampPackage(Level world, BlockPos packagePos, BlockPos deliveryPos, String lockerID, boolean hasDeliveryPos) {
		setState(true, world, packagePos);
		BlockEntity te = world.getBlockEntity(packagePos);
		if (te != null && te instanceof PackageBlockEntity) {
			PackageBlockEntity tePackage = (PackageBlockEntity) te;
			tePackage.setDeliveryPos(deliveryPos, hasDeliveryPos);
			tePackage.setLockerID(lockerID);
		}
	}

	public static BlockPos getDeliveryPos(Level world, BlockPos pos) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te != null && te instanceof PackageBlockEntity) {
			PackageBlockEntity tePackage = (PackageBlockEntity) te;
			return tePackage.getDeliveryPos();
		}
		return null;
	}

	public static String getLockerID(Level world, BlockPos pos) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te != null && te instanceof PackageBlockEntity) {
			PackageBlockEntity tePackage = (PackageBlockEntity) te;
			return tePackage.getLockerID();
		}
		return null;
	}

	public static boolean hasDeliveryLocation(Level world, BlockPos pos) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te != null && te instanceof PackageBlockEntity) {
			PackageBlockEntity tePackage = (PackageBlockEntity) te;
			return tePackage.hasDeliveryLocation();
		}
		return false;
	}

	public static void setState(boolean stamped, Level world, BlockPos pos) {
		BlockState iblockstate = world.getBlockState(pos);
		BlockEntity tileentity = world.getBlockEntity(pos);
		world.setBlock(pos, EnderMailBlocks.PACKAGE.defaultBlockState().setValue(FACING, iblockstate.getValue(FACING)).setValue(STAMPED, stamped), 3);
		if (tileentity != null) {
			world.setBlockEntity(tileentity);
		}
	}

}
