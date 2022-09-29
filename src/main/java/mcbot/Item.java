package mcbot;

import java.util.concurrent.atomic.AtomicBoolean;

import mc.Hand;
import mc.MinecraftItem;
import mc.MinecraftItemID;
import mc.MinecraftWindow;
import util.NBT.*;
import mc.Slot;
import mc.TradeList;
import util.Event;

public class Item {
	public volatile Slot[] inventorys;
	public volatile Slot[] slots;
	public volatile Slot[] equipments;
	public volatile Slot[] alterHand = new Slot[] {Slot.Empty};
	public volatile Slot selected = Slot.Empty;
	public int holdSlot = 0;
	public Event windowUpdate = new Event(false);
	public Event windowIsOpen = new Event();
	public volatile int windowState;
	public volatile int windowID;
	public volatile MinecraftWindow windowType = MinecraftWindow.none;
	public int selectedButton = 0;
	private AtomicBoolean slotLock = new AtomicBoolean(false);
	
	public volatile TradeList[] tradeList;
	public Event tradeListUpdate = new Event();
	public static final int inventory2Index[] = new int[] {
			9,18,27,36,45,54,9,3,2,3,5,10,2,3,3,5,0,4,3,27,3,3,3,2,1,9 
		};
	static final MinecraftItemID []canNotPlaceOnHead = new MinecraftItemID [] {
			MinecraftItemID.miElytra,
			MinecraftItemID.miLeatherChestplate,
			MinecraftItemID.miLeatherLeggings,
			MinecraftItemID.miLeatherBoots,
			MinecraftItemID.miChainmailChestplate,
			MinecraftItemID.miChainmailLeggings,
			MinecraftItemID.miChainmailBoots,
			MinecraftItemID.miIronChestplate,
			MinecraftItemID.miIronLeggings,
			MinecraftItemID.miIronBoots,
			MinecraftItemID.miDiamondChestplate,
			MinecraftItemID.miDiamondLeggings,
			MinecraftItemID.miDiamondBoots,
			MinecraftItemID.miGoldenChestplate,
			MinecraftItemID.miGoldenLeggings,
			MinecraftItemID.miGoldenBoots,
			MinecraftItemID.miNetheriteChestplate,
			MinecraftItemID.miNetheriteLeggings,
			MinecraftItemID.miNetheriteBoots,
			MinecraftItemID.miBow,
			MinecraftItemID.miMinecart,
			MinecraftItemID.miChestMinecart,
			MinecraftItemID.miFurnaceMinecart,
			MinecraftItemID.miHopperMinecart,
			MinecraftItemID.miCarrotOnAStick,
			MinecraftItemID.miWarpedFungusOnAStick,
			MinecraftItemID.miWoodenSword,
			MinecraftItemID.miWoodenShovel,
			MinecraftItemID.miWoodenPickaxe,
			MinecraftItemID.miWoodenAxe,
			MinecraftItemID.miWoodenHoe,
			MinecraftItemID.miStoneSword,
			MinecraftItemID.miStoneShovel,
			MinecraftItemID.miStonePickaxe,
			MinecraftItemID.miStoneAxe,
			MinecraftItemID.miStoneHoe,
			MinecraftItemID.miGoldenSword,
			MinecraftItemID.miGoldenShovel,
			MinecraftItemID.miGoldenPickaxe,
			MinecraftItemID.miGoldenAxe,
			MinecraftItemID.miGoldenHoe,
			MinecraftItemID.miIronSword,
			MinecraftItemID.miIronShovel,
			MinecraftItemID.miIronPickaxe,
			MinecraftItemID.miIronAxe,
			MinecraftItemID.miIronHoe,
			MinecraftItemID.miDiamondSword,
			MinecraftItemID.miDiamondShovel,
			MinecraftItemID.miDiamondPickaxe,
			MinecraftItemID.miDiamondAxe,
			MinecraftItemID.miDiamondHoe,
			MinecraftItemID.miNetheriteSword,
			MinecraftItemID.miNetheriteShovel,
			MinecraftItemID.miNetheritePickaxe,
			MinecraftItemID.miNetheriteAxe,
			MinecraftItemID.miNetheriteHoe,
			MinecraftItemID.miBowl,
			MinecraftItemID.miFishingRod,
			MinecraftItemID.miPiston,
			MinecraftItemID.miArmorStand,
			MinecraftItemID.miCommandBlockMinecart
	};
	
	private Bot bot = null;
	public static final int[] eqid = new int[] {
		    MinecraftItemID.miGoldenSword.ordinal(),
			MinecraftItemID.miGoldenShovel.ordinal(),
			MinecraftItemID.miGoldenPickaxe.ordinal(),
			MinecraftItemID.miGoldenAxe.ordinal(),
			MinecraftItemID.miGoldenHoe.ordinal(),
			MinecraftItemID.miIronSword.ordinal(),
			MinecraftItemID.miIronShovel.ordinal(),
			MinecraftItemID.miIronPickaxe.ordinal(),
			MinecraftItemID.miIronAxe.ordinal(),
			MinecraftItemID.miIronHoe.ordinal(),
			MinecraftItemID.miDiamondSword.ordinal(),
			MinecraftItemID.miDiamondShovel.ordinal(),
			MinecraftItemID.miDiamondPickaxe.ordinal(),
			MinecraftItemID.miDiamondAxe.ordinal(),
			MinecraftItemID.miDiamondHoe.ordinal(),
			MinecraftItemID.miNetheriteSword.ordinal(),
			MinecraftItemID.miNetheriteShovel.ordinal(),
			MinecraftItemID.miNetheritePickaxe.ordinal(),
			MinecraftItemID.miNetheriteAxe.ordinal(),
			MinecraftItemID.miNetheriteHoe.ordinal(),
			
			MinecraftItemID.miGoldenHelmet.ordinal(),
			MinecraftItemID.miGoldenChestplate.ordinal(),
			MinecraftItemID.miGoldenLeggings.ordinal(),
			MinecraftItemID.miGoldenBoots.ordinal(),
		    MinecraftItemID.miIronHelmet.ordinal(),
			MinecraftItemID.miIronChestplate.ordinal(),
			MinecraftItemID.miIronLeggings.ordinal(),
			MinecraftItemID.miIronBoots.ordinal(),
			MinecraftItemID.miDiamondHelmet.ordinal(),
			MinecraftItemID.miDiamondChestplate.ordinal(),
			MinecraftItemID.miDiamondLeggings.ordinal(),
			MinecraftItemID.miDiamondBoots.ordinal(),
			MinecraftItemID.miNetheriteHelmet.ordinal(),
			MinecraftItemID.miNetheriteChestplate.ordinal(),
			MinecraftItemID.miNetheriteLeggings.ordinal(),
			MinecraftItemID.miNetheriteBoots.ordinal(),
			MinecraftItemID.miElytra.ordinal(),
			MinecraftItemID.miFishingRod.ordinal()
	};
	public static final int[] eqdu = new int[] {
			32,32,32,32,32,
			250,250,250,250,
			1561,1561,1561,1561,1561,
			2031,2031,2031,2031,2031,
			77,112,105,91,
			165,240,225,195,
			363,528,495,429,
			407,592,555,481,
			432,64
	};
	public static int getDelable(int itemID) {
		for(int i = 0;i < eqid.length && i < eqdu.length;i++) {
			if(eqid[i] == itemID) return eqdu[i];
		}
		return -1;
	}
	public void lockSlot() throws InterruptedException {
		while(slotLock.compareAndSet(false, true) == false) Thread.sleep(50);
	}
	public void unlockSlot() {
		slotLock.set(false);
	}
	public Item(Bot bot) {
		this.bot = bot;
		inventorys = new Slot[36];
		for(int i = 0;i < 36;i++) inventorys[i] = Slot.Empty;
		slots = new Slot[54];
		for(int i = 0;i < 54;i++) slots[i] = Slot.Empty;
		equipments = new Slot[4];
		for(int i = 0;i < 4;i++) equipments[i] = Slot.Empty;
	}
	public void clear() {
		int i;
		for (i = 0; i < 36; i++) inventorys[i] = Slot.Empty;
		for (i = 0; i < 4; i++) equipments[i] = Slot.Empty;
		for (i = 0; i < 54; i++) slots[i] = Slot.Empty;
		alterHand[0] = Slot.Empty;
		tradeList = null;
		windowType = MinecraftWindow.none;
		windowID = 0;
	}
	public void dropHoldItemStack() throws Exception{
		bot.swingArm(Hand.hMain);
		bot.net.packOutDig(3, 0, 0, 0, 0);
	}
	public void dropHoldItem() throws Exception{
		bot.swingArm(Hand.hMain);
		bot.net.packOutDig(4, 0, 0, 0, 0);
	}
	public void swapItemInHand() throws Exception{
		bot.net.packOutDig(6, 0, 0, 0, 0);
	}
	public void clickWindow(int slot) throws Exception{
		lockSlot();
		Slot[] clicked;
		int clickedIndex;
		if (windowType == MinecraftWindow.none) {
			if (slot == 4) {
				unlockSlot();
				return;
			}
			else if (slot < 5) {
				clicked = this.slots;
				clickedIndex = slot;
			}
			else if (slot >= 9 && slot < 45) {
				clicked =  this.inventorys;
				clickedIndex = slot - 9;
			}
			else if (slot == 45) {
				clicked = alterHand;
				clickedIndex = 0;
			}
			else if (!selected.present) {
				clicked = this.equipments;
				clickedIndex = slot - 5;
			}
			else {
				switch (slot) {
				case 5:
					for (int i = 0; i < canNotPlaceOnHead.length; i++) {
						if (selected.id == canNotPlaceOnHead[i].ordinal()) {
							unlockSlot();
							return;
						}
					}
					break;
				case 6:
					switch (MinecraftItemID.values()[selected.id]) {
					case miLeatherChestplate:
					case miChainmailChestplate:
					case miIronChestplate:
					case miDiamondChestplate:
					case miGoldenChestplate:
					case miNetheriteChestplate:
					case miElytra:
						break;
					default:
						unlockSlot();
						return;
					}
					break;
				case 7:
					switch (MinecraftItemID.values()[selected.id]) {
					case miLeatherLeggings:
					case miChainmailLeggings:
					case miIronLeggings:
					case miDiamondLeggings:
					case miGoldenLeggings:
					case miNetheriteLeggings:
						break;
					default:
						unlockSlot();
						return;
					}
					break;
				case 8:
					switch (MinecraftItemID.values()[selected.id]) {
					case miLeatherBoots:
					case miChainmailBoots:
					case miIronBoots:
					case miDiamondBoots:
					case miGoldenBoots:
					case miNetheriteBoots:
						break;
					default:
						unlockSlot();
						return;
					}
					break;
				}
				clicked = equipments;
				clickedIndex = slot - 5;
			}
		}
		else {
			if (slot < inventory2Index[windowType.ordinal()]) {
				switch (windowType) {
				case anvil:
				case enchantmentTable:
				case smithingTable:
					unlockSlot();
					return;
				case villager:
					if (slot == 2) {
						if (!this.slots[2].present) {
							unlockSlot();
							return;
						}
						if (selected.present) {
							if ((selected.id != this.slots[2].id) || (selected.nbt.isEqual(this.slots[2].nbt) == false)) {
								unlockSlot();
								return;
							}
						}
						if (!this.slots[0].present) {
							unlockSlot();
							return;
						}
						if (tradeList[selectedButton].input1.count <= this.slots[0].count) {
							int[] ic;
							Slot[] sc;
							if (tradeList[selectedButton].input2.present) {
								if (!this.slots[1].present ||
									tradeList[selectedButton].input2.count < this.slots[1].count) {
									unlockSlot();
									return;
								}
								else {
									this.slots[0].count -= tradeList[selectedButton].input1.count;
									if (this.slots[0].count == 0) this.slots[0] = Slot.Empty;
									this.slots[1].count -= tradeList[selectedButton].input2.count;
									if (this.slots[1].count == 0) this.slots[1] = Slot.Empty;
									if (selected.present) {
										if ((selected.count + this.slots[2].count) >= MinecraftItem.item[selected.id].stack) {
											unlockSlot();
											return;
										}
										selected.count += this.slots[2].count;
									}
									else this.selected = this.slots[2].dup();
									
									if (tradeList[selectedButton].input2.count < this.slots[1].count) {
										//slot[0],slot[1]
										ic = new int[] {0,1};
										sc = new Slot[] {this.slots[0],this.slots[1]};
									}
									else {
										//slot[0],slot[1],slot[2]
										this.slots[2] = Slot.Empty;
										ic = new int[] {0,1,2};
										sc = new Slot[] {this.slots[0],this.slots[1],Slot.Empty};
									}
								}
							}
							else {
								if (selected.present) {
									if ((selected.count + this.slots[2].count) >= MinecraftItem.item[selected.id].stack) {
										unlockSlot();
										return;
									}
									selected.count += this.slots[2].count;
								}
								else this.selected = this.slots[2].dup();
								this.slots[0].count -= tradeList[selectedButton].input1.count;
								if (this.slots[0].count == 0) this.slots[0] = Slot.Empty;
								if (!this.slots[0].present || tradeList[selectedButton].input1.count < this.slots[0].count) {
									//slot[0]
									ic = new int[] {0};
									sc = new Slot[] {this.slots[0]};
								}
								else {
									//slot[0],slot[2]
									ic = new int[] {0,2};
									sc = new Slot[] {this.slots[0],this.slots[2]};
								}
							}
							bot.net.packOutClickWindow(windowID, windowState, slot, 0, 0, ic, sc, this.selected);
						}
						unlockSlot();
						return;
					}
					clicked = this.slots;
					clickedIndex = slot;
					break;
				default:
					clicked = this.slots;
					clickedIndex = slot;
					break;
				}
			}
			else if (slot < inventory2Index[windowType.ordinal()] + 36) {
				clicked = this.inventorys;
				clickedIndex = slot - inventory2Index[windowType.ordinal()];
			}
			else {
				unlockSlot();
				return;
			}
		}
		if (clicked[clickedIndex].present && selected.present) {
			NBT n1 = clicked[clickedIndex].nbt;
			NBT n2 = selected.nbt;
			if (clicked[clickedIndex].id == selected.id && n1.isEqual(n2)) {
				int stack = MinecraftItem.item[selected.id].stack;
				if (stack == 1) {
					Slot tmp = selected;
					selected = clicked[clickedIndex];
					clicked[clickedIndex] = tmp;
				}
				else if (clicked[clickedIndex].count + selected.count > stack) {
					selected.count -= stack - clicked[clickedIndex].count;
					clicked[clickedIndex].count = stack;
				}
				else {
					clicked[clickedIndex].count += selected.count;
					selected = Slot.Empty;
				}
			}
			else {
				Slot tmp = selected;
				selected = clicked[clickedIndex];
				clicked[clickedIndex] = tmp;
			}
		}
		else {
			Slot tmp = selected;
			selected = clicked[clickedIndex];
			clicked[clickedIndex] = tmp;
		}
		int [] _s = new int[] {slot};
		Slot [] _s2 = new Slot[] {clicked[clickedIndex]};
		bot.net.packOutClickWindow(windowID, windowState, slot, 0, 0, _s, _s2, selected);
		unlockSlot();
	}
	public void clickInventory(int slot) throws Exception {
		if (slot < 36) clickWindow(slot + inventory2Index[windowType.ordinal()]);
	}
	public void clickSlot(int slot) throws Exception {
		if (slot < inventory2Index[windowType.ordinal()]) clickWindow(slot);
	}
	public void clickAlterHandle() throws Exception {
		if (windowType == MinecraftWindow.none) clickWindow(45);
	}
	public void clickEquipment(int slot) throws Exception {
		if ((windowType == MinecraftWindow.none) && (slot < 4)) clickWindow(slot + 5);
	}
	public void dropSelectedItem() throws Exception {
		lockSlot();
		if (selected.present) {
			selected.count--;
			if (selected.count == 0) selected = Slot.Empty;
			bot.swingArm(Hand.hMain);
			bot.net.packOutClickWindow(windowID, windowState, -999, 1, 1, null, null, selected);
		}
		unlockSlot();
	}
	public void dropSelectedItemStack() throws Exception {
		lockSlot();
		if (selected.present) {
			selected = Slot.Empty;
			bot.swingArm(Hand.hMain);
			bot.net.packOutClickWindow(windowID, windowState, -999, 0, 1, null, null, Slot.Empty);
		}
		unlockSlot();
	}
	public void dropItem(int slot) throws Exception {
		lockSlot();
		Slot[] clicked;
		int clickedIndex;
		if (windowType == MinecraftWindow.none) {
			if (slot < 5) {
				clicked = this.slots;
				clickedIndex = slot;
			}
			else if (slot >= 9 && slot < 45) {
				clicked = this.inventorys;
				clickedIndex = slot - 9;
			}
			else if (slot == 45) {
				clicked = alterHand;
				clickedIndex = 0;
			}
			else {
				clicked = equipments;
				clickedIndex = slot - 5;
			}
		}
		else {
			if (slot < inventory2Index[windowType.ordinal()]) {
				clicked = this.slots;
				clickedIndex = slot;
			}
			else if (slot < inventory2Index[windowType.ordinal()] + 36) {
				clicked = this.inventorys;
				clickedIndex = slot - inventory2Index[windowType.ordinal()];
			}
			else {
				unlockSlot();
				return;
			}
		}
		if (clicked[clickedIndex].present) {
			clicked[clickedIndex].count--;
			if (clicked[clickedIndex].count == 0) clicked[clickedIndex] = Slot.Empty;
			bot.swingArm(Hand.hMain);
			int [] is = new int[] {slot};
			Slot [] ss = new Slot[] {clicked[clickedIndex]};
			bot.net.packOutClickWindow(windowID, windowState, slot, 0, 4, is, ss, selected);
		}
		unlockSlot();
	}
	public void dropItemStack(int slot) throws Exception {
		lockSlot();
		Slot[] clicked;
		int clickedIndex;
		if (windowType == MinecraftWindow.none) {
			if (slot < 5) {
				clicked = this.slots;
				clickedIndex = slot;
			}
			else if (slot >= 9 && slot < 45) {
				clicked = this.inventorys;
				clickedIndex = slot - 9;
			}
			else if (slot == 45) {
				clicked = alterHand;
				clickedIndex = 0;
			}
			else {
				clicked = equipments;
				clickedIndex = slot - 5;
			}
		}
		else {
			if (slot < inventory2Index[windowType.ordinal()]) {
				clicked = this.slots;
				clickedIndex = slot;
			}
			else if (slot < inventory2Index[windowType.ordinal()] + 36) {
				clicked = this.inventorys;
				clickedIndex = slot - inventory2Index[windowType.ordinal()];
			}
			else {
				unlockSlot();
				return;
			}
		}
		if (clicked[clickedIndex].present) {
			clicked[clickedIndex] = Slot.Empty;
			bot.swingArm(Hand.hMain);
			int [] is = new int[] {slot};
			Slot [] ss = new Slot[] {Slot.Empty};
			bot.net.packOutClickWindow(windowID, windowState, slot, 1, 4, is, ss, selected);
		}
		unlockSlot();
	}
	public void dropSlotItem(int slot) throws Exception {
		if (slot < inventory2Index[windowType.ordinal()]) dropItem(slot);
	}
	public void dropSlotItemStack(int slot) throws Exception {
		if (slot < inventory2Index[windowType.ordinal()]) dropItemStack(slot);
	}
	public void dropEquipmentItem(int slot) throws Exception {
		if (windowType == MinecraftWindow.none && slot < 4) dropItem(slot + 4);
	}
	public void dropEquipmentItemStack(int slot) throws Exception {
		if (windowType == MinecraftWindow.none && slot < 4) dropItemStack(slot + 4);
	}
	public void dropInventoryItem(int slot) throws Exception {
		if (slot < 36) dropItem(slot + inventory2Index[windowType.ordinal()]);
	}
	public void dropInventoryItemStack(int slot) throws Exception {
		if (slot < 36) dropItemStack(slot + inventory2Index[windowType.ordinal()]);
	}
	public void dropAlterHand() throws Exception {
		if (windowType == MinecraftWindow.none) dropItem(45);
	}
	public void dropAlterHandStack() throws Exception {
		if (windowType == MinecraftWindow.none) dropItemStack(45);
	}
	public boolean isSelectingItem() {
		return selected.present;
	}
	public boolean waitForTrade(int id, int outOfTime) throws Exception,InterruptedException {
		windowUpdate.clearEvent();
		windowIsOpen.clearEvent();
		tradeListUpdate.clearEvent();
		bot.interactEntity(id, Hand.hMain);
		if (outOfTime < 0) {
			windowUpdate.waitEvent();
			windowIsOpen.waitEvent();
			tradeListUpdate.waitEvent();
		}
		else if (outOfTime > 0){ 
			if(windowUpdate.waitEvent(outOfTime) == false) return false;
			if(windowIsOpen.waitEvent(outOfTime) == false) return false;
			if(tradeListUpdate.waitEvent(outOfTime) == false) return false;
		}
		return true;
	}
	public boolean waitForOpenWindow(int bx, int by, int bz, int outOfTime) throws Exception,InterruptedException {
		windowUpdate.clearEvent();
		windowIsOpen.clearEvent();
		bot.net.packOutPlaceBlock(Hand.hMain.ordinal(), bx, by, bz, 0, 0, 0, 0, false);
		if (outOfTime < 0){ 
			windowUpdate.waitEvent();
			windowIsOpen.waitEvent();
		}
		else if (outOfTime > 0) {
			if(windowUpdate.waitEvent(outOfTime) == false) return false;
			if(windowIsOpen.waitEvent(outOfTime) == false) return false;
		}
		return true;
	}
	public void selectTrade(int slot) throws Exception {
		if (tradeList == null) return;
		if (windowType != MinecraftWindow.villager) return;
		if (tradeList.length <= slot) return;
		bot.net.packOutSelectTrade(slot);
		selectedButton = slot;
	}
	public void clickWindowButton(int slot) throws Exception {
		bot.net.packOutClickWinButton(windowID, slot);
		selectedButton = slot;
	}
	public void holdItemChange(int slot) throws Exception{
		if (slot < 9 && slot >= 0) {
			if (slot + 27 == holdSlot) return;
			holdSlot = slot + 27;
			bot.net.packOutHeldItemChange(slot);
		}
		else if(slot <36 && slot >= 27) {
			if (slot == holdSlot) return;
			holdSlot = slot;
			bot.net.packOutHeldItemChange(slot - 27);
		}
	}
	public boolean swapInventory(Hand hand, int slot,int hotbar) throws Exception {
		if (windowType != MinecraftWindow.none) return false;
		if (hand == Hand.hAlter) hotbar = 40;
		else if (hotbar < 0 || hotbar >= 9) return false;
		if (slot >= 36) return false;

		lockSlot();
		Slot[] swaped;
		int swapedIndex;
		if (hand == Hand.hAlter) {
			swaped = alterHand;
			swapedIndex = 0;
		}
		else {
			swaped = this.inventorys;
			swapedIndex = 27 + hotbar;
		}
		Slot s = swaped[swapedIndex];
		swaped[swapedIndex] = this.inventorys[slot];
		this.inventorys[slot] = s;
		Slot[] sl = new Slot[] {swaped[swapedIndex],this.inventorys[slot]};
		int[] si = new int[] {(hand == Hand.hAlter) ? 45 : hotbar + 36,slot + 9};
		unlockSlot();
		bot.net.packOutClickWindow(windowID, windowState, slot + 9, hotbar, 2, si, sl, selected);
		return true;
	}
	public int findItemOnFastSlot(int item) throws InterruptedException {
		lockSlot();
		for (int i = 27; i < 36; i++) 
			if (inventorys[i].present && inventorys[i].id == item) { 
				unlockSlot();
				return i - 27;
			}
		unlockSlot();
		return -1;
	}
	public int findOutItemInInventory(int item) throws InterruptedException {
		int min = 64;
		int s = -1;
		lockSlot();
		for (int i = 0; i < 36; i++) {
			if (inventorys[i].present && inventorys[i].id == item) { 
				if (s == -1 || inventorys[i].count < min) {
					s = i;
					min = inventorys[i].count;
				}
			}
		}
		unlockSlot();
		return s;
	}
	public int counterItemInInventory(int item,boolean alter) throws InterruptedException {
		int count = 0;
		lockSlot();
		for (int i = 0; i < 36; i++) {
			if (inventorys[i].present && inventorys[i].id == item) count += inventorys[i].count;
		}
		if (alter && alterHand[0].present && alterHand[0].id == item) count += alterHand[0].count;
		unlockSlot();
		return count;
	}
	public int counterEmptyInInventory(boolean alter) throws InterruptedException {
		int count = 0;
		lockSlot();
		if (alter && !alterHand[0].present) count++;
		
		for (int i = 0; i < 36; i++) if (!inventorys[i].present) count++;
		unlockSlot();
		return count;
	}
	public static final int[] foods = new int[]{
			//MinecraftItem::miEnchantedGoldenApple,
			MinecraftItemID.miGoldenApple.ordinal(),
			MinecraftItemID.miGoldenCarrot.ordinal(),

			MinecraftItemID.miCookedPorkchop.ordinal(),
			MinecraftItemID.miCookedCod.ordinal(),
			MinecraftItemID.miCookedSalmon.ordinal(),
			MinecraftItemID.miCookedBeef.ordinal(),
			MinecraftItemID.miCookedChicken.ordinal(),
			MinecraftItemID.miCookedRabbit.ordinal(),
			MinecraftItemID.miCookedMutton.ordinal(),
			MinecraftItemID.miRabbitStew.ordinal(),
			
			MinecraftItemID.miPorkchop.ordinal(),
			MinecraftItemID.miCod.ordinal(),
			MinecraftItemID.miSalmon.ordinal(),
			MinecraftItemID.miBeef.ordinal(),
			MinecraftItemID.miRabbit.ordinal(),
			MinecraftItemID.miMutton.ordinal(),

			MinecraftItemID.miApple.ordinal(),
			MinecraftItemID.miBread.ordinal(),
			MinecraftItemID.miCookie.ordinal(),
			MinecraftItemID.miMelonSlice.ordinal(),
			MinecraftItemID.miMushroomStew.ordinal(),
			MinecraftItemID.miDriedKelp.ordinal(),
			MinecraftItemID.miCarrot.ordinal(),
			MinecraftItemID.miPotato.ordinal(),
			MinecraftItemID.miBakedPotato.ordinal(),
			MinecraftItemID.miPumpkinPie.ordinal(),
			MinecraftItemID.miBeetroot.ordinal(),
			MinecraftItemID.miBeetrootSoup.ordinal(),
			MinecraftItemID.miSweetBerries.ordinal(),
			MinecraftItemID.miGlowBerries.ordinal(),
			MinecraftItemID.miHoneyBottle.ordinal(),

			MinecraftItemID.miChicken.ordinal(),
			MinecraftItemID.miRottenFlesh.ordinal(),
			0,
		};
	public int findOutFastFood() throws InterruptedException {
		boolean[] vaild = new boolean[]{ false,false,false,false,false,false,false,false,false};
		lockSlot();
		for (int i = 27; i < 36; i++)
			if (inventorys[i].present) {
				vaild[i - 27] = true;
			}
		for (int i = 0; i < foods.length; i++) {
			if (foods[i] == 0) break;
			for (int j = 0; j < 9; j++) {
				if (vaild[j] == false) continue;
				if (inventorys[27 + j].id == foods[i]) {
					unlockSlot();
					return j;
				}
			}
		}
		unlockSlot();
		return -1;
	}
	public int cacuPrice(int slot) {
		int price;

		if (tradeList == null) return -1;
		if (slot >= tradeList.length) return -1;
		if (tradeList[slot].demand > 0) {
			int dem = (int)Math.floor(tradeList[slot].input1.count *
				tradeList[slot].priceMultipiler *
				tradeList[slot].demand);
			if (dem < 0) dem = 0;
			price = tradeList[slot].input1.count +dem;
			if (price < 1) price = 1;
			else if (price > MinecraftItem.item[tradeList[slot].input1.id].stack) 
				price = MinecraftItem.item[tradeList[slot].input1.id].stack;
			return price;
		}
		else return tradeList[slot].input1.count;
	}
	void putDownItemInInventory() throws Exception,InterruptedException {
		lockSlot();
		if (!selected.present) {
			unlockSlot();
			return;
		}
		for (int i = 0; i < 36; i++) {
			if (!inventorys[i].present ||
				inventorys[i].id != selected.id ||
				inventorys[i].count >= MinecraftItem.item[inventorys[i].id].stack ||
				!selected.nbt.isEqual(inventorys[i].nbt)) continue;
			unlockSlot();
			clickInventory(i);
			Thread.sleep(100);
			lockSlot();
			if (!selected.present) {
				unlockSlot();
				return;
			}
		}
		for (int i = 0; i < 36; i++) {
			if (!inventorys[i].present) {
				unlockSlot();
				clickInventory(i);
				return;
			}
		}
		unlockSlot();
		dropSelectedItemStack();
	}
	public int findTradeSlot(MinecraftItemID in0, MinecraftItemID out) {
		return findTradeSlot(in0,out,MinecraftItemID.miAir);
	}
	public int findTradeSlot(MinecraftItemID in0, MinecraftItemID out, MinecraftItemID in1) {
		if (tradeList == null) return -1;
		for (int i = 0; i < tradeList.length; i++) {
			if (tradeList[i].input1.id == in0.ordinal() && tradeList[i].output.id == out.ordinal()) {
				if (tradeList[i].input2.present) {
					if (tradeList[i].input2.id == in1.ordinal()) return i;
				}
				else return i;
			}
		}
		return -1;
	}
	public void closeWindow() throws Exception {
		lockSlot();
		bot.net.packOutCloseWindow(windowID);
		windowID = 0;
		windowType = MinecraftWindow.none;
		unlockSlot();
	}
	public Slot getHoldItem() {
		return inventorys[holdSlot];
	}
}
