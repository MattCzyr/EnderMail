package com.chaosthedude.endermail.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.block.PackageBlock;
import com.chaosthedude.endermail.block.entity.LockerBlockEntity;
import com.chaosthedude.endermail.block.entity.PackageBlockEntity;
import com.chaosthedude.endermail.config.ConfigHandler;
import com.chaosthedude.endermail.data.LockerData;
import com.chaosthedude.endermail.item.PackageControllerItem;
import com.chaosthedude.endermail.registry.EnderMailBlocks;
import com.chaosthedude.endermail.registry.EnderMailItems;
import com.chaosthedude.endermail.util.ControllerState;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class EnderMailmanEntity extends Monster {

	public static final String NAME = "ender_mailman";

	private static final EntityDataAccessor<Boolean> CARRYING_PACKAGE = SynchedEntityData.defineId(EnderMailmanEntity.class, EntityDataSerializers.BOOLEAN);
	private NonNullList<ItemStack> contents = NonNullList.<ItemStack>withSize(PackageBlock.INVENTORY_SIZE, ItemStack.EMPTY);
	private int lastCreepySound;
	private int timePickedUp;
	private int timeDelivered;
	private boolean isDelivering;
	private BlockPos startingPos;
	private BlockPos deliveryPos;
	private ItemStack packageController;

	public EnderMailmanEntity(EntityType<? extends EnderMailmanEntity> entityType, Level level) {
		super(entityType, level);
		setMaxUpStep(1.0F);
		setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
	}

	public EnderMailmanEntity(EntityType<? extends EnderMailmanEntity> entityType, Level level, BlockPos startingPos, BlockPos deliveryPos, String lockerID, ItemStack packageController) {
		super(entityType, level);
		this.packageController = packageController;
		setPos(startingPos.getX() + getRandomOffset(), startingPos.getY(), startingPos.getZ() + getRandomOffset());
		setStartingPos(startingPos);
		findDeliveryPos(lockerID, deliveryPos);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(CARRYING_PACKAGE, Boolean.valueOf(false));
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.0D, 0.0F));
		goalSelector.addGoal(2, new EnderMailmanEntity.DeliverGoal(this));
		goalSelector.addGoal(3, new EnderMailmanEntity.TakePackageGoal(this));
		goalSelector.addGoal(4, new EnderMailmanEntity.DieGoal(this));
		goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
		goalSelector.addGoal(6, new RandomLookAroundGoal(this));
		goalSelector.addGoal(7, new HurtByTargetGoal(this));
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40.0D).add(Attributes.MOVEMENT_SPEED, (double) 0.3F).add(Attributes.ATTACK_DAMAGE, 7.0D).add(Attributes.FOLLOW_RANGE, 64.0D);
	}

	@Override
	public void tick() {
		if (level().isClientSide()) {
			for (int i = 0; i < 2; ++i) {
				level().addParticle(ParticleTypes.PORTAL, getRandomX(0.5D), getRandomY() - 0.25D, getRandomZ(0.5D), (random.nextDouble() - 0.5D) * 2.0D, -random.nextDouble(), (random.nextDouble() - 0.5D) * 2.0D);
			}

			if (tickCount - timeDelivered > 100) {
				diePeacefully();
			}
		}

		jumping = false;
		super.tick();
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (isInvulnerableTo(source)) {
			return false;
		} else if (source.isIndirect()) {
			for (int i = 0; i < 64; ++i) {
				if (this.teleportRandomly()) {
					return true;
				}
			}

			return false;
		} else {
			boolean flag = super.hurt(source, amount);
			if (!level().isClientSide() && !(source.getEntity() instanceof LivingEntity) && this.random.nextInt(10) != 0) {
				teleportRandomly();
			}

			return flag;
		}
	}

	@Override
	protected void customServerAiStep() {
		if (isInWaterRainOrBubble()) {
			hurt(damageSources().drown(), 1.0F);
		}

		if (level().isDay()) {
			float f = getLightLevelDependentMagicValue();
			if (f > 0.5F && level().canSeeSky(blockPosition()) && random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
				teleportRandomly();
			}
		}

		super.customServerAiStep();
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENDERMAN_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource p_32527_) {
		return SoundEvents.ENDERMAN_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENDERMAN_DEATH;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		ContainerHelper.saveAllItems(tag, contents);

		tag.putInt("StartingX", startingPos.getX());
		tag.putInt("StartingY", startingPos.getY());
		tag.putInt("StartingZ", startingPos.getZ());

		tag.putInt("DeliveryX", deliveryPos.getX());
		tag.putInt("DeliveryY", deliveryPos.getY());
		tag.putInt("DeliveryZ", deliveryPos.getZ());

		tag.putBoolean("IsDelivering", isDelivering);
		tag.putBoolean("IsCarryingPackage", isCarryingPackage());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		ContainerHelper.loadAllItems(tag, contents);

		startingPos = new BlockPos(tag.getInt("StartingX"), tag.getInt("StartingY"), tag.getInt("StartingZ"));
		deliveryPos = new BlockPos(tag.getInt("DeliveryX"), tag.getInt("DeliveryY"), tag.getInt("DeliveryZ"));

		isDelivering = tag.getBoolean("IsDelivering");
		entityData.set(CARRYING_PACKAGE, tag.getBoolean("IsCarryingPackage"));
	}

	@Override
	protected void dropEquipment() {
		super.dropEquipment();
		for (ItemStack stack : contents) {
			spawnAtLocation(stack);
		}
	}

	@Override
	protected boolean shouldDespawnInPeaceful() {
		return false;
	}

	private boolean teleport(double x, double y, double z) {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);
		while (pos.getY() > level().getMinBuildHeight() && !level().getBlockState(pos).blocksMotion()) {
			pos.move(Direction.DOWN);
		}

		BlockState state = level().getBlockState(pos);
		boolean flag = state.blocksMotion();
		boolean flag1 = state.getFluidState().is(FluidTags.WATER);
		if (flag && !flag1) {
			boolean flag2 = randomTeleport(x, y, z, true);
			if (flag2 && !isSilent()) {
				level().playSound((Player) null, xo, yo, zo, SoundEvents.ENDERMAN_TELEPORT, getSoundSource(), 1.0F, 1.0F);
				playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
			}
			return flag2;
		} else {
			return false;
		}
	}

	protected boolean teleportRandomly() {
		double x = getX() + (random.nextDouble() - 0.5D) * 64.0D;
		double y = getY() + (double) (random.nextInt(64) - 32);
		double z = getZ() + (random.nextDouble() - 0.5D) * 64.0D;
		return teleport(x, y, z);
	}

	private boolean canPlacePackage(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		BlockPos belowPos = pos.below();
		BlockState belowState = level.getBlockState(belowPos);
		return !state.liquid() && state.canBeReplaced() && !belowState.canBeReplaced() && !belowState.is(Blocks.BEDROCK) && belowState.isCollisionShapeFullBlock(level, belowPos) && EnderMailBlocks.PACKAGE.get().defaultBlockState().canSurvive(level, pos);
	}

	private BlockPos findLocker(String lockerID) {
		if (level() instanceof ServerLevel) {
			ServerLevel serverLevel = (ServerLevel) level();
			LockerData data = LockerData.get(serverLevel);
			return data.getLockers().get(lockerID);
		}
		return null;
	}

	private BlockPos findLockerNearPos(BlockPos pos) {
		if (level() instanceof ServerLevel) {
			ServerLevel serverLevel = (ServerLevel) level();
			LockerData data = LockerData.get(serverLevel);
			for (String lockerID : data.getLockers().keySet()) {
				BlockPos lockerPos = data.getLockers().get(lockerID);
				if (ConfigHandler.GENERAL.lockerDeliveryRadiusIgnoresY.get()) {
					int deltaX = pos.getX() - lockerPos.getX();
					int deltaZ = pos.getZ() - lockerPos.getZ();
					int distanceSq = (deltaX * deltaX) + (deltaZ * deltaZ);
					if (distanceSq < ConfigHandler.GENERAL.lockerDeliveryRadius.get() * ConfigHandler.GENERAL.lockerDeliveryRadius.get()) {
						return lockerPos;
					}
				} else {
					if (pos.closerThan(lockerPos, ConfigHandler.GENERAL.lockerDeliveryRadius.get())) {
						return lockerPos;
					}
				}
			}
		}
		return null;
	}

	private int findValidDeliveryHeight(BlockPos pos, int maxHeightDifference) {
		if (pos != null) {
			int startY = pos.getY() <= 0 ? level().getSeaLevel() : pos.getY();
			int upY = startY;
			int downY = startY;
			while (!(canPlacePackage(level(), new BlockPos(pos.getX(), upY, pos.getZ())) || canPlacePackage(level(), new BlockPos(pos.getX(), downY, pos.getZ()))) && (upY < 255 || downY > 1) && upY - startY < maxHeightDifference && startY - downY < maxHeightDifference) {
				upY++;
				downY--;
			}
			BlockPos upPos = new BlockPos(pos.getX(), upY, pos.getZ());
			BlockPos downPos = new BlockPos(pos.getX(), downY, pos.getZ());
			if (upY < 255 && canPlacePackage(level(), upPos)) {
				return upY;
			}
			if (downY > 1 && canPlacePackage(level(), downPos)) {
				return downY;
			}
		}
		return -1;
	}

	private void findDeliveryPos(String lockerID, BlockPos pos) {
		if (lockerID != null && !lockerID.isEmpty()) {
			BlockPos lockerPos = findLocker(lockerID);
			if (lockerPos != null) {
				setDeliveryPos(lockerPos);
				return;
			}
		}
		if (pos != null) {
			BlockPos validLocker = findLockerNearPos(pos);
			if (validLocker != null) {
				setDeliveryPos(validLocker);
				return;
			}
			int deliveryY = findValidDeliveryHeight(pos, 255);
			setDeliveryPos(new BlockPos(pos.getX(), deliveryY, pos.getZ()));
		}
	}

	public void diePeacefully() {
		teleportTo(getX(), -10, getZ());
		hurt(damageSources().outOfBorder(), Float.MAX_VALUE);
	}

	public void setContents(NonNullList<ItemStack> contents) {
		this.contents = contents;
	}

	public NonNullList<ItemStack> getContents() {
		return contents;
	}

	public double getDistance(double x, double y, double z) {
		return Math.sqrt(distanceToSqr(x, y, z));
	}

	public double getDistanceToDelivery() {
		return getDistance(deliveryPos.getX(), deliveryPos.getY(), deliveryPos.getZ());
	}

	public double getDistanceToStart() {
		return getDistance(startingPos.getX(), startingPos.getY(), startingPos.getZ());
	}

	public BlockPos getDeliveryPos() {
		return deliveryPos;
	}

	public void setDeliveryPos(BlockPos pos) {
		deliveryPos = pos;
		setDelivering(true);
	}

	public BlockPos getStartingPos() {
		return startingPos;
	}

	public void setPackageController(ItemStack packageController) {
		this.packageController = packageController;
	}

	public ItemStack getPackageController() {
		return packageController;
	}

	public PackageControllerItem getPackageControllerItem() {
		return (PackageControllerItem) packageController.getItem();
	}

	public boolean hasPackageController() {
		return packageController != null && !packageController.isEmpty();
	}

	public boolean isCarryingPackage() {
		return entityData.get(CARRYING_PACKAGE);
	}

	public boolean isDelivering() {
		return isDelivering;
	}

	public void setCarryingPackage(boolean carrying) {
		entityData.set(CARRYING_PACKAGE, carrying);
	}

	public void setDelivering(boolean delivering) {
		isDelivering = delivering;
	}

	public boolean shouldDeliverOnGround() {
		return canPlacePackage(level(), getDeliveryPos());
	}

	public boolean shouldDeliverToLocker() {
		return level().getBlockState(deliveryPos).getBlock() == EnderMailBlocks.LOCKER.get();
	}

	public void updateTimePickedUp() {
		timePickedUp = tickCount;
	}

	public int getTimePickedUp() {
		return timePickedUp;
	}

	public void updateTimeDelivered() {
		timeDelivered = tickCount;
	}

	public int getTimeDelivered() {
		return timeDelivered;
	}

	public void playEndermanSound() {
		if (tickCount >= lastCreepySound + 400) {
			lastCreepySound = tickCount;
			if (!isSilent()) {
				level().playLocalSound(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ENDERMAN_STARE, this.getSoundSource(), 2.5F, 1.0F, false);
			}
		}
	}

	public double getRandomOffset() {
		return random.nextDouble() * 2 * (random.nextBoolean() ? 1 : -1);
	}

	public void teleportToDeliveryPos() {
		double x = getDeliveryPos().getX() + getRandomOffset();
		double y = getDeliveryPos().getY();
		double z = getDeliveryPos().getZ() + getRandomOffset();
		teleportTo(x, y, z);
	}

	public void teleportToStartingPos() {
		double x = getStartingPos().getX() + getRandomOffset();
		double y = getStartingPos().getY();
		double z = getStartingPos().getZ() + getRandomOffset();
		teleportTo(x, y, z);
	}

	public boolean isAtStartingPos() {
		return blockPosition() == startingPos;
	}

	public void setStartingPos(BlockPos pos) {
		startingPos = pos;
	}

	public ItemStack getPackageStack() {
		ItemStack stackPackage = new ItemStack(EnderMailItems.PACKAGE.get());
		CompoundTag stackTag = new CompoundTag();
		CompoundTag itemTag = new CompoundTag();
		if (!contents.isEmpty()) {
			itemTag = ContainerHelper.saveAllItems(itemTag, contents);
		}
		if (!itemTag.isEmpty()) {
			stackTag.put("BlockEntityTag", itemTag);
		}
		if (!stackTag.isEmpty()) {
			stackPackage.setTag(stackTag);
		}
		return stackPackage;
	}

	static class DeliverGoal extends Goal {
		private final EnderMailmanEntity enderMailman;

		public DeliverGoal(EnderMailmanEntity enderMailman) {
			this.enderMailman = enderMailman;
		}

		@Override
		public boolean canUse() {
			return enderMailman.isDelivering() && enderMailman.isCarryingPackage();
		}

		@Override
		public void tick() {
			if (enderMailman.tickCount - enderMailman.getTimePickedUp() >= 100) {
				boolean delivered = false;
				if (enderMailman.shouldDeliverOnGround()) {
					enderMailman.teleportToDeliveryPos();
					BlockState newBlockState = EnderMailBlocks.PACKAGE.get().getRandomlyRotatedStampedState();
					enderMailman.level().setBlock(enderMailman.getDeliveryPos(), newBlockState, 3);
					enderMailman.level().setBlockEntity(new PackageBlockEntity(enderMailman.getContents(), enderMailman.getDeliveryPos(), newBlockState));
					if (enderMailman.hasPackageController()) {
						enderMailman.getPackageControllerItem().setState(enderMailman.packageController, ControllerState.DELIVERED);
						enderMailman.getPackageControllerItem().setDeliveryPos(enderMailman.packageController, enderMailman.getDeliveryPos());
					}
					if (ConfigHandler.GENERAL.logDeliveries.get()) {
						EnderMail.LOGGER.info("Delivered package to " + enderMailman.getDeliveryPos().getX() + ", " + enderMailman.getDeliveryPos().getY() + ", " + enderMailman.getDeliveryPos().getZ());
					}
					delivered = true;
				} else if (enderMailman.shouldDeliverToLocker()) {
					BlockEntity blockEntity = enderMailman.level().getBlockEntity(enderMailman.getDeliveryPos());
					if (blockEntity != null && blockEntity instanceof LockerBlockEntity) {
						enderMailman.teleportToDeliveryPos();
						LockerBlockEntity lockerBlockEntity = (LockerBlockEntity) blockEntity;
						ItemStack stackPackage = enderMailman.getPackageStack();
						boolean putInLocker = lockerBlockEntity.addPackage(stackPackage);
						if (putInLocker) {
							if (enderMailman.hasPackageController()) {
								enderMailman.getPackageControllerItem().setState(enderMailman.packageController, ControllerState.DELIVERED_TO_LOCKER);
								enderMailman.getPackageControllerItem().setLockerID(enderMailman.packageController, lockerBlockEntity.getLockerID());
								enderMailman.getPackageControllerItem().setDeliveryPos(enderMailman.packageController, enderMailman.getDeliveryPos());
								enderMailman.getPackageControllerItem().setShowLockerLocation(enderMailman.packageController, !ConfigHandler.GENERAL.hideLockerLocation.get());
							}
							if (ConfigHandler.GENERAL.logDeliveries.get()) {
								EnderMail.LOGGER.info("Delivered package to locker " + lockerBlockEntity.getLockerID() + " at " + enderMailman.getDeliveryPos().getX() + ", " + enderMailman.getDeliveryPos().getY() + ", " + enderMailman.getDeliveryPos().getZ());
							}
							delivered = true;
						} else {
							int y = -1;
							List<Direction> directions = new ArrayList<Direction>(Arrays.asList(Direction.NORTH, Direction.EAST, Direction.WEST, Direction.SOUTH));
							while (y < 0 && !directions.isEmpty()) {
								Direction randomDirection = directions.get(new Random().nextInt(directions.size()));
								directions.remove(randomDirection);
								BlockPos newDeliveryPos = enderMailman.getDeliveryPos().relative(randomDirection);
								y = enderMailman.findValidDeliveryHeight(newDeliveryPos, 8);
								if (y > 0) {
									newDeliveryPos = new BlockPos(newDeliveryPos.getX(), y, newDeliveryPos.getZ());
									BlockState newBlockState = EnderMailBlocks.PACKAGE.get().getRandomlyRotatedStampedState();
									enderMailman.level().setBlock(newDeliveryPos, newBlockState, 3);
									enderMailman.level().setBlockEntity(new PackageBlockEntity(enderMailman.getContents(), newDeliveryPos, newBlockState));
									if (enderMailman.hasPackageController()) {
										enderMailman.getPackageControllerItem().setState(enderMailman.packageController, ControllerState.DELIVERED);
										enderMailman.getPackageControllerItem().setDeliveryPos(enderMailman.packageController, newDeliveryPos);
									}
									if (ConfigHandler.GENERAL.logDeliveries.get()) {
										EnderMail.LOGGER.info("Delivered package to " + newDeliveryPos.getX() + ", " + newDeliveryPos.getY() + ", " + newDeliveryPos.getZ() + " near locker " + lockerBlockEntity.getLockerID());
									}
									delivered = true;
								}
							}
						}
					}
				}
				if (!delivered) {
					enderMailman.teleportToStartingPos();
					BlockState newBlockState = EnderMailBlocks.PACKAGE.get().getRandomlyRotatedStampedState();
					enderMailman.level().setBlock(enderMailman.getStartingPos(), newBlockState, 3);
					enderMailman.level().setBlockEntity(new PackageBlockEntity(enderMailman.getContents(), enderMailman.getStartingPos(), newBlockState));
					if (enderMailman.hasPackageController()) {
						enderMailman.getPackageControllerItem().setState(enderMailman.packageController, ControllerState.UNDELIVERABLE);
					}
				}

				enderMailman.updateTimeDelivered();
				enderMailman.setContents(NonNullList.<ItemStack>withSize(PackageBlock.INVENTORY_SIZE, ItemStack.EMPTY));
				enderMailman.setCarryingPackage(false);
				enderMailman.setDelivering(false);
			} else if ((enderMailman.tickCount - enderMailman.getTimePickedUp()) % 20 == 0) {
				enderMailman.teleportRandomly();
			}
		}
	}

	static class TakePackageGoal extends Goal {
		private final EnderMailmanEntity enderMailman;

		public TakePackageGoal(EnderMailmanEntity enderMailman) {
			this.enderMailman = enderMailman;
		}

		@Override
		public boolean canUse() {
			return enderMailman.isDelivering() && !enderMailman.isCarryingPackage();
		}

		@Override
		public void tick() {
			BlockEntity blockEntity = enderMailman.level().getBlockEntity(enderMailman.startingPos);
			if (blockEntity != null && blockEntity instanceof PackageBlockEntity) {
				PackageBlockEntity packageBlockEntity = (PackageBlockEntity) blockEntity;
				enderMailman.setContents(packageBlockEntity.getContents());
				enderMailman.setCarryingPackage(true);
				enderMailman.level().setBlock(enderMailman.startingPos, Blocks.AIR.defaultBlockState(), 3);
				if (enderMailman.hasPackageController()) {
					enderMailman.getPackageControllerItem().setState(enderMailman.packageController, ControllerState.DELIVERING);
				}
				enderMailman.updateTimePickedUp();
			} else {
				enderMailman.setDelivering(false);
			}
		}
	}

	static class DieGoal extends Goal {
		private final EnderMailmanEntity enderMailman;

		public DieGoal(EnderMailmanEntity enderMailman) {
			this.enderMailman = enderMailman;
		}

		@Override
		public boolean canUse() {
			return !enderMailman.isDelivering();
		}

		@Override
		public void tick() {
			if (enderMailman.tickCount - enderMailman.getTimeDelivered() >= 100) {
				enderMailman.diePeacefully();
			}
		}
	}

}
