package com.chaosthedude.endermail.entity;

import java.util.ArrayList;
import java.util.List;

import com.chaosthedude.endermail.blocks.BlockPackage;
import com.chaosthedude.endermail.blocks.te.TileEntityPackage;
import com.chaosthedude.endermail.items.ItemPackageController;
import com.chaosthedude.endermail.registry.EnderMailBlocks;
import com.chaosthedude.endermail.util.EnumControllerState;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class EntityEnderMailman extends EntityMob {
	
	public static String NAME = "ender_mailman";

	private NonNullList<ItemStack> contents = NonNullList.<ItemStack> withSize(BlockPackage.SIZE, ItemStack.EMPTY);
	private int lastCreepySound;
	private int timePickedUp;
	private int timeDelivered;
	private boolean isDelivering;
	private boolean isCarryingPackage;
	private BlockPos startingPos;
	private BlockPos deliveryPos;
	private ItemStack packageController;

	public EntityEnderMailman(World world) {
		super(world);
		setSize(0.6F, 2.9F);
		stepHeight = 1.0F;
		setPathPriority(PathNodeType.WATER, -1.0F);
	}

	public EntityEnderMailman(World world, BlockPos startingPos, BlockPos deliveryPos, ItemStack packageController) {
		this(world);
		this.packageController = packageController;
		setPosition(startingPos.getX() + getRandomOffset(), startingPos.getY(), startingPos.getZ() + getRandomOffset());
		setStartingPos(startingPos);
		int startY = deliveryPos.getY() < 0 ? world.getHeight(deliveryPos.getX(), deliveryPos.getZ()) : deliveryPos.getY();
		int offset = 0;
		boolean negate = false;
		int y = startY;
		while (!(canPlacePackage(world, new BlockPos(deliveryPos.getX(), y, deliveryPos.getZ())))) {
			y = startY + offset;
			System.out.println(y);
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
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D, 0.0F));
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(8, new EntityAILookIdle(this));
		tasks.addTask(10, new EntityEnderMailman.AIDeliver(this));
		tasks.addTask(11, new EntityEnderMailman.AITakePackage(this));
		tasks.addTask(12, new EntityEnderMailman.AIKill(this));
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
	}

	@Override
	public float getEyeHeight() {
		return 2.55F;
	}

	@Override
	public void onLivingUpdate() {
		if (world.isRemote) {
			for (int i = 0; i < 2; ++i) {
				world.spawnParticle(EnumParticleTypes.PORTAL, posX + (rand.nextDouble() - 0.5D) * (double) width, posY + rand.nextDouble() * (double) height - 0.25D, posZ + (rand.nextDouble() - 0.5D) * (double) width,
						(rand.nextDouble() - 0.5D) * 2.0D, -rand.nextDouble(), (rand.nextDouble() - 0.5D) * 2.0D, new int[0]);
			}

			if (ticksExisted - timeDelivered > 100) {
				setDead();
			}
		}

		isJumping = false;
		super.onLivingUpdate();
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (isEntityInvulnerable(source)) {
			return false;
		} else if (source instanceof EntityDamageSourceIndirect) {
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

			if (f > 0.5F && world.canSeeSky(new BlockPos(this)) && rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
				teleportRandomly();
			}
		}

		super.updateAITasks();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_ENDERMEN_AMBIENT;
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_ENDERMEN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ENDERMEN_DEATH;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		ItemStackHelper.saveAllItems(compound, contents);

		compound.setInteger("StartingX", startingPos.getX());
		compound.setInteger("StartingY", startingPos.getY());
		compound.setInteger("StartingZ", startingPos.getZ());

		compound.setInteger("DeliveryX", deliveryPos.getX());
		compound.setInteger("DeliveryY", deliveryPos.getY());
		compound.setInteger("DeliveryZ", deliveryPos.getZ());

		compound.setBoolean("IsDelivering", isDelivering);
		compound.setBoolean("IsCarryingPackage", isCarryingPackage);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		ItemStackHelper.loadAllItems(compound, contents);

		startingPos = new BlockPos(compound.getInteger("StartingX"), compound.getInteger("StartingY"), compound.getInteger("StartingZ"));
		deliveryPos = new BlockPos(compound.getInteger("DeliveryX"), compound.getInteger("DeliveryY"), compound.getInteger("DeliveryZ"));

		isDelivering = compound.getBoolean("IsDelivering");
		isCarryingPackage = compound.getBoolean("IsCarryingPackage");
	}

	@Override
	protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
		super.dropEquipment(wasRecentlyHit, lootingModifier);

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

	public double getDistanceToDelivery() {
		return getDistance(deliveryPos.getX(), deliveryPos.getY(), deliveryPos.getZ());
	}

	public double getDistanceToStart() {
		return getDistance(startingPos.getX(), startingPos.getY(), startingPos.getZ());
	}

	protected boolean teleportRandomly() {
		double x = posX + (rand.nextDouble() - 0.5D) * 64.0D;
		double y = posY + (double) (rand.nextInt(64) - 32);
		double z = posZ + (rand.nextDouble() - 0.5D) * 64.0D;
		return teleportTo(x, y, z);
	}

	private boolean teleportTo(double x, double y, double z) {
		EnderTeleportEvent event = new EnderTeleportEvent(this, x, y, z, 0);
		if (MinecraftForge.EVENT_BUS.post(event)) {
			return false;
		}
		boolean canTeleport = attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ());

		if (canTeleport) {
			world.playSound((EntityPlayer) null, prevPosX, prevPosY, prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, getSoundCategory(), 1.0F, 1.0F);
			playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
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

	public List<BlockPos> getNearbyPackages() {
		List<BlockPos> packages = new ArrayList<BlockPos>();
		for (int x = -5; x < 5; x++) {
			for (int y = -5; y < 5; y++) {
				for (int z = -5; z < 5; z++) {
					BlockPos pos = new BlockPos(getPosition().getX() + x, getPosition().getY() + y, getPosition().getZ() + z);
					if (world.getBlockState(new BlockPos(x, y, z)).getBlock() instanceof BlockPackage) {
						TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
						if (tileEntity != null && tileEntity instanceof TileEntityPackage) {
							packages.add(pos);
						}
					}
				}
			}
		}

		return packages;
	}

	private boolean canPlacePackage(World world, BlockPos pos) {
		return EnderMailBlocks.default_package.canPlaceBlockAt(world, pos) && world.isSideSolid(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ()), EnumFacing.UP);
	}

	public void setPackageController(ItemStack packageController) {
		this.packageController = packageController;
	}

	public ItemPackageController getPackageController() {
		return (ItemPackageController) packageController.getItem();
	}

	public boolean isCarryingPackage() {
		return isCarryingPackage;
	}

	public boolean isDelivering() {
		return isDelivering;
	}
	
	public void setCarryingPackage(boolean carrying) {
		isCarryingPackage = carrying;
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
				world.playSound(posX, posY + (double) getEyeHeight(), posZ, SoundEvents.ENTITY_ENDERMEN_STARE, getSoundCategory(), 2.5F, 1.0F, false);
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

	static class AIDeliver extends EntityAIBase {
		private final EntityEnderMailman enderMailman;

		public AIDeliver(EntityEnderMailman enderMailman) {
			this.enderMailman = enderMailman;
		}

		@Override
		public boolean shouldExecute() {
			return enderMailman.isDelivering() && enderMailman.isCarryingPackage();
		}

		@Override
		public void updateTask() {
			if (enderMailman.ticksExisted - enderMailman.getTimePickedUp() >= 100) {
				if (EnderMailBlocks.stamped_package.canPlaceBlockAt(enderMailman.world, enderMailman.getDeliveryPos())) {
					enderMailman.teleportToDeliveryPos();
					enderMailman.world.setBlockState(enderMailman.getDeliveryPos(), EnderMailBlocks.stamped_package.getDefaultState(), 3);
					enderMailman.world.setTileEntity(enderMailman.getDeliveryPos(), new TileEntityPackage(enderMailman.getContents()));
					enderMailman.setContents(NonNullList.<ItemStack> withSize(BlockPackage.SIZE, ItemStack.EMPTY));
					enderMailman.getPackageController().setState(enderMailman.packageController, EnumControllerState.SUCCESS);
					enderMailman.getPackageController().setDeliveryPos(enderMailman.packageController, enderMailman.getDeliveryPos());
					enderMailman.teleportToStartingPos();
				} else {
					enderMailman.teleportToStartingPos();
					enderMailman.world.setBlockState(enderMailman.getStartingPos(), EnderMailBlocks.stamped_package.getDefaultState(), 3);
					enderMailman.world.setTileEntity(enderMailman.getStartingPos(), new TileEntityPackage(enderMailman.getContents()));
					enderMailman.setContents(NonNullList.<ItemStack> withSize(BlockPackage.SIZE, ItemStack.EMPTY));
					enderMailman.getPackageController().setState(enderMailman.packageController, EnumControllerState.FAILURE);
				}

				enderMailman.updateTimeDelivered();
				enderMailman.setDelivering(false);
			} else if ((enderMailman.ticksExisted - enderMailman.getTimePickedUp()) % 20 == 0) {
				enderMailman.teleportRandomly();
			}
		}
	}

	static class AITakePackage extends EntityAIBase {
		private final EntityEnderMailman enderMailman;

		public AITakePackage(EntityEnderMailman enderMailman) {
			this.enderMailman = enderMailman;
		}

		@Override
		public boolean shouldExecute() {
			return enderMailman.isDelivering() && !enderMailman.isCarryingPackage();
		}

		@Override
		public void updateTask() {
			TileEntity tileEntity = enderMailman.world.getTileEntity(enderMailman.startingPos);
			if (tileEntity != null && tileEntity instanceof TileEntityPackage) {
				TileEntityPackage tileEntityPackage = (TileEntityPackage) tileEntity;
				enderMailman.setContents(tileEntityPackage.getContents());
				enderMailman.setCarryingPackage(true);
				enderMailman.world.setBlockToAir(enderMailman.startingPos);
				enderMailman.getPackageController().setState(enderMailman.packageController, EnumControllerState.DELIVERING);
				enderMailman.updateTimePickedUp();
			} else {
				enderMailman.setDelivering(false);
			}
		}
	}

	static class AIKill extends EntityAIBase {
		private final EntityEnderMailman enderMailman;

		public AIKill(EntityEnderMailman enderMailman) {
			this.enderMailman = enderMailman;
		}

		@Override
		public boolean shouldExecute() {
			return !enderMailman.isDelivering();
		}

		@Override
		public void updateTask() {
			if (enderMailman.ticksExisted - enderMailman.getTimeDelivered() >= 100) {
				enderMailman.setDead();
			}
		}
	}

}
