package mcbot;

import mc.Enchantment;
import mc.Entity;
import mc.Hand;
import mc.MinecraftItem;
import mc.MinecraftItemID;

public class FishMan extends ComplexBot{
	private Bot bot;
	private volatile long lastFishTime;
	
	public FishMan(Bot bot) {
		this.bot = bot;
	}
	
	class FishMan2 extends Thread{
		public void run() {
			boolean st = false;
			int []rubbishDrop = new int[36];
			int rubbishCount;
			int[] treasureDrop = new int[36];
			int treasureCount;
			
			try {
				lastFishTime = System.currentTimeMillis();
				while(true) {
					float orgYaw = bot.yaw;
					float orgPitch = bot.pitch;
					int slot = bot.item.findItemOnFastSlot(MinecraftItemID.miFishingRod.ordinal());
					if(slot < 0) {
						sleep(1000);
						continue;
					}
					if(!bot.item.inventorys[slot + 27].present) {
						sleep(1000);
						continue;
					}
					if(bot.item.inventorys[slot + 27].damage > 60) {
						sleep(1000);
						continue;
					}
					if(st == false) bot.rotation(bot.yaw,12);
					else bot.rotation(bot.yaw,45);
					st = !st;
					bot.item.holdItemChange(slot);
					sleep(50);
					bot.useItem(Hand.hMain);
					rubbishCount = 0;
					treasureCount = 0;
					bot.item.lockSlot();
					for(int i = 0;i < 36;i++) {
						boolean rubbish = false;
						if(!bot.item.inventorys[i].present) continue;
						if(bot.item.inventorys[i].id == MinecraftItemID.miFishingRod.ordinal()) continue;
						if(MinecraftItem.item[bot.item.inventorys[i].id].stack == 1){
							Enchantment ench = bot.item.inventorys[i].ench;
							int enConfilet = 0;
							int toolConfilet = 0;
							int wep = 0;
							int bow = 0;
							int soul = 0;
							if(ench != null) {
								if((ench.enchantments & (1L << Enchantment.binding_curse)) != 0) rubbish = true;
								else if(bot.item.inventorys[i].id == MinecraftItemID.miIronBoots.ordinal() ||
										bot.item.inventorys[i].id == MinecraftItemID.miIronChestplate.ordinal() ||
										bot.item.inventorys[i].id == MinecraftItemID.miIronHelmet.ordinal() ||
										bot.item.inventorys[i].id == MinecraftItemID.miIronLeggings.ordinal()){
									if((ench.enchantments & (1L << Enchantment.blast_protection)) != 0 && 
										ench.enchantmentsLvl[Enchantment.blast_protection] >= 4) enConfilet++;
									if((ench.enchantments & (1L << Enchantment.fire_protection)) != 0 && 
										ench.enchantmentsLvl[Enchantment.fire_protection] >= 4) enConfilet++;
									if((ench.enchantments & (1L << Enchantment.projectile_protection)) != 0 && 
										ench.enchantmentsLvl[Enchantment.projectile_protection] >= 4) enConfilet++;
									if((ench.enchantments & (1L << Enchantment.protection)) != 0 && 
										ench.enchantmentsLvl[Enchantment.protection] >= 4) enConfilet++;
									if(enConfilet < 3) rubbish = true;
								}
								else if(bot.item.inventorys[i].id == MinecraftItemID.miIronPickaxe.ordinal()) rubbish = true;
								else if(bot.item.inventorys[i].id == MinecraftItemID.miIronAxe.ordinal() ||
									bot.item.inventorys[i].id == MinecraftItemID.miIronSword.ordinal()) {
									int level = 0;
									if((ench.enchantments & (1L << Enchantment.sharpness)) != 0 && 
										ench.enchantmentsLvl[Enchantment.sharpness] >= 4) {
										level = ench.enchantmentsLvl[Enchantment.sharpness];
										wep++;
									}
									if((ench.enchantments & (1L << Enchantment.bane_of_arthropods)) != 0 && 
										ench.enchantmentsLvl[Enchantment.bane_of_arthropods] >= 4) {
										level = ench.enchantmentsLvl[Enchantment.bane_of_arthropods];
										wep++;
									}
									if((ench.enchantments & (1L << Enchantment.smite)) != 0 && 
										ench.enchantmentsLvl[Enchantment.smite] >= 4) wep++; 
									if(wep < 2) rubbish = true;
									else if(wep == 2 && level < 5) rubbish = true;
								}
								else {
									if((ench.enchantments & (1L << Enchantment.blast_protection)) != 0 && 
										ench.enchantmentsLvl[Enchantment.blast_protection] >= 4) enConfilet++;
									if((ench.enchantments & (1L << Enchantment.fire_protection)) != 0 && 
										ench.enchantmentsLvl[Enchantment.fire_protection] >= 4) enConfilet++;
									if((ench.enchantments & (1L << Enchantment.projectile_protection)) != 0 && 
										ench.enchantmentsLvl[Enchantment.projectile_protection] >= 4) enConfilet++;
									if((ench.enchantments & (1L << Enchantment.protection)) != 0 && 
										ench.enchantmentsLvl[Enchantment.protection] >= 4) enConfilet++;
									
									if((ench.enchantments & (1L << Enchantment.fortune)) != 0) toolConfilet++;
									if((ench.enchantments & (1L << Enchantment.silk_touch)) != 0) toolConfilet++;
									if((ench.enchantments & (1L << Enchantment.sharpness)) != 0 && 
										ench.enchantmentsLvl[Enchantment.sharpness] >= 4) wep++;
									if((ench.enchantments & (1L << Enchantment.bane_of_arthropods)) != 0 && 
										ench.enchantmentsLvl[Enchantment.bane_of_arthropods] >= 4) wep++;
									if((ench.enchantments & (1L << Enchantment.smite)) != 0 && 
										ench.enchantmentsLvl[Enchantment.smite] >= 4) wep++; 
									
									if((ench.enchantments & (1L << Enchantment.infinity)) != 0) bow++;
									if((ench.enchantments & (1L << Enchantment.mending)) != 0) bow++;
									if((ench.enchantments & (1L << Enchantment.soul_speed)) != 0 &&
										ench.enchantmentsLvl[Enchantment.soul_speed] >= 3) soul = 1;
									if(toolConfilet < 2 && enConfilet < 2 && wep < 2 && bow < 2 && soul < 1) rubbish = true;
									
								}
							}
							else rubbish = true;
						}
						else {
							if(bot.item.inventorys[i].id != MinecraftItemID.miGoldIngot.ordinal() &&
								bot.item.inventorys[i].id != MinecraftItemID.miNetheriteScrap.ordinal() &&
								bot.item.inventorys[i].id != MinecraftItemID.miDiamond.ordinal() &&
								bot.item.inventorys[i].id != MinecraftItemID.miLapisLazuli.ordinal() &&
								bot.item.inventorys[i].id != MinecraftItemID.miGhastTear.ordinal() &&
								bot.item.inventorys[i].id != MinecraftItemID.miBlazeRod.ordinal()) rubbish = true;
						}
						if(rubbish) {
							rubbishDrop[rubbishCount] = i;
							rubbishCount++;
						}
						else {
							treasureDrop[treasureCount] = i;
							treasureCount++;
						}
					}
					bot.item.unlockSlot();
					int sleepTime = 0;
					if(rubbishCount != 0) {
						sleepTime += 200;
						sleep(100);
						bot.rotation(orgYaw + 90,orgPitch);
						sleep(100);
						bot.rotation(orgYaw + 90,0);
						while(rubbishCount != 0) {
							rubbishCount--;
							bot.item.dropInventoryItemStack(rubbishDrop[rubbishCount]);
							sleep(100);
							sleepTime += 100;
						}
					}
					if(sleepTime > 200) {
						sleep(500);
						sleepTime += 500;
					}
					if(treasureCount != 0) {
						bot.rotation(orgYaw - 90,orgPitch);
						sleep(100);
						bot.rotation(orgYaw - 90,45);
						sleepTime += 100;
						while(treasureCount != 0) {
							treasureCount--;
							bot.item.dropInventoryItemStack(treasureDrop[treasureCount]);
							sleep(100);
							sleepTime += 100;
						}
					}
					bot.rotation(orgYaw,orgPitch);
					sleep(200);
					sleepTime += 200;
					if(sleepTime < 3000) sleep(3000 - sleepTime);
					Entity en = bot.getEntity(bot.fishFloatID);
					if(en == null) {
						slot = bot.item.holdSlot;
						if(slot == 27) {
							bot.item.holdItemChange(2);
							Thread.sleep(500);
							bot.item.holdItemChange(0);
						}
						else {
							bot.item.holdItemChange(0);
							Thread.sleep(500);
							bot.item.holdItemChange(slot - 27);
						}
						lastFishTime = System.currentTimeMillis();
						continue;
					}
					int lastY = en.vy;
					en.updateVelo = false;
					while(true) {
						sleep(50);
						if(en.updateVelo == false)continue;
						en.updateVelo = false;
						if(en.vy > 350 || en.vy < -350) {
							bot.useItem(Hand.hMain);
							lastFishTime = System.currentTimeMillis();
							break;
						}
						else if(lastY == 0 && en.vy == 0) {
							bot.useItem(Hand.hMain);
							lastFishTime = System.currentTimeMillis();
							break;
						}
						else lastY = en.vy;
					}
					sleep(1000);
				}
			} catch (InterruptedException e) {
				return;
			}
			catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

	public void run() {
		FishMan2 fishMan = new FishMan2();
		try {
			//bot.chat.loginEvent.waitEvent();
			bot.chat.homeCommand(2000000);
			while(true) {
				try {
					fishMan.start();
				} catch(Exception e) {
					e.printStackTrace();
				}
				lastFishTime = System.currentTimeMillis();
				while(true) {
					Thread.sleep(1000);
					if(System.currentTimeMillis() - lastFishTime > 30000) {
						fishMan.interrupt();
						fishMan.join();
						fishMan = new FishMan2();
						int slot = bot.item.holdSlot;
						if(slot == 27) {
							bot.item.holdItemChange(2);
							Thread.sleep(500);
							bot.item.holdItemChange(0);
						}
						else {
							bot.item.holdItemChange(0);
							Thread.sleep(500);
							bot.item.holdItemChange(slot - 27);
						}
						break;
					}
				}
			}
		} catch (InterruptedException e) {
			fishMan.interrupt();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
}
