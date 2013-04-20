package need4speed402.mods.barrels;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBarrel extends ItemBlock {
	public ItemBarrel(int var1) {
		super(var1);
		this.setHasSubtypes(true);
		this.setItemName("barrel");
	}

	/**
	 * Returns the metadata of the block which this Item (ItemBlock) can place
	 */
	public int getMetadata(int var1) {
		return var1;
	}

	public String getItemNameIS(ItemStack var1) {
		return "T" + Integer.toString(var1.getItemDamage()) + "barrel";
	}
}
