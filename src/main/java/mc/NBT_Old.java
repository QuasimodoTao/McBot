package mc;

import java.util.ArrayList;
import java.util.List;

import util.Pack;

class NBTBaseArray{
	public long intVal;
	public float floatVal;
	public byte [] arByte;
	public short[] arShort;
	public int[] arInt;
	public long [] arLong;
	public String str;
}
class NBTBaseObject{
	public int type;
	public String name;
	public long intVal;
	public float floatVal;
	public byte [] arByte;
	public short[] arShort;
	public int[] arInt;
	public long [] arLong;
	public String str;
}

class NBTCompound{
	public String name;
	public List<NBTBaseObject> list;
}
public class NBT_Old {
	private static final int TAG_End = 0;
	private static final int TAG_Byte = 1;
	private static final int TAG_Short = 2;
	private static final int TAG_Int = 3;
	private static final int TAG_Long = 4;
	private static final int TAG_Float = 5;
	private static final int TAG_Double = 6;
	private static final int TAG_Byte_Array = 7;
	private static final int TAG_String = 8;
	private static final int TAG_List = 9;
	private static final int TAG_Compound = 10;
	private static final int TAG_Int_Array = 11;
	private static final int TAG_Long_Array = 12;
	
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
	
	
	public int damage = 0;
	public int repairCost = 0;
	public long enchantments = 0;
	public byte [] enchantmentsLvl = new byte[32];
	public byte[] data = null;
	public int x = 0, y = 0, z = 0;
	public String id = null;
	
	public NBT_Old dup() {
		NBT_Old res = new NBT_Old();
		res.damage = this.damage;
		res.repairCost = this.repairCost;
		res.enchantments = this.enchantments;
		System.arraycopy(enchantmentsLvl,0,res.enchantmentsLvl,0,this.enchantmentsLvl.length);
		res.data = new byte[this.data.length];
		System.arraycopy(this.data,0, res.data, 0, this.data.length);
		res.x = this.x;
		res.y = this.y;
		res.z = this.z;
		res.id = this.id;
		return res;
	}
	
	
	private void readTag(Pack p,int type,String name) throws Exception {
		int count;

		if (type == TAG_End) return;
		switch (type) {
		case 1:p.readByte(); break;
		case 2:p.readShort(); break;
		case 3:p.readInt(); break;
		case 4:p.readLong(); break;
		case 5:p.readFloat(); break;
		case 6:p.readDouble(); break;
		case 7:
			count = p.readInt();
			while ((count--) != 0) p.readByte();
			break;
		case 8:p.readShortString(); break;
		case 9:
			type = p.readByte();
			count = p.readInt();
			if (count <= 0) break;
			while ((count--) != 0) readTag(p, type, name);
			break;
		case 10:
			while (true) {
				type = p.readUByte();
				if (type == TAG_End) break;
				name = p.readShortString();
				if (name.compareTo("Damage") == 0 && type == 3) damage = p.readInt();
				else if (name.compareTo("RepairCost") == 0 && type == 3) repairCost = p.readInt();
				else if ((name.compareTo("Enchantments") == 0 || name.compareTo("StoredEnchantments") == 0) && type == 9) {
					type = p.readByte();
					count = p.readInt();
					if (count > 0) {
						if (type == 0x0a) while ((count--) != 0) readEnchantments(p);
						else while ((count--) != 0) readTag(p, type, name);
					}
				}
				else if (name == "x" && type == 3) x = p.readInt();
				else if (name == "y" && type == 3) y = p.readInt();
				else if (name == "z" && type == 3) z = p.readInt();
				else if (name == "id" && type == 8) id = p.readShortString();
				else readTag(p, type, name);
			}
			break;
		case 11:
			count = p.readInt();
			while ((count--) != 0) p.readInt();
			break;
		case 12:
			count = p.readInt();
			while ((count--) != 0) p.readLong();
			break;
		}
		
	}
	private void readEnchantments(Pack p) throws Exception {
		String ench;
		String name;
		int enchID = -1;
		int level = -1;
		int type;

		while (true) {
			type = p.readUByte();
			if (type == 0) return;
			name = p.readShortString();
			if (name.compareTo("id") == 0 && type == 8) {
				ench = p.readShortString();
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

				if (level >= 0) {
					enchantments |= 1L << enchID;
					if (enchID < 32) enchantmentsLvl[enchID] = (byte)level;
				}
			}
			else if (name.compareTo("lvl") == 0 && type == 2) {
				level = p.readShort();
				if (enchID >= 0) {
					enchantments |= 1L << enchID;
					if (enchID < 32) enchantmentsLvl[enchID] = (byte)level;
				}
			}
			else readTag(p, type, name);
		}
	}
	
	public NBT_Old() {}
	public NBT_Old(Pack p) throws Exception {
		read(p);
	}
	void read(Pack p) throws Exception{
		int pos;
		int type;
		int count;
		int i;
		String name;

		pos = p.tellg();

		type = p.readUByte();
		if (type != 0) {
			name = p.readShortString();
			readTag(p, type, name);
		}
		int len = p.tellg() - pos;
		data = new byte[len];
		p.seekg(pos);
		p.readArray(data);
	}
	void write(Pack p) {
		if (data != null) p.writeArray(data);
	}
	public boolean isEqual(NBT_Old t) {
		if (this == t) return true;
		if (this.data.length != t.data.length) return false;
		for(int i = 0;i < this.data.length;i++) {
			if(this.data[i] != t.data[i]) return false;
		}
		return true;
	}
	
	
}
