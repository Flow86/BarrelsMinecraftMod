package need4speed402.mods.barrels;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer$ItemRenderType;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class BarrelRender extends TileEntitySpecialRenderer
{
    public static BarrelRender instance = new BarrelRender();
    private RenderBlocks blockRender = new RenderBlocks();
    Tessellator tessellator;

    public BarrelRender()
    {
        this.tessellator = Tessellator.instance;
    }

    private void renderText(String var1, int var2, double var3, double var5, double var7)
    {
        GL11.glPushMatrix();
        GL11.glTranslated(var3 + 0.5D, var5 + 0.9100000262260437D, var7 + 0.5D);
        GL11.glRotatef((float)(-90 * var2), 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(Barrels.instance.textX, 0.0F, -0.501F + Barrels.instance.textZ);
        GL11.glScalef(Barrels.instance.textScale, Barrels.instance.textScale, 1.0F);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glNormal3f(0.0F, 0.0F, -Barrels.instance.textScale);
        GL11.glDepthMask(false);
        this.getFontRenderer().drawString(var1, -this.getFontRenderer().getStringWidth(var1) / 2, 2, Barrels.instance.textColor);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }

    public void renderItem(ItemStack var1, RenderEngine var2, int var3, double var4, double var6, double var8)
    {
        GL11.glPushMatrix();
        GL11.glTranslated(var4 + 0.5D, var6 + 0.75D, var8 + 0.5D);
        GL11.glRotatef((float)(-90 * var3), 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.32F + Barrels.instance.itemX, 0.0F, -0.505F + Barrels.instance.itemZ);
        GL11.glScalef(Barrels.instance.itemScale, Barrels.instance.itemScale, 0.001F);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        IItemRenderer var10 = MinecraftForgeClient.getItemRenderer(var1, IItemRenderer$ItemRenderType.INVENTORY);
        var2.bindTexture(var2.getTexture(var1.getItem().getTextureFile()));
        Item var11 = var1.getItem();
        int var12;
        int var13;
        float var14;

        for (var12 = 0; var12 < (var11.requiresMultipleRenderPasses() ? var11.getRenderPasses(var1.getItemDamage()) : 1); ++var12)
        {
            var13 = var11.getColorFromItemStack(var1, var12);
            var14 = (float)(var13 >> 16 & 255) / 255.0F;
            float var15 = (float)(var13 >> 8 & 255) / 255.0F;
            float var16 = (float)(var13 & 255) / 255.0F;
            GL11.glColor4f(var14, var15, var16, 1.0F);

            if ((!(var11 instanceof ItemBlock) || !RenderBlocks.renderItemIn3d(Block.blocksList[var11.itemID].getRenderType())) && var10 == null)
            {
                var13 = var11.requiresMultipleRenderPasses() ? var11.getIconIndex(var1, var12) : var11.getIconIndex(var1);
                this.tessellator.startDrawingQuads();
                this.tessellator.addVertexWithUV(0.0D, 16.0D, 0.0D, (double)(var13 % 16 * 16) * 0.00390625D, (double)(var13 / 16 * 16 + 16) * 0.00390625D);
                this.tessellator.addVertexWithUV(16.0D, 16.0D, 0.0D, (double)(var13 % 16 * 16 + 16) * 0.00390625D, (double)(var13 / 16 * 16 + 16) * 0.00390625D);
                this.tessellator.addVertexWithUV(16.0D, 0.0D, 0.0D, (double)(var13 % 16 * 16 + 16) * 0.00390625D, (double)(var13 / 16 * 16) * 0.00390625D);
                this.tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, (double)(var13 % 16 * 16) * 0.00390625D, (double)(var13 / 16 * 16) * 0.00390625D);
                this.tessellator.draw();
            }
            else
            {
                GL11.glScalef(10.0F, 10.0F, 1.0F);
                GL11.glTranslatef(0.8F, 0.8F, 1.0F);
                GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);

                if (var10 != null)
                {
                    var10.renderItem(IItemRenderer$ItemRenderType.INVENTORY, var1, new Object[] {this.blockRender});
                }
                else
                {
                    this.blockRender.useInventoryTint = true;
                    this.blockRender.renderBlockAsItem(Block.blocksList[var1.itemID], var1.getItemDamage(), 1.0F);
                }

                GL11.glDisable(GL11.GL_LIGHTING);
            }
        }

        if (var1.hasEffect())
        {
            GL11.glDepthFunc(GL11.GL_EQUAL);
            var2.bindTexture(var2.getTexture("%blur%/misc/glint.png"));
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
            GL11.glColor4f(0.5F, 0.25F, 0.8F, 1.0F);
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glDepthMask(false);
            this.tessellator.startDrawingQuads();

            for (var12 = 0; var12 < 2; ++var12)
            {
                float var17 = (float)(Minecraft.getSystemTime() % (long)(3000 + var12 * 1873)) / (float)(3000 + var12 * 1873) * 256.0F;
                var14 = var12 == 1 ? -1.0F : 4.0F;
                this.tessellator.addVertexWithUV(0.0D, 16.0D, 0.0D, (double)(var17 + 20.0F * var14) * 0.00390625D, 0.0D);
                this.tessellator.addVertexWithUV(16.0D, 16.0D, 0.0D, (double)(var17 + 20.0F + 20.0F * var14) * 0.00390625D, 0.0D);
                this.tessellator.addVertexWithUV(16.0D, 0.0D, 0.0D, (double)(var17 + 20.0F) * 0.00390625D, 0.0D);
                this.tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, (double)var17 * 0.00390625D, 0.0D);
            }

            this.tessellator.draw();
            GL11.glDepthMask(true);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
        }

        if (var1.isItemDamaged())
        {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glTranslatef(0.0F, 0.0F, -0.1F);
            var2.bindTexture(var2.getTexture(var1.getItem().getTextureFile()));
            var12 = 13 - var1.getItemDamageForDisplay() * 13 / var1.getMaxDamage();
            var13 = 255 - var1.getItemDamageForDisplay() * 255 / var1.getMaxDamage();
            this.tessellator.startDrawingQuads();

            if (var12 != 0)
            {
                this.renderQuad(2, 13, var12, 1, 255 - var13 << 16 | var13 << 8);
            }

            if (var12 != 13)
            {
                this.renderQuad(2 + var12, 13, 12 - var12, 1, (255 - var13) / 4 << 16 | 16128);
                this.renderQuad(14, 13, 1, 1, 0);
            }

            this.renderQuad(2, 14, 13, 1, 0);
            this.tessellator.draw();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        GL11.glPopMatrix();
    }

    private void renderQuad(int var1, int var2, int var3, int var4, int var5)
    {
        this.tessellator.setColorOpaque_I(var5);
        this.tessellator.addVertex((double)var1, (double)var2, 0.0D);
        this.tessellator.addVertex((double)var1, (double)(var2 + var4), 0.0D);
        this.tessellator.addVertex((double)(var1 + var3), (double)(var2 + var4), 0.0D);
        this.tessellator.addVertex((double)(var1 + var3), (double)var2, 0.0D);
    }

    public void renderTileEntityAt(TileEntity var1, double var2, double var4, double var6, float var8)
    {
        try
        {
            TileEntityBarrel var9 = (TileEntityBarrel)var1;

            if (var9.getItem() != null)
            {
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_CULL_FACE);

                if (Barrels.instance.onlyRenderOneSide)
                {
                    byte var10 = var9.getSide();

                    if (this.isSideValidForRender(var1, var10))
                    {
                        this.setLight(var9, var10);
                        this.renderText(var9.getOverlay(), var10, var2, var4, var6);
                        this.renderItem(var9.getItem(), Minecraft.getMinecraft().renderEngine, var10, var2, var4, var6);
                    }
                }
                else
                {
                    if (this.isSideValidForRender(var1, 0))
                    {
                        this.setLight(var9, 0);
                        this.renderText(var9.getOverlay(), 0, var2, var4, var6);
                        this.renderItem(var9.getItem(), Minecraft.getMinecraft().renderEngine, 0, var2, var4, var6);
                    }

                    if (this.isSideValidForRender(var1, 1))
                    {
                        this.setLight(var9, 1);
                        this.renderText(var9.getOverlay(), 1, var2, var4, var6);
                        this.renderItem(var9.getItem(), Minecraft.getMinecraft().renderEngine, 1, var2, var4, var6);
                    }

                    if (this.isSideValidForRender(var1, 2))
                    {
                        this.setLight(var9, 2);
                        this.renderText(var9.getOverlay(), 2, var2, var4, var6);
                        this.renderItem(var9.getItem(), Minecraft.getMinecraft().renderEngine, 2, var2, var4, var6);
                    }

                    if (this.isSideValidForRender(var1, 3))
                    {
                        this.setLight(var9, 3);
                        this.renderText(var9.getOverlay(), 3, var2, var4, var6);
                        this.renderItem(var9.getItem(), Minecraft.getMinecraft().renderEngine, 3, var2, var4, var6);
                    }
                }

                GL11.glDisable(GL11.GL_CULL_FACE);
                GL11.glEnable(GL11.GL_LIGHTING);
            }
        }
        catch (Exception var11)
        {
            ;
        }
    }

    private void setLight(TileEntity var1, int var2)
    {
        byte var3 = 0;
        byte var4 = 0;

        if (var2 == 0)
        {
            var3 = -1;
        }
        else if (var2 == 1)
        {
            var4 = 1;
        }
        else if (var2 == 2)
        {
            var3 = 1;
        }
        else if (var2 == 3)
        {
            var4 = -1;
        }

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(var1.worldObj.getSavedLightValue(EnumSkyBlock.Block, var1.xCoord + var4, var1.yCoord, var1.zCoord + var3) * 16), (float)(var1.worldObj.getSavedLightValue(EnumSkyBlock.Sky, var1.xCoord + var4, var1.yCoord, var1.zCoord + var3) * 16));
    }

    protected boolean isSideValidForRender(TileEntity var1, int var2)
    {
        boolean var3 = false;
        byte var4 = 0;
        byte var5 = 0;

        if (var2 == 0)
        {
            var4 = -1;
        }
        else if (var2 == 1)
        {
            var5 = 1;
        }
        else if (var2 == 2)
        {
            var4 = 1;
        }
        else if (var2 == 3)
        {
            var5 = -1;
        }

        int var6 = var1.worldObj.getBlockId(var1.xCoord + var5, var1.yCoord, var1.zCoord + var4);
        return var6 == 0 || !Block.blocksList[var6].isOpaqueCube();
    }
}
