package need4speed402.mods.barrels;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockBarrel extends Block
{
    private Random random = new Random();

    public BlockBarrel(int var1)
    {
        super(var1, Material.wood);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setStepSound(Block.soundWoodFootstep);
        this.setHardness(2.0F);
        this.setBlockName("barrel");
        setBurnProperties(this.blockID, 0, 1);
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public int getBlockTextureFromSideAndMetadata(int var1, int var2)
    {
        return var1 != 0 && var1 != 1 ? var2 * 16 + 1 : var2 * 16;
    }

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World var1, int var2, int var3, int var4, EntityLiving var5)
    {
        TileEntityBarrel var6 = (TileEntityBarrel)var1.getBlockTileEntity(var2, var3, var4);
        var6.setSide((byte)(Math.round(var5.rotationYaw / 90.0F) & 3));
    }

    public static boolean equals(ItemStack var0, ItemStack var1)
    {
        return var0 == var1 ? true : (var0 != null && var1 != null ? (!var0.isItemEqual(var1) ? false : (var0.getTagCompound() == var1.getTagCompound() ? true : (var0.getTagCompound() != null && var1.getTagCompound() != null ? Arrays.equals(var0.getTagCompound().getTags().toArray(), var1.getTagCompound().getTags().toArray()) : false))) : false);
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9)
    {
        TileEntityBarrel var10 = (TileEntityBarrel)var1.getBlockTileEntity(var2, var3, var4);
        ItemStack var11 = var5.inventory.mainInventory[var5.inventory.currentItem] == null ? null : var5.inventory.mainInventory[var5.inventory.currentItem].copy();

        if (var10 != null)
        {
            if (var10.isTimerActive())
            {
                var10.resetTimer();

                if (var10.getItem() != null)
                {
                    int var12;

                    if (var10.getSelectedSlot() == -var5.inventory.currentItem - 1)
                    {
                        if (var10.getItem().getItemDamage() > var10.getBlockMetadata() || var11 != null && var11.getItemDamage() > var10.getBlockMetadata())
                        {
                            var12 = var10.getBlockMetadata();
                            int var13 = -1;

                            if (var11 == null)
                            {
                                var13 = var10.getItem().getItemDamage();

                                if (var10.getItem().stackSize == 1)
                                {
                                    var10.setItem((ItemStack)null);
                                }
                                else
                                {
                                    --var10.getItem().stackSize;
                                }
                            }
                            else if (var11.stackSize == 1)
                            {
                                var13 = var11.getItemDamage();
                            }

                            if (var13 != -1)
                            {
                                var1.setBlockMetadata(var2, var3, var4, var13);

                                if (var1.isRemote)
                                {
                                    var1.markBlockForRenderUpdate(var2, var3, var4);
                                }
                                else
                                {
                                    PacketHandler.instance.sendClientBlockChange(var10);
                                }

                                var10.blockMetadata = var13;
                                var11 = new ItemStack(Barrels.instance.barrel, 1, var12);
                            }
                        }
                    }
                    else
                    {
                        if (var10.getSelectedSlot() == var5.inventory.currentItem + 1 && (var11 == null || equals(var11, var10.getItem())))
                        {
                            for (var12 = 0; var12 < var5.inventory.mainInventory.length; ++var12)
                            {
                                var5.inventory.mainInventory[var12] = this.addItem(var10, var5.inventory.mainInventory[var12]);
                            }

                            var5.inventory.onInventoryChanged();
                            var10.onInventoryChanged();
                            return true;
                        }

                        var11 = this.addItem(var10, var11);
                    }
                }
            }
            else if (var11 != null)
            {
                if (var11.itemID == Barrels.instance.barrel.blockID && (var10.getItem() != null && var10.getItem().getItemDamage() > var10.getBlockMetadata() || var11 != null && var11.getItemDamage() > var10.getBlockMetadata()))
                {
                    var10.setClick((byte)(-var5.inventory.currentItem - 1));
                }
                else if (var10.getItem() == null || equals(var11, var10.getItem()))
                {
                    var10.setClick((byte)(var5.inventory.currentItem + 1));
                }

                var11 = this.addItem(var10, var11);
            }

            if (var11 != null && var11.stackSize <= 0)
            {
                var11 = null;
            }

            var5.inventory.mainInventory[var5.inventory.currentItem] = var11;
            var5.inventory.onInventoryChanged();
            var10.onInventoryChanged();
        }

        return true;
    }

    public ItemStack addItem(TileEntityBarrel var1, ItemStack var2)
    {
        if (var2 != null)
        {
            var2 = var2.copy();

            if (var1.getItem() == null)
            {
                var1.setItem(var2.copy());
                return null;
            }
            else
            {
                if (equals(var2, var1.getItem()))
                {
                    ItemStack var10000 = var1.getItem();
                    var10000.stackSize += var2.stackSize;
                    var2.stackSize = 0;
                }

                if (var1.getItem().stackSize > var1.getInventorySize())
                {
                    var2.stackSize += var1.getItem().stackSize - var1.getInventorySize();
                    var1.getItem().stackSize = var1.getInventorySize();
                }

                if (var2.stackSize <= 0)
                {
                    var2 = null;
                }

                return var2;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Called when the block is clicked by a player. Args: x, y, z, entityPlayer
     */
    public void onBlockClicked(World var1, int var2, int var3, int var4, EntityPlayer var5)
    {
        TileEntityBarrel var6 = (TileEntityBarrel)var1.getBlockTileEntity(var2, var3, var4);
        var6.resetTimer();

        if (var6.getItem() != null)
        {
            if (var6.getItem().stackSize <= 0)
            {
                var6.setItem((ItemStack)null);
                var6.onInventoryChanged();
            }
            else
            {
                ItemStack var7;

                if (var5.isSneaking())
                {
                    var7 = var6.getItem().copy();
                    var7.stackSize = 1;
                    this.spawnItemInWorld(var1, var5.posX, var5.posY, var5.posZ, var7, false);
                    --var6.getItem().stackSize;
                }
                else if (var6.getItem().stackSize > var6.getItem().getMaxStackSize())
                {
                    var7 = var6.getItem().copy();
                    var7.stackSize = var7.getMaxStackSize();
                    this.spawnItemInWorld(var1, var5.posX, var5.posY, var5.posZ, var7, false);
                    ItemStack var10000 = var6.getItem();
                    var10000.stackSize -= var7.stackSize;
                }
                else if (var6.getItem().stackSize != 1 && Barrels.instance.leaveOneItem)
                {
                    var7 = var6.getItem().copy();
                    --var7.stackSize;
                    this.spawnItemInWorld(var1, var5.posX, var5.posY, var5.posZ, var7, false);
                    var6.getItem().stackSize = 1;
                }
                else
                {
                    this.spawnItemInWorld(var1, var5.posX, var5.posY, var5.posZ, var6.getItem(), false);
                    var6.setItem((ItemStack)null);
                }

                if (var6.getItem() != null && var6.getItem().stackSize <= 0)
                {
                    var6.setItem((ItemStack)null);
                }

                var6.onInventoryChanged();
            }
        }
    }

    public boolean hasTileEntity(int var1)
    {
        return true;
    }

    /**
     * ejects contained items into the world, and notifies neighbours of an update, as appropriate
     */
    public void breakBlock(World var1, int var2, int var3, int var4, int var5, int var6)
    {
        TileEntityBarrel var7 = (TileEntityBarrel)var1.getBlockTileEntity(var2, var3, var4);

        if (var7.getItem() != null)
        {
            this.spawnItemInWorld(var1, (double)var2, (double)var3, (double)var4, var7.getItem().copy(), true);
        }

        var1.removeBlockTileEntity(var2, var3, var4);
    }

    private void spawnItemInWorld(World var1, double var2, double var4, double var6, ItemStack var8, boolean var9)
    {
        if (!var1.isRemote)
        {
            int var10 = (int)Math.ceil((double)(var8.stackSize / var8.getMaxStackSize()));
            int var11 = var10 < 64 ? var10 : 64;
            int var12 = var10 == 0 ? 0 : (int)Math.ceil((double)(var8.stackSize / var11));
            int var13 = var10 == 0 ? var8.stackSize : var8.stackSize % var11;
            var8.stackSize = var12;

            for (int var14 = 0; var14 < var11; ++var14)
            {
                EntityItem var15 = null;

                if (var9)
                {
                    var15 = new EntityItem(var1, var2 + 0.5D, var4 + 0.5D, var6 + 0.5D, var8.copy());
                    var15.motionX = (double)(this.random.nextInt(99) - 49) * 0.0015D;
                    var15.motionZ = (double)(this.random.nextInt(99) - 49) * 0.0015D;
                    var15.delayBeforeCanPickup = 10;
                }
                else
                {
                    var15 = new EntityItem(var1, var2, var4 + 0.5D, var6, var8.copy());
                    var15.motionX = 0.0D;
                    var15.motionZ = 0.0D;
                }

                var1.spawnEntityInWorld(var15);
            }

            if (var13 != 0)
            {
                var8.stackSize = var13;
                EntityItem var16 = null;

                if (var9)
                {
                    var16 = new EntityItem(var1, var2 + 0.5D, var4 + 0.5D, var6 + 0.5D, var8.copy());
                    var16.motionX = (double)(this.random.nextInt(99) - 49) * 0.0015D;
                    var16.motionZ = (double)(this.random.nextInt(99) - 49) * 0.0015D;
                    var16.delayBeforeCanPickup = 10;
                }
                else
                {
                    var16 = new EntityItem(var1, var2, var4 + 0.5D, var6, var8.copy());
                    var16.motionX = 0.0D;
                    var16.motionZ = 0.0D;
                }

                var1.spawnEntityInWorld(var16);
            }
        }
    }

    public TileEntity createTileEntity(World var1, int var2)
    {
        return new TileEntityBarrel();
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    public int damageDropped(int var1)
    {
        return var1;
    }

    @SideOnly(Side.CLIENT)

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(int var1, CreativeTabs var2, List var3)
    {
        if (Barrels.instance.T1BarrelMaxStorage != -1)
        {
            var3.add(Barrels.instance.T1barrel);
        }

        if (Barrels.instance.T2BarrelMaxStorage != -1)
        {
            var3.add(Barrels.instance.T2barrel);
        }

        if (Barrels.instance.T3BarrelMaxStorage != -1)
        {
            var3.add(Barrels.instance.T3barrel);
        }
    }

    public String getTextureFile()
    {
        Barrels.instance.getClass();
        return "/need4speed402/mods/barrels/gfx/Barrels.png";
    }
}
