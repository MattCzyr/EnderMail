package com.chaosthedude.endermail.entity;

import java.util.ArrayList;
import java.util.List;

import com.chaosthedude.endermail.blocks.PackageBlock;
import com.chaosthedude.endermail.blocks.te.PackageTileEntity;
import com.chaosthedude.endermail.items.PackageControllerItem;
import com.chaosthedude.endermail.registry.EnderMailBlocks;
import com.chaosthedude.endermail.util.ControllerState;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
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
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

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

	public EnderMailmanEntity(EntityType<? extends EnderMailmanEntity> entityType, World world, BlockPos startingPos, BlockPos deliveryPos, ItemStack packageController) {
		super(entityType, world);
		this.packageController = packageController;
		setPosition(startingPos.getX() + getRandomOffset(), startingPos.getY(), startingPos.getZ() + getRandomOffset());
		setStartingPos(startingPos);
		int startY = deliveryPos.getY() <= 0 ? world.getHeight(Type.WORLD_SURFACE, deliveryPos.getX(), deliveryPos.getZ()) : deliveryPos.getY();
		int offset = 0;
		boolean negate = false;
		int y = startY;
		while (!(canPlacePackage(world, new BlockPos(deliveryPos.getX(), y, deliveryPos.getZ())))) {
			y = startY + offset;
			if (negate) {
				offset = -offset;
			} else {
				if (offset < 0) {
					offset--;
				} else {
					offset++;
				}
			}

			negate = !negate;

			if ((y + offset > 255 || y + offset < 0) && (y - offset > 255 || y - offset < 0)) {
				y = -1;
				break;
			}
		}

		setDeliveryPos(new BlockPos(deliveryPos.getX(), y, deliveryPos.getZ()));
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

	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
		getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
		getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
	}

	@Override
	public void livingTick() {
		if (world.isRemote) {
			for (int i = 0; i < 2; ++i) {
				world.addParticle(ParticleTypes.PORTAL, getPosX() + (this.rand.nextDouble() - 0.5D) * (double) this.getWidth(), getPosY() + this.rand.nextDouble() * (double) this.getHeight() - 0.25D,
						getPosZ() + (this.rand.nextDouble() - 0.5D) * (double) this.getWidth(), (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D);
			}

			if (ticksExisted - timeDelivered > 100) {
				kill();

			}
		}

		isJumping = false;
		super.livingTick();
	}

	public void kill() {
		attackEntityFrom(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
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

			if (f > 0.5F && world.canBlockSeeSky(new BlockPos(this)) && rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
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

	protected boolean teleportRandomly() {
		double x = getPosX() + (rand.nextDouble() - 0.5D) * 64.0D;
		double y = getPosY() + (double) (rand.nextInt(64) - 32);
		double z = getPosZ() + (rand.nextDouble() - 0.5D) * 64.0D;
		return teleportTo(x, y, z);
	}

	private boolean teleportTo(double x, double y, double z) {
		EnderTeleportEvent event = new EnderTeleportEvent(this, x, y, z, 0);
		if (MinecraftForge.EVENT_BUS.post(event)) {
			return false;
		}
		boolean canTeleport = attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), false);

		if (canTeleport) {
			world.playSound((PlayerEntity) null, prevPosX, prevPosY, prevPosZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, getSoundCategory(), 1.0F, 1.0F);
			playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
		}

		return canTeleport;
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

	private boolean canPlacePackage(World world, BlockPos pos) {
		return EnderMailBlocks.PACKAGE_BLOCK.getStampedState().isValidPosition(world, pos) && world.getBlockState(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())).isSolid();
	}

	public void setPackageController(ItemStack packageController) {
		this.packageController = packageController;
	}

	public PackageControllerItem getPackageController() {
		return (PackageControllerItem) packageController.getItem();
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
				if (EnderMailBlocks.PACKAGE_BLOCK.getStampedState().isValidPosition(enderMailman.world, enderMailman.getDeliveryPos())) {
					enderMailman.teleportToDeliveryPos();
					enderMailman.world.setBlockState(enderMailman.getDeliveryPos(), EnderMailBlocks.PACKAGE_BLOCK.getStampedState(), 3);
					enderMailman.world.setTileEntity(enderMailman.getDeliveryPos(), new PackageTileEntity(enderMailman.getContents()));
					enderMailman.setContents(NonNullList.<ItemStack>withSize(PackageBlock.INVENTORY_SIZE, ItemStack.EMPTY));
					enderMailman.getPackageController().setState(enderMailman.packageController, ControllerState.DELIVERED);
					enderMailman.getPackageController().setDeliveryPos(enderMailman.packageController, enderMailman.getDeliveryPos());
					enderMailman.teleportToStartingPos();
				} else {
					enderMailman.teleportToStartingPos();
					enderMailman.world.setBlockState(enderMailman.getStartingPos(), EnderMailBlocks.PACKAGE_BLOCK.getStampedState(), 3);
					enderMailman.world.setTileEntity(enderMailman.getStartingPos(), new PackageTileEntity(enderMailman.getContents()));
					enderMailman.setContents(NonNullList.<ItemStack>withSize(PackageBlock.INVENTORY_SIZE, ItemStack.EMPTY));
					enderMailman.getPackageController().setState(enderMailman.packageController, ControllerState.RETURNED);
				}

				enderMailman.updateTimeDelivered();
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
				enderMailman.getPackageController().setState(enderMailman.packageController, ControllerState.DELIVERING);
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
				enderMailman.kill();
			}
		}
	}

}
