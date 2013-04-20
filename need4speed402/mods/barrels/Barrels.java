package need4speed402.mods.barrels;

import java.util.ArrayList;

import need4speed402.mods.barrels.proxy.Proxy;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = { "barrelsModUpdate" }, packetHandler = PacketHandler.class)
@Mod(modid = "barrels", name = "The Barrels Mod", version = "1.6.1", useMetadata = false)
public class Barrels {
	@Instance("barrels")
	public static Barrels instance;
	@SidedProxy(serverSide = "need4speed402.mods.barrels.proxy.Proxy", clientSide = "need4speed402.mods.barrels.proxy.ProxyClient")
	public static Proxy proxy = new Proxy();
	public BlockBarrel barrel;
	public ItemStack T1barrel;
	public ItemStack T2barrel;
	public ItemStack T3barrel;
	public final String block_png = "/need4speed402/mods/barrels/gfx/Barrels.png";
	public int T1BarrelMaxStorage = 64;
	public int T2BarrelMaxStorage = 1024;
	public int T3BarrelMaxStorage = 4089;
	private String T1barrelCrafting = "shaped;logWood;logWood;logWood;item,ingotIron;nothing;item,ingotIron;logWood;logWood;logWood";
	private String T2barrelCrafting = "shaped;item,blazeRod;block,obsidian;item,blazeRod;item,enderPearl;barrel,T1barrel;item,enderPearl;item,blazeRod;block,obsidian;item,blazeRod";
	private String T3barrelCrafting = "shaped;block,obsidian;item,blazeRod;block,obsidian;item,diamond;barrel,T2barrel;item,diamond;block,obsidian;item,blazeRod;block,obsidian";
	private String T1barrelName = "Barrel";
	private String T2barrelName = "Ender Barrel";
	private String T3barrelName = "Quantum Barrel";
	public float itemScale = 0.04F;
	public float textScale = 0.01F;
	public float textX = 0.0F;
	public float textZ = 0.0F;
	public int textColor = -1;
	public float itemX = 0.0F;
	public float itemZ = 0.0F;
	public boolean onlyRenderOneSide = false;
	public boolean leaveOneItem = false;
	public String interaction = "in/out;in/out;unused;unused;unused;unused";

	@PreInit
	public void preLoad(FMLPreInitializationEvent var1) {
		instance = this;
		Configuration var2 = new Configuration(var1.getSuggestedConfigurationFile());

		try {
			this.barrel = new BlockBarrel(var2.get("block", "Barrel", 4000, "The block ID for barrels.").getInt());
			this.T1barrel = new ItemStack(this.barrel, 1, 0);
			this.T2barrel = new ItemStack(this.barrel, 1, 1);
			this.T3barrel = new ItemStack(this.barrel, 1, 2);
			this.T1BarrelMaxStorage = var2.get("BarrelStorage", "T1BarrelMaxStorage", this.T1BarrelMaxStorage,
					"The maximum stacks of items that a barrel can hold.\n Set any value to -1 and that type of barrel will be disabled.").getInt();
			this.T2BarrelMaxStorage = var2.get("BarrelStorage", "T2BarrelMaxStorage", this.T2BarrelMaxStorage).getInt();
			this.T3BarrelMaxStorage = var2.get("BarrelStorage", "T3BarrelMaxStorage", this.T3BarrelMaxStorage).getInt();
			this.T1barrelName = var2.get("name", "T1barrelName", this.T1barrelName, "Name of the barrels.").value;
			this.T2barrelName = var2.get("name", "T2barrelName", this.T2barrelName).value;
			this.T3barrelName = var2.get("name", "T3barrelName", this.T3barrelName).value;
			this.itemScale = (float) var2.get("render", "itemScale", this.itemScale, "Render settings for barrel render.").getDouble(this.itemScale);
			this.textScale = (float) var2.get("render", "textScale", this.textScale).getDouble(this.textScale);
			this.textX = (float) var2.get("render", "textX", this.textX).getDouble(this.textX);
			this.textZ = (float) var2.get("render", "textZ", this.textZ).getDouble(this.textZ);
			this.textColor = var2.get("render", "textColor", this.textColor).getInt();
			this.itemX = (float) var2.get("render", "itemX", this.itemX).getDouble(this.itemX);
			this.itemZ = (float) var2.get("render", "itemZ", this.itemZ).getDouble(this.itemZ);
			this.onlyRenderOneSide = var2.get("render", "onlyRenderOneSide", this.onlyRenderOneSide).getBoolean(this.onlyRenderOneSide);
			this.interaction = var2
					.get("interaction",
							"interaction",
							this.interaction,
							"How the barrels interact with outside sources (buildcraft pipes)\n Usage: can be in three states: in/out/\'in/out\'\n Formatting: <up>;<down>;<forward>;<back>;<right>;<left>").value;
			this.leaveOneItem = var2.get("interaction", "leaveOneItem", this.leaveOneItem).getBoolean(this.leaveOneItem);
			this.T1barrelCrafting = var2.get("crafting", "T1barrelCrafting", this.T1barrelCrafting,
					"This allowes you to chage the crafting recipeis.\n Look on the forum for tutorials on setting this up.").value;
			this.T2barrelCrafting = var2.get("crafting", "T2barrelCrafting", this.T2barrelCrafting).value;
			this.T3barrelCrafting = var2.get("crafting", "T3barrelCrafting", this.T2barrelCrafting).value;
		} catch (Exception var7) {
			System.err.println("There has been a problem with the initialization of Barrels!");
			throw new RuntimeException(var7);
		} finally {
			var2.save();
		}

		proxy.initializeRendering();
	}

	@Init
	public void load(FMLInitializationEvent var1) {
		GameRegistry.registerTileEntity(TileEntityBarrel.class, "tileBarrels");
		GameRegistry.registerBlock(this.barrel, ItemBarrel.class, "block.barrel", "barrels");
		proxy.preloadTextures();

		if (this.T1BarrelMaxStorage != -1) {
			LanguageRegistry.addName(this.T1barrel, this.T1barrelName);
		}

		if (this.T2BarrelMaxStorage != -1) {
			LanguageRegistry.addName(this.T2barrel, this.T2barrelName);
		}

		if (this.T3BarrelMaxStorage != -1) {
			LanguageRegistry.addName(this.T3barrel, this.T3barrelName);
		}
	}

	@PostInit
	public void postLoad(FMLPostInitializationEvent var1) throws Exception {
		if (this.T1BarrelMaxStorage != -1) {
			this.addRecipe(this.T1barrel, this.T1barrelCrafting);
		}

		if (this.T2BarrelMaxStorage != -1) {
			this.addRecipe(this.T2barrel, this.T2barrelCrafting);
		}

		if (this.T3BarrelMaxStorage != -1) {
			this.addRecipe(this.T3barrel, this.T3barrelCrafting);
		}
	}

	public void addRecipe(ItemStack var1, String var2) {
		String var3 = "nothing";

		try {
			if (var2 == null || var2.length() == 0) {
				return;
			}

			String[] var4 = var2.split(";");

			if (var4.length != 10) {
				throw new IllegalArgumentException("Insuficent information");
			}

			ArrayList var5 = new ArrayList();
			byte var6 = 0;
			byte var7 = 3;
			byte var8 = 0;
			byte var9 = 3;
			boolean var10 = true;
			boolean var11 = true;

			for (byte var12 = 0; var12 < 3; ++var12) {
				if (var10 && var4[var12 * 3 + 1].equals("nothing") && var4[var12 * 3 + 2].equals("nothing") && var4[var12 * 3 + 3].equals("nothing")) {
					++var6;
				} else {
					var10 = false;
				}

				if (var11 && var4[9 - var12 * 3].equals("nothing") && var4[8 - var12 * 3].equals("nothing") && var4[7 - var12 * 3].equals("nothing")) {
					--var7;
				} else {
					var11 = false;
				}
			}

			var10 = true;
			var11 = true;
			int var23;

			for (var23 = 0; var23 < 3; ++var23) {
				if (var10 && var4[var23 + 1].equals("nothing") && var4[var23 + 4].equals("nothing") && var4[var23 + 7].equals("nothing")) {
					++var8;
				} else {
					var10 = false;
				}

				if (var11 && var4[3 - var23].equals("nothing") && var4[6 - var23].equals("nothing") && var4[9 - var23].equals("nothing")) {
					--var9;
				} else {
					var11 = false;
				}
			}

			var10 = var4[0].equals("shapeless");

			for (int var22 = var6; var22 < var7; ++var22) {
				if (!var10) {
					var5.add(var22 - var6, "");
				}

				for (var23 = var8; var23 < var9; ++var23) {
					int var13 = var22 * 3 + var23 + 1;
					boolean var14 = var4[var13].equals("nothing");

					if (!var10) {
						String var15 = null;

						if (var14) {
							var15 = " ";
						} else {
							var15 = Integer.toString(var13);
						}

						if (var22 - var6 == 0) {
							var5.set(0, var5.get(0) + var15);
						} else if (var22 - var6 == 1) {
							var5.set(1, var5.get(1) + var15);
						} else if (var22 - var6 == 2) {
							var5.set(2, var5.get(2) + var15);
						}
					}

					if (!var14) {
						if (!var10) {
							var5.add(Character.valueOf(Integer.toString(var13).charAt(0)));
						}

						Object var25 = null;

						if (var4[var13].contains(",")) {
							String[] var16 = var4[var13].split(",");
							int var19;
							int var18;

							if (var16[0].equals("block")) {
								Block[] var17 = Block.blocksList;
								var18 = var17.length;

								for (var19 = 0; var19 < var18; ++var19) {
									Block var20 = var17[var19];

									if (var20 != null && var20.getBlockName() != null && var20.getBlockName().equals("tile." + var16[1])) {
										var25 = new ItemStack(var20, 1, var16.length == 3 ? Integer.parseInt(var16[2]) : 0);
										break;
									}
								}
							} else if (var16[0].equals("item")) {
								Item[] var24 = Item.itemsList;
								var18 = var24.length;

								for (var19 = 0; var19 < var18; ++var19) {
									Item var26 = var24[var19];

									if (var26 != null && var26.getItemName() != null && var26.getItemName().equals("item." + var16[1])) {
										var25 = new ItemStack(var26, 1, var16.length == 3 ? Integer.parseInt(var16[2]) : 0);
										break;
									}
								}
							} else if (var16[0].equals("barrel")) {
								var25 = this.getClass().getField(var16[1]).get(this);
							} else if (!var16[0].equals("itemid") && !var16[0].equals("blockid")) {
								var25 = Class.forName(var16[0]).getField(var16[1]).get((Object) null);

								if (!(var25 instanceof ItemStack)) {
									if (var25 instanceof Block) {
										var25 = new ItemStack((Block) var25, 1, var16.length == 3 ? Integer.parseInt(var16[2]) : 0);
									} else if (var25 instanceof Item) {
										var25 = new ItemStack((Item) var25, 1, var16.length == 3 ? Integer.parseInt(var16[2]) : 0);
									} else if (!(var25 instanceof String)) {
										throw new IllegalArgumentException("Wrong type.");
									}
								}
							} else {
								var25 = new ItemStack(Integer.parseInt(var16[1]), 1, var16.length == 3 ? Integer.parseInt(var16[2]) : 0);
							}
						} else {
							var25 = var4[var13];
						}

						var5.add(var25);
					}
				}
			}

			if (var4[0].equals("shaped")) {
				GameRegistry.addRecipe(new ShapedOreRecipe(var1, var5.toArray()));
			} else {
				if (!var10) {
					throw new IllegalArgumentException("Type of crafting has not been specified.");
				}

				GameRegistry.addRecipe(new ShapelessOreRecipe(var1, var5.toArray()));
			}
		} catch (Exception var21) {
			var21.printStackTrace();
			System.err.println(var21.toString() + ": There was a problem while adding a recipe. Plase make sure your configuratons are correct.");
		}
	}
}
