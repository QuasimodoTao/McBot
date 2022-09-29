package mc;

import util.NBT.NBTComp;
import util.NBT.NBTList;

public class Enchantment {
	public static final int bane_of_arthropods = 0;
	public static final int blast_protection = 1;
	public static final int depth_strider = 2;
	public static final int efficiency = 3;
	public static final int feather_falling = 4;
	public static final int fire_aspect = 5;
	public static final int fire_protection = 6;
	public static final int fortune = 7;
	public static final int frost_walker = 8;
	public static final int impaling = 9;
	public static final int knockback = 10;
	public static final int looting = 11;
	public static final int loyalty = 12;
	public static final int luck_of_the_sea = 13;
	public static final int lure = 14;
	public static final int piercing = 15;
	public static final int power = 16;
	public static final int projectile_protection = 17;
	public static final int protection = 18;
	public static final int punch = 19;
	public static final int quick_charge = 20;
	public static final int respiration = 21;
	public static final int riptide = 22;
	public static final int sharpness = 23;
	public static final int smite = 24;
	public static final int soul_speed = 25;
	public static final int sweeping = 26;
	public static final int thorns = 27;
	public static final int unbreaking = 28;
	
	public static final int aqua_affinity = 32;
	public static final int channeling = 33;
	public static final int binding_curse = 34;
	public static final int vanishing_curse = 35;
	public static final int flame = 36;
	public static final int infinity = 37;
	public static final int mending = 38;
	public static final int multishot = 39;
	public static final int silk_touch = 40;

	public static final byte[] max_level = 
			new byte[]{5,4,3,5,4,2,4,3,2,5,2,3,3,3,3,4,
					5,4,5,2,3,3,3,5,3,3,3,3,0,0,0,0 };
	
	
	public long enchantments = 0;
	public byte [] enchantmentsLvl = new byte[32];
	
	public Enchantment(NBTComp nc) {
		NBTList ec = null;
		if(nc.exist("Enchantments") == true) ec = nc.getList("Enchantments");
		else if(nc.exist("StoredEnchantments") == true) ec = nc.getList("StoredEnchantments");
		else return;
		
		if(ec.getElementType() != 10) return;
		for(int i = 0;i < ec.size();i++) {
			NBTComp ep = (NBTComp)ec.get(i);
			if(ep.exist("id") && ep.exist("lvl")) {
				String ench = ep.getString("id");
				int level = ep.getShort("lvl");
				int enchID = 63;
				
				if(ench.compareTo("minecraft:aqua_affinity") == 0) enchID = aqua_affinity;
				else if(ench.compareTo("minecraft:bane_of_arthropods") == 0) enchID = bane_of_arthropods;
				else if(ench.compareTo("minecraft:channeling") == 0) enchID = channeling;
				else if(ench.compareTo("minecraft:binding_curse") == 0) enchID = binding_curse;
				else if(ench.compareTo("minecraft:vanishing_curse") == 0) enchID = vanishing_curse;
				else if(ench.compareTo("minecraft:depth_strider") == 0) enchID = depth_strider;
				else if(ench.compareTo("minecraft:efficiency") == 0) enchID = efficiency;
				else if(ench.compareTo("minecraft:feather_falling") == 0) enchID = feather_falling;
				else if(ench.compareTo("minecraft:fire_aspect") == 0) enchID = fire_aspect;
				else if(ench.compareTo("minecraft:fire_protection") == 0) enchID = fire_protection;
				else if(ench.compareTo("minecraft:flame") == 0) enchID = flame;
				else if(ench.compareTo("minecraft:fortune") == 0) enchID = fortune;
				else if(ench.compareTo("minecraft:frost_walker") == 0) enchID = frost_walker;
				else if(ench.compareTo("minecraft:impaling") == 0) enchID = impaling;
				else if(ench.compareTo("infinity") == 0) enchID = infinity;
				else if(ench.compareTo("minecraft:knockback") == 0) enchID = knockback;
				else if(ench.compareTo("minecraft:looting") == 0) enchID = looting;
				else if(ench.compareTo("minecraft:loyalty") == 0) enchID = loyalty;
				else if(ench.compareTo("minecraft:luck_of_the_sea") == 0) enchID = luck_of_the_sea;
				else if(ench.compareTo("minecraft:lure") == 0) enchID = lure;
				else if(ench.compareTo("minecraft:mending") == 0) enchID = mending;
				else if(ench.compareTo("minecraft:multishot") == 0) enchID = multishot;
				else if(ench.compareTo("minecraft:piercing") == 0) enchID = piercing;
				else if(ench.compareTo("minecraft:power") == 0) enchID = power;
				else if(ench.compareTo("minecraft:projectile_protection") == 0) enchID = projectile_protection;
				else if(ench.compareTo("minecraft:protection") == 0) enchID = protection;
				else if(ench.compareTo("minecraft:punch") == 0) enchID = punch;
				else if(ench.compareTo("minecraft:quick_charge") == 0) enchID = quick_charge;
				else if(ench.compareTo("minecraft:respiration") == 0) enchID = respiration;
				else if(ench.compareTo("minecraft:riptide") == 0) enchID = riptide;
				else if(ench.compareTo("minecraft:sharpness") == 0) enchID = sharpness;
				else if(ench.compareTo("minecraft:silk_touch") == 0) enchID = silk_touch;
				else if(ench.compareTo("minecraft:smite") == 0) enchID = smite;
				else if(ench.compareTo("minecraft:soul_speed") == 0) enchID = soul_speed;
				else if(ench.compareTo("minecraft:sweeping") == 0) enchID = sweeping;
				else if(ench.compareTo("minecraft:thorns") == 0) enchID = thorns;
				else if(ench.compareTo("minecraft:unbreaking") == 0) enchID = unbreaking;
				else if(ench.compareTo("minecraft:blast_protection") == 0) enchID = blast_protection;
				enchantments |= 1L << enchID;
				if (enchID < 32) enchantmentsLvl[enchID] = (byte)level;
			}
			
		}
		
	}
	
	
	
	
}
