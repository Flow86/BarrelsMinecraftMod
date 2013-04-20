package need4speed402.mods.barrels;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

public class TileEntityBarrel extends TileEntity implements ISidedInventory {
	private ItemStack item;
	private String overlay = "";
	private byte doubleClickTimer = -1;
	private byte selectedSlot;
	private ItemStack lastStack = null;
	private int lastStackCount = 0;
	private byte side = 0;
	private int lastDecreased = 0;

	protected byte getSide() {
		return this.side;
	}

	public void setSide(byte var1) {
		this.side = var1;
	}

	public ItemStack getItem() {
		return this.item;
	}

	public void setItem(ItemStack var1) {
		this.item = var1;
	}

	/**
	 * validates a tile entity
	 */
	public void validate() {
		super.validate();
		PacketHandler.instance.sendServerRequest(this);
	}

	public int getInventorySize() {
		return this.getStackLimit() * this.getItem().getMaxStackSize();
	}

	public int getStackLimit() {
		int var1 = this.getBlockMetadata();
		return var1 == 0 ? Barrels.instance.T1BarrelMaxStorage : (var1 == 1 ? Barrels.instance.T2BarrelMaxStorage
				: (var1 == 2 ? Barrels.instance.T3BarrelMaxStorage : 0));
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	public int getSizeInventory() {
		return 3;
	}

	/**
	 * Allows the entity to update its state. Overridden in most subclasses,
	 * e.g. the mob spawner uses this to count ticks and creates a new spawn
	 * inside its implementation.
	 */
	public void updateEntity() {
		if (this.isTimerActive()) {
			--this.doubleClickTimer;
		}

		boolean var1 = false;

		if (this.lastStack != null) {
			if (this.getItem() != null && this.lastStack.stackSize != this.lastStackCount) {
				ItemStack var10000 = this.getItem();
				var10000.stackSize += this.lastStack.stackSize - this.lastStackCount;

				if (this.getItem().stackSize <= 0) {
					this.setItem((ItemStack) null);
				}

				var1 = true;
			}

			this.lastStack = null;
		}

		if (this.getItem() != null && this.lastDecreased != 0) {
			this.lastDecreased = 0;

			if (this.getItem().stackSize <= 0) {
				this.setItem((ItemStack) null);
			}

			var1 = true;
		}

		if (var1) {
			this.onInventoryChanged();
		}
	}

	/**
	 * Called when an the contents of an Inventory change, usually
	 */
	public void onInventoryChanged() {
		if (this.worldObj.isRemote) {
			this.overlay = "";

			if (this.getItem() == null) {
				return;
			}

			int var1 = (int) Math.ceil((double) (this.getItem().stackSize / this.getItem().getMaxStackSize()));

			if (var1 > 0 && this.getItem().getMaxStackSize() != 1) {
				this.overlay = var1 == 1 ? Integer.toString(this.getItem().getMaxStackSize()) : Integer.toString(var1) + " x "
						+ Integer.toString(this.getItem().getMaxStackSize());
			} else if (this.getItem().getMaxStackSize() == 1) {
				this.overlay = Integer.toString(var1);
			}

			int var2 = this.getItem().stackSize % this.getItem().getMaxStackSize();

			if (var2 > 0) {
				this.overlay = this.overlay + (var1 > 0 ? " + " + Integer.toString(var2) : Integer.toString(var2));
			}
		} else {
			PacketHandler.instance.sendClientsItem(this);
		}
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	public void writeToNBT(NBTTagCompound var1) {
		if (this.getItem() != null) {
			ItemStack var2 = this.getItem().copy();

			if (this.lastStack != null && var2 != null && this.lastStack.stackSize != this.lastStackCount) {
				var2.stackSize += this.lastStack.stackSize - this.lastStackCount;

				if (var2.stackSize <= 0) {
					var2 = null;
				}
			}

			if (var2 != null && this.lastDecreased != 0 && var2.stackSize <= 0) {
				var2 = null;
			}

			if (var2 != null) {
				var1.setInteger("item", var2.itemID);
				var1.setInteger("size", var2.stackSize);
				var1.setInteger("metadata", var2.getItemDamage());

				if (var2.getTagCompound() != null) {
					var1.setCompoundTag("tag", var2.getTagCompound());
				}
			}
		}

		var1.setByte("side", this.getSide());
		super.writeToNBT(var1);
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	public void readFromNBT(NBTTagCompound var1) {
		if (var1.hasKey("item")) {
			this.setItem(new ItemStack(var1.getInteger("item"), var1.getInteger("size"), var1.getInteger("metadata")));

			if (var1.hasKey("tag")) {
				this.getItem().setTagCompound(var1.getCompoundTag("tag"));
			}

			if (this.getItem().itemID == -1) {
				this.setItem((ItemStack) null);
			}
		} else {
			this.setItem((ItemStack) null);
		}

		this.setSide(var1.getByte("side"));
		super.readFromNBT(var1);
	}

	public void setClick(byte var1) {
		this.selectedSlot = var1;
		this.doubleClickTimer = 10;
	}

	public int getSelectedSlot() {
		return this.selectedSlot;
	}

	public void resetTimer() {
		this.doubleClickTimer = -1;
	}

	public boolean isTimerActive() {
		return this.doubleClickTimer > -1;
	}

	public String getOverlay() {
		return this.overlay;
	}

	/**
	 * Returns the stack in slot i
	 */
	public ItemStack getStackInSlot(int var1) {
		if (this.item != null) {
			ItemStack var2 = this.getItem().copy();
			var2.stackSize = this.getStackSizeInSlot(var1);

			if (this.lastStack != null) {
				if (this.lastStack.stackSize != this.lastStackCount) {
					ItemStack var10000 = this.getItem();
					var10000.stackSize += this.lastStack.stackSize - this.lastStackCount;
					this.onInventoryChanged();
				}

				if (this.getItem().stackSize <= 0) {
					this.setItem((ItemStack) null);
				}

				this.lastStack = null;
			}

			this.lastStackCount = var2.stackSize;
			this.lastStack = var2;
			return var2;
		} else {
			return null;
		}
	}

	private int getStackSizeInSlot(int var1) {
		if (this.getItem() != null) {
			switch (var1) {
			case 0:
				return Math.min(this.getItem().getMaxStackSize(), this.getItem().stackSize);

			case 1:
				return this.getItem().stackSize - this.getStackSizeInSlot(0);

			case 2:
				return Math.max(this.getItem().getMaxStackSize() + (this.getItem().stackSize - this.getInventorySize()), 0);
			}
		}

		return 0;
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be
	 * crafting or armor sections).
	 */
	public void setInventorySlotContents(int var1, ItemStack var2) {
		ItemStack var10000;

		if (this.getItem() != null && this.lastDecreased != 0) {
			var10000 = this.getItem();
			var10000.stackSize += this.lastDecreased;
		}

		if (this.getItem() == null && var2 != null) {
			this.setItem(var2);
			this.onInventoryChanged();
		} else {
			int var3 = this.getStackSizeInSlot(var1);
			int var4 = this.getItem().stackSize;

			if (var2 == null) {
				if (this.getItem().stackSize < var3) {
					this.setItem((ItemStack) null);
				} else {
					var10000 = this.getItem();
					var10000.stackSize -= var3;
				}
			} else {
				var10000 = this.getItem();
				var10000.stackSize += var2.stackSize - var3;
			}

			if (this.getItem().stackSize != var4) {
				this.onInventoryChanged();
			}
		}

		this.lastDecreased = 0;
		this.lastStack = null;
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number
	 * (second arg) of items and returns them in a new stack.
	 */
	public ItemStack decrStackSize(int var1, int var2) {
		if (var1 != 2 && this.getStackSizeInSlot(var1) != 0 && this.getItem() != null) {
			ItemStack var3 = this.getItem().copy();

			if (this.getItem().stackSize > var2) {
				var3.stackSize = var2;
			}

			int var4 = this.lastDecreased;

			if (this.lastDecreased != -1) {
				this.lastDecreased += var2;
			} else {
				this.lastDecreased = var2;
			}

			ItemStack var10000 = this.getItem();
			var10000.stackSize -= this.lastDecreased - var4;
			return var3;
		} else {
			return null;
		}
	}

	/**
	 * Do not make give this method the name canInteractWith because it clashes
	 * with Container
	 */
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return false;
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be
	 * 64, possibly will be extended. *Isn't this more of a set than a get?*
	 */
	public int getInventoryStackLimit() {
		return 64;
	}

	public void openChest() {
	}

	public void closeChest() {
	}

	/**
	 * When some containers are closed they call this on each slot, then drop
	 * whatever it returns as an EntityItem - like when you close a workbench
	 * GUI.
	 */
	public ItemStack getStackInSlotOnClosing(int var1) {
		return null;
	}

	/**
	 * Returns the name of the inventory.
	 */
	public String getInvName() {
		return "container.barrel";
	}

	public int getStartInventorySide(ForgeDirection var1) {
		switch (this.getModeForSide(var1)) {
		case 1:
			return 2;

		case 2:
		case 3:
			return 0;

		default:
			return -1;
		}
	}

	public int getSizeInventorySide(ForgeDirection var1) {
		return this.getModeForSide(var1);
	}

	public byte getModeForSide(ForgeDirection var1) {
		return var1 == ForgeDirection.UP ? this.getMode(0) : (var1 == ForgeDirection.DOWN ? this.getMode(1)
				: (var1 == ForgeDirection.NORTH ? (this.getSide() == 0 ? this.getMode(2) : (this.getSide() == 1 ? this.getMode(4) : (this.getSide() == 2 ? this
						.getMode(3) : this.getMode(5)))) : (var1 == ForgeDirection.SOUTH ? (this.getSide() == 0 ? this.getMode(3) : (this.getSide() == 1 ? this
						.getMode(5) : (this.getSide() == 2 ? this.getMode(2) : this.getMode(4)))) : (var1 == ForgeDirection.EAST ? (this.getSide() == 0 ? this
						.getMode(5) : (this.getSide() == 1 ? this.getMode(2) : (this.getSide() == 2 ? this.getMode(4) : this.getMode(3))))
						: (var1 == ForgeDirection.WEST ? (this.getSide() == 0 ? this.getMode(4) : (this.getSide() == 1 ? this.getMode(3)
								: (this.getSide() == 2 ? this.getMode(5) : this.getMode(2)))) : 0)))));
	}

	private byte getMode(int var1) {
		String[] var2 = Barrels.instance.interaction.split(";");
		return (byte) (var2[var1].equals("in") ? 1 : (var2[var1].equals("out") ? 2 : (var2[var1].equals("in/out") ? 3 : 0)));
	}
}
