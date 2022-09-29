package mcbot;

import java.io.FileOutputStream;

import mc.Chunk;
import mc.Entity;
import mc.Location;
import mc.MinecraftEntityID;
import mc.MinecraftWindow;
import mc.MinecraftWorld;
import util.NBT.*;
import mc.Slot;
import mc.TradeList;
import util.BitString;
import util.Pack;

public class PackLoop {
	
	static class PackInDisconnectPlay implements PackEvent{
		public void event(Bot bot, Pack p) throws Exception {
			if(p.getID() != 0x1a) 
				throw new Exception("Bad pack type");
			String reason = p.readString();
			System.out.println(reason);
		}
	}
	static class PackInSpawnEntity implements PackEvent{
		public void event(Bot bot, Pack p) throws Exception {
			if(p.getID() != 0x00) throw new Exception("Bad pack type");
			Entity en = new Entity();
			en.id = p.readVarInt();
			p.readLong();
			p.readLong();
			en.type = p.readVarInt();
			en.x = p.readDouble();
			en.y = p.readDouble();
			en.z = p.readDouble();
			en.pitch = p.readByte();
			en.yaw = p.readByte();
			en.data = p.readInt();
			if (en.data != 0) {
				en.vx = p.readShort();
				en.vy = p.readShort();
				en.vz = p.readShort();
			}
			bot.spawnEntity(en);
		}
	}
	static class PackInSpawnExpOrb implements PackEvent{
		public void event(Bot bot, Pack p) throws Exception {
			if(p.getID() != 0x01) throw new Exception("Bad pack type");
			Entity en = new Entity();
			en.id = p.readVarInt();
			en.x = p.readDouble();
			en.y = p.readDouble();
			en.z = p.readDouble();
			en.data = p.readShort();
			en.type = MinecraftEntityID.meExperienceOrb.ordinal();
			bot.spawnEntity(en);
		}
	}
	static class PackInSpawnLivingEntity implements PackEvent{
		public void event(Bot bot,Pack p) throws Exception{
			if(p.getID() != 0x02) throw new Exception("Bad pack type");
			Entity en = new Entity();
			en.id = p.readVarInt();
			p.readLong();
			p.readLong();
			en.type = p.readVarInt();
			en.x = p.readDouble();
			en.y = p.readDouble();
			en.z = p.readDouble();
			en.yaw = p.readByte();
			en.pitch = p.readByte();
			en.headPitch = p.readByte();
			en.vx = p.readShort();
			en.vy = p.readShort();
			en.vz = p.readShort();
			bot.spawnEntity(en);
		}
	}
	//static class PackInSpawnPainting implements Event{}
	static class PackInSpawnPlayer implements PackEvent{
		public void event(Bot bot,Pack p) throws Exception{
			if(p.getID() != 0x04) throw new Exception("Bad pack type");
			Entity en = new Entity();
			en.id = p.readVarInt();
			p.readLong();
			p.readLong();
			en.x = p.readDouble();
			en.y = p.readDouble();
			en.z = p.readDouble();
			en.yaw = p.readByte();
			en.pitch = p.readByte();
			en.type = MinecraftEntityID.mePlayer.ordinal();
			bot.spawnEntity(en);
		}
	}
	//static class PackInSculkVibrationSignal implements PackEvent{}
	//static class PackInEntityAnimation implements Event{}
	//static class PackInStatistics implements Event{}
	static class PackInAcknowledgePlayerDigging implements PackEvent{
		public void event(Bot bot, Pack p) throws Exception {
			if(p.getID() != 0x08) throw new Exception("Bad pack type");
			if(bot.worldLoadEnable == false) return;
			Location loc = p.readPos();
			int block = p.readVarInt();
			bot.acknowledgeDig(loc, block);
		}
	}
	//static class PackInBreakAnimation implements Event{}
	//static class PackInEntityData implements Event{}
	//static class PackInBlockAction implements Event{}
	static class PackInBlockChange implements PackEvent{
		public void event(Bot bot,Pack p) throws Exception{
			if(p.getID() != 0x0c) throw new Exception("Bad pack type");
			if(bot.worldLoadEnable == false) return;
			Location loc = p.readPos();
			//int org = bot.world.getBlock(x[0], y[0], z[0]);
			int cur = p.readVarInt();
			bot.world.setBlock(loc.x,loc.y,loc.z,cur);
		};
	}
	//static class PackInBossBar implements Event{}
	//static class PackInServerDifficulty implements Event{}
	static class PackInChatMessage implements PackEvent{
		public void event(Bot bot,Pack p) throws Exception {
			if (p.getID() != 0x0f) throw new Exception("Bad pack type");
			String json = p.readString();
			int pos = p.readByte();
			bot.chat.onChatMessage(json, pos);
		}
	}
	//static class PackInClearTitles implements Event{}
	//static class PackInTabComplete implements Event{}
	//static class PackInDeclareCommands implements Event{}
	static class PackInCloseWindow implements PackEvent{
		public void event(Bot bot,Pack p) throws Exception {
			if (p.getID() != 0x13) throw new Exception("Bad pack type");
			int wid = p.readUByte();
			if(wid != bot.item.windowID) return;
			bot.item.lockSlot();
			bot.item.windowType = MinecraftWindow.none;
			bot.item.unlockSlot();
			bot.item.windowUpdate.setEvent();;
		}
	};
	static class PackInWindowItems implements PackEvent{
		public void event(Bot bot,Pack p) throws Exception{
			if (p.getID() != 0x14) throw new Exception("Bad pack type");
			int wid = p.readUByte();
			bot.item.lockSlot();
			if(wid == 0) {
				bot.item.windowState = p.readVarInt();
				int slotCount = p.readVarInt();
				int fs = slotCount;
				if(fs >= 5) fs = 5;
				for(int i = 0;i < fs;i++) bot.item.slots[i] = new Slot(p);
				fs = slotCount;
				if(fs > 9) fs = 9;
				fs -= 5;
				for(int i = 0;i < fs;i++) bot.item.equipments[i] = new Slot(p);
				fs = slotCount;
				if(fs > 45) fs = 45;
				fs -= 9;
				for(int i = 0;i < fs;i++) bot.item.inventorys[i] = new Slot(p);
				if(slotCount >= 46) bot.item.alterHand[0] = new Slot(p);
				while(slotCount > 46) {
					slotCount--;
					new Slot(p);
				}
				bot.item.selected = new Slot(p);
			}
			else if(wid == bot.item.windowID) {
				bot.item.windowState = p.readVarInt();
				int slotCount = p.readVarInt();
				int slotsCount = Item.inventory2Index[bot.item.windowType.ordinal()];
				int fs = slotCount;
				if(fs > slotsCount) fs = slotsCount;
				for(int i = 0;i < fs;i++) bot.item.slots[i] = new Slot(p);
				fs = slotCount;
				if(fs > slotsCount + 36) fs = slotsCount + 36;
				fs -= slotsCount;
				for(int i = 0;i < fs;i++) bot.item.inventorys[i] = new Slot(p);
				bot.item.selected = new Slot(p);
			}
			bot.item.unlockSlot();
			bot.item.windowUpdate.setEvent();;
		}
	};
	//static class PackInWindowProperty implements Event{}
	static class PackInSetSlot implements PackEvent{
		public void event(Bot bot,Pack p) throws Exception{
			if (p.getID() != 0x16) throw new Exception("Bad pack type");
			int wid = p.readByte();
			int ws = p.readVarInt();
			int slotId = p.readShort();
			bot.item.lockSlot();
			if(wid == -1) {
				bot.item.windowState = ws;
				bot.item.selected = new Slot(p);
			}
			else {
				bot.item.windowState = ws;
				if(wid == 0) {
					if(slotId < 5) bot.item.slots[slotId] = new Slot(p);
					else if(slotId < 9) bot.item.equipments[slotId - 5] = new Slot(p);
					else if(slotId < 45) bot.item.inventorys[slotId - 9] = new Slot(p);
					else if(slotId == 45) bot.item.alterHand[0] = new Slot(p);
				}
				else if(wid == bot.item.windowID){
					int slotsCount = Item.inventory2Index[bot.item.windowType.ordinal()];
					if(slotId < slotsCount) bot.item.slots[slotId] = new Slot(p);
					else if(slotId < slotsCount + 36) bot.item.inventorys[slotId - slotsCount] = new Slot(p);
				}
			}
			bot.item.unlockSlot();
			bot.item.windowUpdate.setEvent();;
		}
	}
	//static class PackInSetCooldown implements Event{}
	//static class PaclInPluginMessage implements Event{}
	//static class PackInNamedSoundEffect implements Event{}
	//static class PackInDisconnect implements Event{}
	//static class PackInEntityStatus implements Event{}
	//static class PackInExplosion implements Event{}
	static class PackInUnloadChunk implements PackEvent{
		public void event(Bot bot,Pack p) throws Exception{
			if (p.getID() != 0x1d) throw new Exception("Bad pack type");
			if(bot.worldLoadEnable == false) return;
			int x = p.readInt();
			int z = p.readInt();
			bot.world.unlock(x, z);
		}
	};
	//static class PackInChangeGameState implements Event{}
	//static class PackInOpenHorseWindow implements Event{}
	//static class PackInInitializeWorldBorder implements Event{}
	static class PackInKeepAlive implements PackEvent{
		public void event(Bot bot,Pack p) throws Exception{
			if (p.getID() != 0x21) throw new Exception("Bad pack type");
			bot.net.packOutKeepAlive(p.readLong());
			bot.keepalive.prevTime = System.currentTimeMillis();
		}
	};
	static class PackInChunkDataAndUpdateLight implements PackEvent{
		public void event(Bot bot,Pack p) throws Exception{
			if (p.getID() != 0x22) throw new Exception("Bad pack type");
			if(bot.worldLoadEnable == false) return;
			int high = bot.world.getTotalHigh();
			Chunk chunk = new Chunk();
			chunk.x = p.readInt();
			chunk.z = p.readInt();
			if(chunk.x == -16260 && chunk.z == -128260) {
				int inBreak = 1;
				
			}
			NBT.parseNBT(p);
			int y = 0;
			int dataSize = p.readVarInt();
			int endPos = dataSize  + p.tellg();
			while (true) {
				if (y >= high) break;
				if(p.tellg() >= endPos) break;
				p.readShort();
				int bitPerEntry = p.readByte();
				if (bitPerEntry == 0) {
					int block = p.readVarInt();
					chunk.defBlock[y] = block;
					p.readVarInt();
				}
				else if (bitPerEntry <= 8) {
					if (bitPerEntry < 4) bitPerEntry = 4;
					int plate_len = p.readVarInt();
					int[] plate = new int[plate_len];
					for (int j = 0; j < plate_len; j++) plate[j] = p.readVarInt();
					int bit_str_len = p.readVarInt();
					long[] ar = new long[bit_str_len];
					for (int j = 0; j < bit_str_len; j++) ar[j] = p.readLong();
					chunk.block[y] = new short[4096];
					BitString s =  new BitString(ar);
					int bit_counter = 0;
					int dx, dy, dz;
					for (dy = 0; dy < 16; dy++) {
						for (dz = 0; dz < 16; dz++) {
							for (dx = 0; dx < 16; dx++) {
								bit_counter += bitPerEntry;
								int val;
								if (bit_counter > 64) {
									bit_counter -= 64;
									bit_counter = bitPerEntry - bit_counter;
									val = s.getBits(bit_counter);
									bit_counter = bitPerEntry;
								}
								else if (bit_counter == 64) bit_counter = 0;
								val = s.getBits(bitPerEntry);
								if (val >= plate_len) val = 1;
								else val = plate[val];
								chunk.block[y][dy * 256 + dz * 16 + dx] = (short)val;
							}
						}
					}
				}
				else {
					int arLen = p.readVarInt();
					long[] ar = new long[arLen];
					for (int i = 0; i < arLen; i++) {
						ar[i] = p.readLong();
					}
					BitString b = new BitString(ar);
					int bit_counter = 0;
					int dy, dz, dx;
					chunk.block[y] = new short[4096];
					for (dy = 0; dy < 16; dy++) {
						for (dz = 0; dz < 16; dz++) {
							for (dx = 0; dx < 16; dx++) {
								bit_counter += 15;
								int val;
								if (bit_counter > 64) {
									bit_counter -= 64;
									bit_counter = 15 - bit_counter;
									val = b.getBits(bit_counter);
									bit_counter = 0;
								}
								else if (bit_counter == 64) bit_counter = 0;
								val = b.getBits(15);
								chunk.block[y][dy * 256 + dz * 16 + dx] = (short)val;
							}
						}
					}

				}
				bitPerEntry = p.readByte();
				if (bitPerEntry == 0) {
					p.readVarInt();
					p.readVarInt();
				}
				else if (bitPerEntry <= 3) {
					int platLen = p.readVarInt();
					while ((platLen--) != 0) p.readVarInt();
					int arLen = p.readVarInt();
					while ((arLen--) != 0) p.readLong();
				}
				else {
					int arLen = p.readVarInt();
					while ((arLen--) != 0) p.readLong();
				}
				y++;
			}
			bot.world.load(chunk);
		}
	}
	//static class PackInEffect implements Event{}
	//static class PackInParticle implements Event{}
	//static class PackInUpdateLight implements Event{}
	static class PackInJoinGame implements PackEvent{
		public void event(Bot bot,Pack p) throws Exception{
			if (p.getID() != 0x26) throw new Exception("Bad pack type");
			int selfId = p.readInt();
			p.readByte();
			p.readByte();
			p.readByte();
			int len = p.readVarInt();
			while ((len--) != 0) p.readString(); 
			NBT.parseNBT(p);
			NBT.parseNBT(p);
			String name = p.readString();
			MinecraftWorld wId;
			if (name.compareTo("minecraft:the_end") == 0) wId = MinecraftWorld.mwTheEnd;
			else if (name.compareTo("minecraft:overworld") == 0) wId = MinecraftWorld.mwOverworld;
			else wId = MinecraftWorld.mwNethier;
			bot.selfID = selfId;
			bot.world.respawn(wId);
		}
	};
	//static class PackInMapData implements Event{}
	static class PackInTradeList implements PackEvent{
		public void event(Bot bot,Pack p) throws Exception{
			if (p.getID() != 0x28) throw new Exception("Bad pack type");
			int wid = p.readVarInt();
			if(wid != bot.item.windowID) return;
			int count = p.readByte();
			TradeList [] list = new TradeList[count];
			for (int i = 0; i < count; i++) {
				list[i] = new TradeList();
				list[i].input1 = new Slot(p);
				list[i].output = new Slot(p);
				boolean has = p.readBoolean();
				if (has) list[i].input2 = new Slot(p);
				list[i].disabled = p.readBoolean();
				list[i].tradedCount = p.readInt();
				list[i].maxTradedCount = p.readInt();
				list[i].xp = p.readInt();
				list[i].specialPrice = p.readInt();
				list[i].priceMultipiler = p.readFloat();
				list[i].demand = p.readInt();
				if (list[i].demand < 0) list[i].demand = 0;
			}
			bot.item.tradeList = list;
			bot.item.tradeListUpdate.setEvent();
		}
	}
	static class PackInEnityPosition implements PackEvent{
		public void event(Bot bot,Pack p) throws Exception{
			if (p.getID() != 0x29) throw new Exception("Bad pack type");
			int id = p.readVarInt();
			Entity en = bot.getEntity(id);
			if(en == null) return;
			en.x += p.readShort() / 4096.0;
			en.y += p.readShort() / 4096.0;
			en.z += p.readShort() / 4096.0;
		}
	}
	static class PackInEnityPositionAndRotation implements PackEvent{
		public void event(Bot bot,Pack p) throws Exception{
			if (p.getID() != 0x2a) throw new Exception("Bad pack type");
			int id = p.readVarInt();
			Entity en = bot.getEntity(id);
			if(en == null) return;
			en.x += p.readShort() / 4096.0;
			en.y += p.readShort() / 4096.0;
			en.z += p.readShort() / 4096.0;
			en.yaw = p.readByte();
			en.pitch = p.readByte();
		}
	};
	static class PackInEntityRotation implements PackEvent{
		public void event(Bot bot,Pack p) throws Exception{
			if (p.getID() != 0x2b) throw new Exception("Bad pack type");
			int id = p.readVarInt();
			Entity en = bot.getEntity(id);
			if(en == null) return;
			en.yaw = p.readByte();
			en.pitch = p.readByte();
		}
	};
	//static class PackInVehicleMove implements Event{}
	//static class PackInOpenBook implements Event{}
	static class PackInOpenWindow implements PackEvent{
		public void event(Bot bot,Pack p) throws Exception{
			if (p.getID() != 0x2e) throw new Exception("Bad pack type");
			int wid = p.readVarInt();
			int type = p.readVarInt();
			//String title = p.readString();
			bot.item.windowID = wid;
			bot.item.windowType = MinecraftWindow.values()[type];
			bot.item.windowIsOpen.setEvent();
		}
	}
	//static class PackInOpenSignEditor implements Event{}
	//static class PackInPing implements Event{}
	//static class PackInCraftRecipeResponse implements Event{}
	//static class PackInPlayerAbilities implements Event{}
	//static class PackInEndCombatEvent implements Event{}
	//static class PackInEnterCombatEvent implements Event{}
	//static class PackInDeathCombatEvent implements Event{}
	//static class PackInPlayerInfo implements Event{}
	//static class PackInFacePlayer implements Event{}
	static class PackInPlayerPositionAndLook implements PackEvent{
		public void event(Bot bot,Pack p) throws Exception{
			if (p.getID() != 0x38) throw new Exception("Bad pack type");
			//double ox = bot.x;
			//double oy = bot.y;
			//double oz = bot.z;
			double x = p.readDouble();
			double y = p.readDouble();
			double z = p.readDouble();
			float yaw = p.readFloat();
			float pitch = p.readFloat();
			int flag = p.readByte();
			int id = p.readVarInt();
			if((flag & 0x01) == 1) bot.x += x;
			else bot.x = x;
			if((flag & 0x02) == 1) bot.y += y;
			else bot.y = y;
			if((flag & 0x04) == 1) bot.z += z;
			else bot.z = z;
			if((flag & 0x08) == 1) bot.yaw += yaw;
			else bot.yaw = yaw;
			if((flag & 0x010) == 1) bot.pitch += pitch;
			else bot.pitch = pitch;
			bot.net.packOutTeleConfirm(id);
			bot.posUpdate.setEvent();
			System.out.println("Set pos (" + bot.x + "," + bot.y + "," + bot.z + ").");
		}
	}
	//static class PackInUnlockRecipes implements Event{}
	static class PackInDestroyEntites implements PackEvent{
		public void event(Bot bot,Pack p) throws Exception{
			if (p.getID() != 0x3a) throw new Exception("Bad pack type");
			int count = p.readVarInt();
			while((count--) != 0) bot.destroyEntity(p.readVarInt());
		}
	}
	//static class PackInRemoveEntityEffect implements Event{}
	//static class PackInResourcePackSend implements Event{}
	static class PackInRespawn implements PackEvent{
		public void event(Bot bot,Pack p) throws Exception{
			if (p.getID() != 0x3d) throw new Exception("Bad pack type");
			NBT.parseNBT(p);
			String name = p.readString();
			MinecraftWorld wId;
			if (name.compareTo("minecraft:the_end") == 0) wId = MinecraftWorld.mwTheEnd;
			else if (name.compareTo("minecraft:overworld") == 0) wId = MinecraftWorld.mwOverworld;
			else wId = MinecraftWorld.mwNethier;
			bot.world.respawn(wId);
		}
	}
	//static class PackInEntityHeadLook implements Event{}
	static class PackInMulitBlockChange implements PackEvent{
		public void event(Bot bot,Pack p) throws Exception{
			if (p.getID() != 0x3f) throw new Exception("Bad pack type");
			if(bot.worldLoadEnable == false) return;
			int cx, cy, cz;
			long c = p.readLong();
			cy = (int)(c & 0xfffff);
			cz = (int)((c >> 20) & 0x3fffff);
			cx = (int)((c >> 42) & 0x3fffff);
			if ((cy & 0x00080000) != 0) cy |= 0xfff00000;
			if ((cz & 0x00200000) != 0) cz |= 0xffc00000;
			if ((cx & 0x00200000) != 0) cx |= 0xffc00000;
			if(cy == -3 && cx == 4242 && cz == -4455) {
				int inBreak = 1;
				
			}
			cx <<= 4;
			cy <<= 4;
			cz <<= 4;
			p.readByte();
			int count = p.readVarInt();
			while((count--) != 0) {
				long v = p.readVarLong();
				int x = (int)((v >> 8) & 0x0f);
				int z = (int)((v >> 4) & 0x0f);
				int y = (int)(v & 0x0f);
				int val = (int)(v >> 12);
				//int org = bot.world.getBlock(x + cx, y + cy, z + cz);
				bot.world.setBlock(x + cx, y + cy, z + cz, val);
			}
		}
	}
	//static class PackInSelectAdvancementTab implements Event{}
	//static class PackInActionBar implements Event{}
	//static class PackInWorldBorderCenter implements Event{}
	//static class PackInWorldBorderLerpSize implements Event{}
	//static class PackInWorldBorderSize implements Event{}
	//static class PackInWorldBorderWarningDelay implements Event{}
	//static class PackInWorldBorderWarningReach implements Event{}
	//static class PackInCamera implements Event{}
	static class PackInHeldItemChange implements PackEvent{
		public void event(Bot bot,Pack p) throws Exception{
			if (p.getID() != 0x48) throw new Exception("Bad pack type");
			bot.item.holdSlot = p.readByte() + 27;
		}
	}
	//static class PackInUpdateViewPosition implements Event{}
	//static class PackInUpdateViewDistance implements Event{}
	//static class PackInSpawnPoint implements Event{}
	//static class PackInDisplayScoreboard implements Event{}
	//static class PackInEntityMetadata implements Event{}
	//static class PackInAttachEntity implements Event{}
	static class PackInEntityVelocity implements PackEvent{
		public void event(Bot bot,Pack p) throws Exception{
			if (p.getID() != 0x4f) throw new Exception("Bad pack type");
			int id = p.readVarInt();
			Entity en = bot.getEntity(id);
			if(en == null) return;
			en.vx = p.readShort();
			en.vy = p.readShort();
			en.vz = p.readShort();
			en.updateVelo = true;
		}
	};
	//static class PackInEntityEquipment implements Event{}
	//static class PackInSetExperience implements Event{}
	static class PackInUpdateHealth implements PackEvent{
		public void event(Bot bot,Pack p) throws Exception{
			if (p.getID() != 0x52) throw new Exception("Bad pack type");
			bot.health = p.readFloat();
			bot.food = p.readVarInt();
			bot.saturation = p.readFloat();
			bot.healthUpdate = true;
			if(bot.health <= 0) {
				bot.net.packOutClientStatus(0);
				if(bot.user.json != null && bot.user.json.containsKey("onRespawn")) {
					new Thread(()->{
						try {
							Thread.sleep(500);
							bot.chat.delayableCommand(bot.user.json.getString("onRespawn"), -1);
						} catch (Exception e) {}
					}).start();
				}
			}
		}
	}
	//static class PackInScoreboardObjective implements Event{}
	//static class PackInSetPassengers implements Event{}
	//static class PackInTerm implements Event{}
	//static class PackInUpdateScore implements Event{}
	//static class PackInUpdateSimulationDistance implements Event{}
	//static class PackInSetTitleSubtitle implements Event{}
	static class PackInTimeUpdate implements PackEvent{
		public void event(Bot bot, Pack p) throws Exception {
			long worldAge,prevAge;
			if (p.getID() != 0x59) throw new Exception("Bad pack type");
			worldAge = p.readLong();
			p.readLong();
			if(bot.prevUpdateTime != 0) {
				prevAge = bot.worldAge;
				bot.worldAge = worldAge;
				long timetmp;
				long curTime = timetmp = System.currentTimeMillis();
				curTime -= bot.prevUpdateTime;
				worldAge -= prevAge;
				bot.tickTime = (int)(curTime / worldAge);
				bot.prevUpdateTime = timetmp;
			}
			else bot.prevUpdateTime = System.currentTimeMillis();
		}
	}
	//static class PackInSetTitleText implements Event{}
	//static class PackInSetTitleTimes implements Event{}
	//static class PackInEntitySoundEffect implements Event{}
	//static class PackInSoundEffect implements Event{}
	//static class PackInStopSound implements Event{}
	//static class PackInPlayerListHeaderAndFooter implements Event{}
	//static class PackInNBTQueryResponse implements Event{}
	//static class PackInCollectItem implements Event{}
	static class PackInEntityTeleport implements PackEvent{
		public void event(Bot bot,Pack p) throws Exception{
			if (p.getID() != 0x62) throw new Exception("Bad pack type");
			int id = p.readVarInt();
			Entity en = bot.getEntity(id);
			if(en == null) return;
			en.x = p.readDouble();
			en.y = p.readDouble();
			en.z = p.readDouble();
			en.yaw = p.readByte();
			en.pitch = p.readByte();
		}
	}
	//class PackInAdvancements implements Event{}
	//class PackInEntityProperties implements Event{}
	//class PackInEntityEffect implements Event{}
	//class PackInDeclareRecipes implements Event{}
	//class PackInTags implements Event{}

	public static final PackEvent[] event = new PackEvent[] {
			new PackInSpawnEntity(),//0x00
			new PackInSpawnExpOrb(),
			new PackInSpawnLivingEntity(),
			null,
			new PackInSpawnPlayer(),
			null,
			null,
			null,
			new PackInAcknowledgePlayerDigging(),//0x08
			null,
			null,
			null,
			new PackInBlockChange(),
			null,
			null,
			new PackInChatMessage(),
			null,//0x10
			null,
			null,
			new PackInCloseWindow(),
			new PackInWindowItems(),
			null,
			new PackInSetSlot(),
			null,
			null,//0x18
			null,
			new PackInDisconnectPlay(),
			null,
			null,
			new PackInUnloadChunk(),
			null,
			null,
			null,//0x20
			new PackInKeepAlive(),
			new PackInChunkDataAndUpdateLight(),
			null,
			null,
			null,
			new PackInJoinGame(),
			null,
			new PackInTradeList(),//0x28
			new PackInEnityPosition(),
		    new PackInEnityPositionAndRotation(),
		    new PackInEntityRotation(),
			null,
			null,
			new PackInOpenWindow(),
			null,
			null,//0x30
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			new PackInPlayerPositionAndLook(),//0x38
			null,
			new PackInDestroyEntites(),
			null,
			null,
			new PackInRespawn(),
			null,
			new PackInMulitBlockChange(),
			null,//0x40
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			new PackInHeldItemChange(),//0x48
			null,
			null,
			null,
			null,
			null,
			null,
			new PackInEntityVelocity(),
			null,//0x50
			null,
			new PackInUpdateHealth(),
			null,
			null,
			null,
			null,
			null,
			null,//0x58
			new PackInTimeUpdate(),
			null,
			null,
			null,
			null,
			null,
			null,
			null,//0x60
			null,
			new PackInEntityTeleport(),
			null,
			null,
			null,
			null,
			null,
	};

}
