package com.chaosthedude.endermail.blocks;

import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import com.chaosthedude.endermail.EnderMail;
import com.chaosthedude.endermail.blocks.te.TileEntityPackage;
import com.chaosthedude.endermail.network.PacketSpawnMailman;
import com.chaosthedude.endermail.registry.EnderMailBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockStampedPackage extends Block implements ITileEntityProvider {

	public static final String NAME = "stamped_package";
	public static final int SIZE = 5;

	public BlockStampedPackage() {
		super(Material.WOOD);
		setUnlocalizedName(EnderMail.MODID + "." + NAME);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) {
			open(world, pos);
		}

		return true;
	}
	
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		TileEntity tileEntityStampedPackage = (TileEntityPackage) world.getTileEntity(pos);
		NBTTagCompound tag = new NBTTagCompound();
		tileEntityStampedPackage.writeToNBT(tag);
		BlockPos deliveryPos = new BlockPos(tag.getInteger("DeliveryX"), tag.getInteger("DeliveryY"), tag.getInteger("DeliveryZ"));
		EnderMail.network.sendToServer(new PacketSpawnMailman(pos, deliveryPos));

		super.onBlockAdded(world, pos, state);
	}
	
	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
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
							tooltip.add(String.format("%s x%d", new Object[] { contentStack.getDisplayName(), Integer.valueOf(contentStack.getCount()) }));
						}
					}
				}
			}
		} else {
			tooltip.add(I18n.format("string.endermail.holdShift"));
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityPackage();
	}
	
	public static void open(World world, BlockPos pos) {
		IBlockState iblockstate = world.getBlockState(pos);
		TileEntity tileentity = world.getTileEntity(pos);
		world.setBlockState(pos, EnderMailBlocks.blockPackage.getDefaultState(), 3);
		if (tileentity != null) {
			tileentity.validate();
			world.setTileEntity(pos, tileentity);
		}
	}

}
