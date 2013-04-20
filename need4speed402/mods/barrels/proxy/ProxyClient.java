package need4speed402.mods.barrels.proxy;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import need4speed402.mods.barrels.BarrelRender;
import need4speed402.mods.barrels.Barrels;
import need4speed402.mods.barrels.TileEntityBarrel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

@SideOnly(Side.CLIENT)
public class ProxyClient extends Proxy {
	public void initializeRendering() {
		TileEntityRenderer.instance.specialRendererMap.put(TileEntityBarrel.class, BarrelRender.instance);
		BarrelRender.instance.setTileEntityRenderer(TileEntityRenderer.instance);
	}

	public void preloadTextures() {
		Barrels.instance.getClass();
		MinecraftForgeClient.preloadTexture("/need4speed402/mods/barrels/gfx/Barrels.png");
	}
}
