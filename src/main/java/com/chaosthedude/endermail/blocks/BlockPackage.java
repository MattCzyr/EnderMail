package com.chaosthedude.endermail.blocks;

import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.blocks.te.TileEntityPackage;
import com.chaosthedude.endermail.gui.GuiHandler;
import com.chaosthedude.endermail.items.ItemPackageController;
import com.chaosthedude.endermail.registry.EnderMailBlocks;
import com.chaosthedude.endermail.registry.EnderMailItems;
import com.chaosthedude.endermail.util.EnumControllerState;
import com.chaosthedude.endermail.util.ItemUtils;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPackage extends BlockContainer {

	public static final String DEFAULT_NAME = "package";
	public static final String STAMPED_NAME = "stamped_package";
	
	public static final int SIZE = 5;

	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	private static boolean keepInventory;

	private boolean isStamped;

	public BlockPackage(boolean isStamped) {
		super(Material.WOOD);
		if (isStamped) {
			setUnlocalizedName(EnderMail.MODID + "." + STAMPED_NAME);
		} else {
			setUnlocalizedName(EnderMail.MODID + "." + DEFAULT_NAME);
		}
		setCreativeTab(CreativeTabs.DECORATIONS);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		
		this.isStamped = isStamped;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!isStamped && ItemUtils.isHolding(player, EnderMailItems.stamp)) {
			player.openGui(EnderMail.instance, GuiHandler.STAMP_ID, world, pos.getX(), pos.getY(), pos.getZ());
		} else if (!isStamped) {
			player.openGui(EnderMail.instance, GuiHandler.PACKAGE_ID, world, pos.getX(), pos.getY(), pos.getZ());
		} else if (isStamped && player.isSneaking()) {
			setState(false, world, pos);
		} else if (isStamped && ItemUtils.isHolding(player, EnderMailItems.packageController)) {
			ItemStack stack = ItemUtils.getHeldItem(player, EnderMailItems.packageController);
			ItemPackageController packageController = (ItemPackageController) stack.getItem();
			BlockPos deliveryPos = getDeliveryPos(world, pos);
			if (deliveryPos != null) {
				packageController.setDeliveryPos(stack, pos);
				packageController.setState(stack, EnumControllerState.DELIVERING);
			}
		}

		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		if (stack.hasDisplayName()) {
			TileEntity tileentity = world.getTileEntity(pos);

			if (tileentity instanceof TileEntityFurnace) {
				((TileEntityFurnace) tileentity).setCustomInventoryName(stack.getDisplayName());
			}
		}
	}

	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te,
			ItemStack stack) {
		player.addStat(StatList.getBlockStats(this));
		player.addExhaustion(0.005F);

		if (te != null && te instanceof TileEntityPackage) {
			TileEntityPackage tileEntityPackage = (TileEntityPackage) te;
			ItemStack itemstack = new ItemStack(Item.getItemFromBlock(this));
			NBTTagCompound tag1 = new NBTTagCompound();
			NBTTagCompound tag2 = new NBTTagCompound();
			tag1.setTag("BlockEntityTag", ((TileEntityPackage) te).saveToNBT(tag2));
			itemstack.setTagCompound(tag1);

			if (tileEntityPackage.hasCustomName()) {
				itemstack.setStackDisplayName(tileEntityPackage.getName());
				tileEntityPackage.setCustomName("");
			}

			spawnAsEntity(world, pos, itemstack);

			world.updateComparatorOutputLevel(pos, state.getBlock());
		}
	}

	@Override
	public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
		ItemStack stack = super.getItem(world, pos, state);
		TileEntityPackage tileEntityPackage = (TileEntityPackage) world.getTileEntity(pos);
		NBTTagCompound tag = tileEntityPackage.saveToNBT(new NBTTagCompound());
		if (!tag.hasNoTags()) {
			stack.setTagInfo("BlockEntityTag", tag);
		}

		return stack;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
		super.addInformation(stack, player, tooltip, advanced);
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			NBTTagCompound temp = stack.getTagCompound();
			if (temp != null && temp.hasKey("BlockEntityTag", 10)) {
				NBTTagCompound tag = temp.getCompoundTag("BlockEntityTag");
				if (tag.hasKey("Items", 9)) {
					NonNullList<ItemStack> content = NonNullList.<ItemStack> withSize(SIZE, ItemStack.EMPTY);
					ItemStackHelper.loadAllItems(tag, content);
					for (ItemStack contentStack : content) {
						if (!contentStack.isEmpty()) {
							tooltip.add(String.format("%s x%d", new Object[] { contentStack.getDisplayName(),
									Integer.valueOf(contentStack.getCount()) }));
						}
					}
				}
			}
		} else {
			tooltip.add(I18n.format("string.endermail.holdShift"));
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if (!keepInventory) {
			TileEntity tileentity = world.getTileEntity(pos);
			if (tileentity instanceof TileEntityPackage) {
				InventoryHelper.dropInventoryItems(world, pos, (TileEntityPackage) tileentity);
			}
		}

		super.breakBlock(world, pos, state);
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		setDefaultFacing(world, pos, state);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getFront(meta);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumFacing) state.getValue(FACING)).getIndex();
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING });
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityPackage();
	}
	
	public static void stampPackage(World world, BlockPos packagePos, BlockPos deliveryPos) {
		//deliveryPos = pos;
		setState(true, world, packagePos);
		TileEntity te = world.getTileEntity(packagePos);
		if (te != null && te instanceof TileEntityPackage) {
			TileEntityPackage tePackage = (TileEntityPackage) te;
			tePackage.setDeliveryPos(deliveryPos);
		}
	}
	
	public static BlockPos getDeliveryPos(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te != null && te instanceof TileEntityPackage) {
			TileEntityPackage tePackage = (TileEntityPackage) te;
			return tePackage.getDeliveryPos();
		}
		
		return null;
	}

	public static void setState(boolean flag, World world, BlockPos pos) {
		//packingState = state;

		IBlockState iblockstate = world.getBlockState(pos);
		TileEntity tileentity = world.getTileEntity(pos);
		keepInventory = true;

		if (flag) {
			world.setBlockState(pos,
					EnderMailBlocks.stamped_package.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)),
					3);
			world.setBlockState(pos,
					EnderMailBlocks.stamped_package.getDefaultState().withProperty(FACING, iblockstate.getValue(FACING)),
					3);
		} else {
			world.setBlockState(pos, EnderMailBlocks.default_package.getDefaultState().withProperty(FACING,
					iblockstate.getValue(FACING)), 3);
			world.setBlockState(pos, EnderMailBlocks.default_package.getDefaultState().withProperty(FACING,
					iblockstate.getValue(FACING)), 3);
		}

		keepInventory = false;

		if (tileentity != null) {
			tileentity.validate();
			world.setTileEntity(pos, tileentity);
		}
	}

	private void setDefaultFacing(World world, BlockPos pos, IBlockState state) {
		if (!world.isRemote) {
			IBlockState iblockstate = world.getBlockState(pos.north());
			IBlockState iblockstate1 = world.getBlockState(pos.south());
			IBlockState iblockstate2 = world.getBlockState(pos.west());
			IBlockState iblockstate3 = world.getBlockState(pos.east());
			EnumFacing enumfacing = (EnumFacing) state.getValue(FACING);

			if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock()) {
				enumfacing = EnumFacing.SOUTH;
			} else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock()) {
				enumfacing = EnumFacing.NORTH;
			} else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock()) {
				enumfacing = EnumFacing.EAST;
			} else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock()) {
				enumfacing = EnumFacing.WEST;
			}

			world.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
		}
	}

}
