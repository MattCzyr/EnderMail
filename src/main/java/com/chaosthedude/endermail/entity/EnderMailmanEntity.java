package com.chaosthedude.endermail.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.blocks.PackageBlock;
import com.chaosthedude.endermail.blocks.te.LockerTileEntity;
import com.chaosthedude.endermail.blocks.te.PackageTileEntity;
import com.chaosthedude.endermail.config.ConfigHandler;
import com.chaosthedude.endermail.data.LockerWorldData;
import com.chaosthedude.endermail.items.PackageControllerItem;
import com.chaosthedude.endermail.registry.EnderMailBlocks;
import com.chaosthedude.endermail.registry.EnderMailItems;
import com.chaosthedude.endermail.util.ControllerState;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnderMailmanEntity extends MonsterEntity {

	public static final String NAME = "ender_mailman";

	private static final DataParameter<Boolean> CARRYING_PACKAGE = EntityDataManager.<Boolean>createKey(EnderMailmanEntity.class, DataSerializers.BOOLEAN);
	private NonNullList<ItemStack> contents = NonNullList.<ItemStack>withSize(PackageBlock.INVENTORY_SIZE, ItemStack.EMPTY);
	private int lastCreepySound;
	private int timePickedUp;
	private int timeDelivered;
	private boolean isDelivering;
	private BlockPos startingPos;
	private BlockPos deliveryPos;
	private ItemStack packageController;

	public EnderMailmanEntity(EntityType<? extends EnderMailmanEntity> entityType, World world) {
		super(entityType, world);
		stepHeight = 1.0F;
		setPathPriority(PathNodeType.WATER, -1.0F);
	}

	public EnderMailmanEntity(EntityType<? extends EnderMailmanEntity> entityType, World world, BlockPos startingPos, BlockPos deliveryPos, String lockerID, ItemStack packageController) {
		super(entityType, world);
		this.packageController = packageController;
		setPosition(startingPos.getX() + getRandomOffset(), startingPos.getY(), startingPos.getZ() + getRandomOffset());
		setStartingPos(startingPos);
		findDeliveryPos(lockerID, deliveryPos);
	}

	@Override
	protected void registerData() {
		super.registerData();
		dataManager.register(CARRYING_PACKAGE, Boolean.valueOf(false));
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1.0D, 0.0F));
		goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
		goalSelector.addGoal(8, new LookRandomlyGoal(this));
		goalSelector.addGoal(10, new EnderMailmanEntity.DeliverGoal(this));
		goalSelector.addGoal(11, new EnderMailmanEntity.TakePackageGoal(this));
		goalSelector.addGoal(12, new EnderMailmanEntity.DieGoal(this));
		goalSelector.addGoal(1, new HurtByTargetGoal(this));
	}
	
	public static AttributeModifierMap.MutableAttribute createAttributes() {
		return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 40.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, (double) 0.3F).createMutableAttribute(Attributes.ATTACK_DAMAGE, 7.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 64.0D);
	}

	@Override
	public void livingTick() {
		if (world.isRemote) {
			for (int i = 0; i < 2; ++i) {
				world.addParticle(ParticleTypes.PORTAL, getPosX() + (rand.nextDouble() - 0.5D) * (double) getWidth(), getPosY() + rand.nextDouble() * (double) getHeight() - 0.25D,
						getPosZ() + (rand.nextDouble() - 0.5D) * (double) getWidth(), (rand.nextDouble() - 0.5D) * 2.0D, -rand.nextDouble(), (rand.nextDouble() - 0.5D) * 2.0D);
			}

			if (ticksExisted - timeDelivered > 100) {
				diePeacefully();
			}
		}

		isJumping = false;
		super.livingTick();
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (isInvulnerableTo(source)) {
			return false;
		} else if (source instanceof IndirectEntityDamageSource) {
			for (int i = 0; i < 64; ++i) {
				if (teleportRandomly()) {
					return true;
				}
			}

			return false;
		} else {
			boolean flag = super.attackEntityFrom(source, amount);
			if (source.isUnblockable() && rand.nextInt(10) != 0) {
				teleportRandomly();
			}

			return flag;
		}
	}

	@Override
	protected void updateAITasks() {
		if (isWet()) {
			attackEntityFrom(DamageSource.DROWN, 1.0F);
		}

		if (world.isDaytime()) {
			float f = getBrightness();
			if (f > 0.5F && world.canBlockSeeSky(getPosition()) && rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
				teleportRandomly();
			}
		}

		super.updateAITasks();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_ENDERMAN_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_ENDERMAN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ENDERMAN_DEATH;
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		ItemStackHelper.saveAllItems(compound, contents);

		compound.putInt("StartingX", startingPos.getX());
		compound.putInt("StartingY", startingPos.getY());
		compound.putInt("StartingZ", startingPos.getZ());

		compound.putInt("DeliveryX", deliveryPos.getX());
		compound.putInt("DeliveryY", deliveryPos.getY());
		compound.putInt("DeliveryZ", deliveryPos.getZ());

		compound.putBoolean("IsDelivering", isDelivering);
		compound.putBoolean("IsCarryingPackage", isCarryingPackage());
	}

	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);
		ItemStackHelper.loadAllItems(compound, contents);

		startingPos = new BlockPos(compound.getInt("StartingX"), compound.getInt("StartingY"), compound.getInt("StartingZ"));
		deliveryPos = new BlockPos(compound.getInt("DeliveryX"), compound.getInt("DeliveryY"), compound.getInt("DeliveryZ"));

		isDelivering = compound.getBoolean("IsDelivering");
		dataManager.set(CARRYING_PACKAGE, compound.getBoolean("IsCarryingPackage"));
	}

	@Override
	protected void dropInventory() {
		super.dropInventory();
		for (ItemStack stack : contents) {
			entityDropItem(stack, 0.0F);
		}
	}
	
	@Override
	protected boolean isDespawnPeaceful() {
		return false;
	}

	private boolean teleportTo(double x, double y, double z) {
		boolean canTeleport = attemptTeleport(x, y, z, false);
		if (canTeleport) {
			world.playSound((PlayerEntity) null, prevPosX, prevPosY, prevPosZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, getSoundCategory(), 1.0F, 1.0F);
			playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
		}

		return canTeleport;
	}

	protected boolean teleportRandomly() {
		double x = getPosX() + (rand.nextDouble() - 0.5D) * 64.0D;
		double y = getPosY() + (double) (rand.nextInt(64) - 32);
		double z = getPosZ() + (rand.nextDouble() - 0.5D) * 64.0D;
		return teleportTo(x, y, z);
	}

	private boolean canPlacePackage(World world, BlockPos pos) {
		return EnderMailBlocks.PACKAGE.getStampedState().isValidPosition(world, pos) && world.isAirBlock(pos) && Block.hasSolidSideOnTop(world, pos.down());
	}

	private BlockPos findLocker(String lockerID) {
		LockerWorldData data = LockerWorldData.get(world);
		if (data != null) {
			return data.getLockers().get(lockerID);
		}
		return null;
	}

	private BlockPos findLockerNearPos(BlockPos pos) {
		LockerWorldData data = LockerWorldData.get(world);
		if (data != null) {
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
					if (pos.withinDistance(lockerPos, ConfigHandler.GENERAL.lockerDeliveryRadius.get())) {
						return lockerPos;
					}
				}
			}
		}
		return null;
	}

	private int findValidDeliveryHeight(BlockPos pos, int maxHeightDifference) {
		if (pos != null) {
			int startY = pos.getY() <= 0 ? world.getSeaLevel() : pos.getY();
			int upY = startY;
			int downY = startY;
			while (!(canPlacePackage(world, new BlockPos(pos.getX(), upY, pos.getZ())) || canPlacePackage(world, new BlockPos(pos.getX(), downY, pos.getZ()))) && (upY < 255 || downY > 1) && upY - startY < maxHeightDifference && startY - downY < maxHeightDifference) {
				upY++;
				downY--;
			}
			BlockPos upPos = new BlockPos(pos.getX(), upY, pos.getZ());
			BlockPos downPos = new BlockPos(pos.getX(), downY, pos.getZ());
			if (upY < 255 && canPlacePackage(world, upPos)) {
				return upY;
			}
			if (downY > 1 && canPlacePackage(world, downPos)) {
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
		teleportTo(getPosX(), -10, getPosZ());
		attackEntityFrom(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
	}

	public void setContents(NonNullList<ItemStack> contents) {
		this.contents = contents;
	}

	public NonNullList<ItemStack> getContents() {
		return contents;
	}

	public double getDistance(double x, double y, double z) {
		return Math.sqrt(getDistanceSq(x, y, z));
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
		return dataManager.get(CARRYING_PACKAGE);
	}

	public boolean isDelivering() {
		return isDelivering;
	}

	public void setCarryingPackage(boolean carrying) {
		dataManager.set(CARRYING_PACKAGE, carrying);
	}

	public void setDelivering(boolean delivering) {
		isDelivering = delivering;
	}

	public boolean shouldDeliverOnGround() {
		return canPlacePackage(world, getDeliveryPos());
	}

	public boolean shouldDeliverToLocker() {
		return world.getBlockState(deliveryPos).getBlock() == EnderMailBlocks.LOCKER;
	}

	public void updateTimePickedUp() {
		timePickedUp = ticksExisted;
	}

	public int getTimePickedUp() {
		return timePickedUp;
	}

	public void updateTimeDelivered() {
		timeDelivered = ticksExisted;
	}

	public int getTimeDelivered() {
		return timeDelivered;
	}

	public void playEndermanSound() {
		if (ticksExisted >= lastCreepySound + 400) {
			lastCreepySound = ticksExisted;
			if (!isSilent()) {
				world.playSound(getPosX(), getPosY() + (double) getEyeHeight(), getPosZ(), SoundEvents.ENTITY_ENDERMAN_STARE, getSoundCategory(), 2.5F, 1.0F, false);
			}
		}
	}

	public double getRandomOffset() {
		return getRNG().nextDouble() * 2 * (getRNG().nextBoolean() ? 1 : -1);
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
		return getPosition() == startingPos;
	}

	public void setStartingPos(BlockPos pos) {
		startingPos = pos;
	}

	public ItemStack getPackageStack() {
		ItemStack stackPackage = new ItemStack(EnderMailItems.PACKAGE);
		CompoundNBT stackTag = new CompoundNBT();
		CompoundNBT itemTag = new CompoundNBT();
		if (!contents.isEmpty()) {
			itemTag = ItemStackHelper.saveAllItems(itemTag, contents);
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
		public boolean shouldExecute() {
			return enderMailman.isDelivering() && enderMailman.isCarryingPackage();
		}

		@Override
		public void tick() {
			if (enderMailman.ticksExisted - enderMailman.getTimePickedUp() >= 100) {
				boolean delivered = false;
				if (enderMailman.shouldDeliverOnGround()) {
					enderMailman.teleportToDeliveryPos();
					enderMailman.world.setBlockState(enderMailman.getDeliveryPos(), EnderMailBlocks.PACKAGE.getRandomlyRotatedStampedState(), 3);
					enderMailman.world.setTileEntity(enderMailman.getDeliveryPos(), new PackageTileEntity(enderMailman.getContents()));
					if (enderMailman.hasPackageController()) {
						enderMailman.getPackageControllerItem().setState(enderMailman.packageController, ControllerState.DELIVERED);
						enderMailman.getPackageControllerItem().setDeliveryPos(enderMailman.packageController, enderMailman.getDeliveryPos());
					}
					if (ConfigHandler.GENERAL.logDeliveries.get()) {
						EnderMail.logger.info("Delivered package to " + enderMailman.getDeliveryPos().getX() + ", " + enderMailman.getDeliveryPos().getY() + ", " + enderMailman.getDeliveryPos().getZ());
					}
					delivered = true;
				} else if (enderMailman.shouldDeliverToLocker()) {
					TileEntity te = enderMailman.world.getTileEntity(enderMailman.getDeliveryPos());
					if (te != null && te instanceof LockerTileEntity) {
						enderMailman.teleportToDeliveryPos();
						LockerTileEntity lockerTe = (LockerTileEntity) te;
						ItemStack stackPackage = enderMailman.getPackageStack();
						boolean putInLocker = lockerTe.addPackage(stackPackage);
						if (putInLocker) {
							if (enderMailman.hasPackageController()) {
								enderMailman.getPackageControllerItem().setState(enderMailman.packageController, ControllerState.DELIVERED_TO_LOCKER);
								enderMailman.getPackageControllerItem().setLockerID(enderMailman.packageController, lockerTe.getLockerID());
								enderMailman.getPackageControllerItem().setDeliveryPos(enderMailman.packageController, enderMailman.getDeliveryPos());
								enderMailman.getPackageControllerItem().setShowLockerLocation(enderMailman.packageController, !ConfigHandler.GENERAL.hideLockerLocation.get());
							}
							if (ConfigHandler.GENERAL.logDeliveries.get()) {
								EnderMail.logger.info("Delivered package to locker " + lockerTe.getLockerID() + " at " + enderMailman.getDeliveryPos().getX() + ", " + enderMailman.getDeliveryPos().getY() + ", " + enderMailman.getDeliveryPos().getZ());
							}
							delivered = true;
						} else {
							int y = -1;
							List<Direction> directions = new ArrayList<Direction>(Arrays.asList(Direction.NORTH, Direction.EAST, Direction.WEST, Direction.SOUTH));
							while (y < 0 && !directions.isEmpty()) {
								Direction randomDirection = directions.get(new Random().nextInt(directions.size()));
								directions.remove(randomDirection);
								BlockPos newDeliveryPos = enderMailman.getDeliveryPos().offset(randomDirection);
								y = enderMailman.findValidDeliveryHeight(newDeliveryPos, 8);
								if (y > 0) {
									newDeliveryPos = new BlockPos(newDeliveryPos.getX(), y, newDeliveryPos.getZ());
									enderMailman.world.setBlockState(newDeliveryPos, EnderMailBlocks.PACKAGE.getRandomlyRotatedStampedState(), 3);
									enderMailman.world.setTileEntity(newDeliveryPos, new PackageTileEntity(enderMailman.getContents()));
									if (enderMailman.hasPackageController()) {
										enderMailman.getPackageControllerItem().setState(enderMailman.packageController, ControllerState.DELIVERED);
										enderMailman.getPackageControllerItem().setDeliveryPos(enderMailman.packageController, newDeliveryPos);
									}
									if (ConfigHandler.GENERAL.logDeliveries.get()) {
										EnderMail.logger.info("Delivered package to " + newDeliveryPos.getX() + ", " + newDeliveryPos.getY() + ", " + newDeliveryPos.getZ() + " near locker " + lockerTe.getLockerID());
									}
									delivered = true;
								}
							}
						}
					}
				}
				if (!delivered) {
					enderMailman.teleportToStartingPos();
					enderMailman.world.setBlockState(enderMailman.getStartingPos(), EnderMailBlocks.PACKAGE.getRandomlyRotatedStampedState(), 3);
					enderMailman.world.setTileEntity(enderMailman.getStartingPos(), new PackageTileEntity(enderMailman.getContents()));
					if (enderMailman.hasPackageController()) {
						enderMailman.getPackageControllerItem().setState(enderMailman.packageController, ControllerState.UNDELIVERABLE);
					}
				}

				enderMailman.updateTimeDelivered();
				enderMailman.setContents(NonNullList.<ItemStack>withSize(PackageBlock.INVENTORY_SIZE, ItemStack.EMPTY));
				enderMailman.setCarryingPackage(false);
				enderMailman.setDelivering(false);
			} else if ((enderMailman.ticksExisted - enderMailman.getTimePickedUp()) % 20 == 0) {
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
		public boolean shouldExecute() {
			return enderMailman.isDelivering() && !enderMailman.isCarryingPackage();
		}

		@Override
		public void tick() {
			TileEntity tileEntity = enderMailman.world.getTileEntity(enderMailman.startingPos);
			if (tileEntity != null && tileEntity instanceof PackageTileEntity) {
				PackageTileEntity tileEntityPackage = (PackageTileEntity) tileEntity;
				enderMailman.setContents(tileEntityPackage.getContents());
				enderMailman.setCarryingPackage(true);
				enderMailman.world.setBlockState(enderMailman.startingPos, Blocks.AIR.getDefaultState());
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
		public boolean shouldExecute() {
			return !enderMailman.isDelivering();
		}

		@Override
		public void tick() {
			if (enderMailman.ticksExisted - enderMailman.getTimeDelivered() >= 100) {
				enderMailman.diePeacefully();
			}
		}
	}

}
