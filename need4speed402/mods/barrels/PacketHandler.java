package need4speed402.mods.barrels;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Arrays;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;

public class PacketHandler implements IPacketHandler {
	public static PacketHandler instance = new PacketHandler();
	public static final String networkChannel = "barrelsModUpdate";

	public void sendClientsItem(TileEntityBarrel var1) {
		if (!var1.worldObj.isRemote) {
			byte[] var2;

			if (var1.getItem() != null) {
				var2 = new byte[30];
				var2 = this.toByte(var2, var1.xCoord, 0);
				var2 = this.toByte(var2, var1.yCoord, 5);
				var2 = this.toByte(var2, var1.zCoord, 10);
				var2 = this.toByte(var2, var1.getItem().itemID, 15);
				var2 = this.toByte(var2, var1.getItem().stackSize + 1, 20);
				var2 = this.toByte(var2, var1.getItem().getItemDamage(), 25);
				var2 = this.writeNBT(var1.getItem().getTagCompound(), var2);
			} else {
				var2 = new byte[15];
				var2 = this.toByte(var2, var1.xCoord, 0);
				var2 = this.toByte(var2, var1.yCoord, 5);
				var2 = this.toByte(var2, var1.zCoord, 10);
			}

			PacketDispatcher.sendPacketToAllPlayers(new Packet250CustomPayload("barrelsModUpdate", var2));
		}
	}

	public void sendClientBlockChange(TileEntityBarrel var1) {
		if (!var1.worldObj.isRemote) {
			byte[] var2 = new byte[16];
			var2 = this.toByte(var2, var1.xCoord, 0);
			var2 = this.toByte(var2, var1.yCoord, 5);
			var2 = this.toByte(var2, var1.zCoord, 10);
			var2[15] = (byte) (-(var1.getBlockMetadata() + 1));
			PacketDispatcher.sendPacketToAllPlayers(new Packet250CustomPayload("barrelsModUpdate", var2));
		}
	}

	public void sendClientInfo(TileEntityBarrel var1, Player var2) {
		if (!var1.worldObj.isRemote) {
			byte[] var3;

			if (var1.getItem() != null) {
				var3 = new byte[31];
				var3 = this.toByte(var3, var1.xCoord, 0);
				var3 = this.toByte(var3, var1.yCoord, 5);
				var3 = this.toByte(var3, var1.zCoord, 10);
				var3 = this.toByte(var3, var1.getItem().itemID, 15);
				var3 = this.toByte(var3, -(var1.getItem().stackSize + 1), 20);
				var3 = this.toByte(var3, var1.getItem().getItemDamage(), 25);
				var3[30] = (byte) (var1.getSide() + 1);
				var3 = this.writeNBT(var1.getItem().getTagCompound(), var3);
			} else {
				var3 = new byte[16];
				var3 = this.toByte(var3, var1.xCoord, 0);
				var3 = this.toByte(var3, var1.yCoord, 5);
				var3 = this.toByte(var3, var1.zCoord, 10);
				var3[15] = (byte) (var1.getSide() + 1);
			}

			PacketDispatcher.sendPacketToPlayer(new Packet250CustomPayload("barrelsModUpdate", var3), var2);
		}
	}

	public void sendServerRequest(TileEntityBarrel var1) {
		if (var1.worldObj.isRemote) {
			byte[] var2 = new byte[15];
			var2 = this.toByte(var2, var1.xCoord, 0);
			var2 = this.toByte(var2, var1.yCoord, 5);
			var2 = this.toByte(var2, var1.zCoord, 10);
			PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("barrelsModUpdate", var2));
		}
	}

	public void onPacketData(INetworkManager var1, Packet250CustomPayload var2, Player var3) {
		try {
			if (!var2.channel.equals("barrelsModUpdate")) {
				return;
			}

			byte[] var4 = var2.data;
			int var5 = toInteger(var4, 0);
			int var6 = toInteger(var4, 5);
			int var7 = toInteger(var4, 10);
			World var8 = ((EntityPlayer) var3).worldObj;
			TileEntityBarrel var9 = (TileEntityBarrel) var8.getBlockTileEntity(var5, var6, var7);

			if (var9 == null) {
				return;
			}

			if (var8.isRemote) {
				if (var4.length == 16 && var4[15] < 0) {
					if (var9.blockMetadata != -(var4[15] + 1)) {
						var9.blockMetadata = -(var4[15] + 1);
						var8.setBlockMetadata(var5, var6, var7, -(var4[15] + 1));
						var8.markBlockForRenderUpdate(var5, var6, var7);
					}
				} else {
					if (var4.length <= 16) {
						var9.setItem((ItemStack) null);

						if (var4.length == 16) {
							var9.setSide((byte) (var4[15] - 1));
						}
					} else {
						boolean var10 = false;

						if (toInteger(var4, 20) > 0) {
							var9.setItem(new ItemStack(toInteger(var4, 15), toInteger(var4, 20) - 1, toInteger(var4, 25)));
						} else {
							var10 = true;
							var9.setItem(new ItemStack(toInteger(var4, 15), -(toInteger(var4, 20) + 1), toInteger(var4, 25)));
							var9.setSide((byte) (var4[30] - 1));
						}

						if (!var10 && var4.length > 30 || var4.length > 31) {
							var9.getItem().setTagCompound(
									(NBTTagCompound) NBTBase.readNamedTag(new DataInputStream(new ByteArrayInputStream(Arrays.copyOfRange(var4,
											var10 ? 31 : 30, var4.length)))));
						}
					}

					var9.onInventoryChanged();
				}
			} else {
				this.sendClientInfo(var9, var3);
			}
		} catch (Exception var11) {
			var11.printStackTrace();
		}
	}

	private static int toInteger(byte[] var0, int var1) {
		int var2 = 0;

		for (int var3 = var1; var3 < var1 + 4; ++var3) {
			var2 = (var2 << 8) + (var0[var3] & 255);
		}

		return var2;
	}

	private byte[] toByte(byte[] var1, int var2, int var3) {
		var1[var3++] = (byte) (var2 >> 24);
		var1[var3++] = (byte) (var2 >> 16);
		var1[var3++] = (byte) (var2 >> 8);
		var1[var3++] = (byte) var2;
		return var1;
	}

	private byte[] writeNBT(NBTTagCompound var1, byte[] var2) {
		try {
			if (var1 != null) {
				ByteArrayOutputStream var3 = new ByteArrayOutputStream();
				NBTBase.writeNamedTag(var1, new DataOutputStream(var3));
				byte[] var4 = new byte[var2.length + var3.size()];

				for (int var5 = 0; var5 < var2.length; ++var5) {
					var4[var5] = var2[var5];
				}

				byte[] var8 = var3.toByteArray();

				for (int var6 = var2.length; var6 < var8.length + var2.length; ++var6) {
					var4[var6] = var8[var6 - var2.length];
				}

				var2 = var4;
			}
		} catch (Exception var7) {
			var7.printStackTrace();
		}

		return var2;
	}
}
