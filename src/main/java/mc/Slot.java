package mc;

import util.Pack;
import util.NBT.*;

public class Slot {
	public boolean present = false;
	public int count = 0;
	public int id = 0;
	public NBT nbt = null;
	public static final Slot Empty = new Slot();
	public Enchantment ench = null;
	public int damage = 0;
	
	public Slot() {}
	public Slot(Pack p) throws Exception {
		read(p);
	}
	public void read(Pack p) throws Exception {
		present = p.readBoolean();
		if (!present) return;
		id = p.readVarInt();
		count = p.readByte();
		nbt = NBT.parseNBT(p);
		if(nbt instanceof NBTComp) {
			ench = new Enchantment((NBTComp)nbt);
			if(((NBTComp)nbt).exist("Damage")) damage = ((NBTComp)nbt).getInt("Damage");
		}
	}
	public void write(Pack p) {
		p.writeBoolean(present);
		if (!present) return;
		p.writeVarInt(id);
		p.writeByte(count);
		if(nbt == null) p.writeByte(0);
		else nbt.write(p);
	}
	public Slot dup(){
		Slot res = new Slot();
		res.count = this.count;
		res.id = this.id;
		res.present = this.present;
		res.nbt = this.nbt;
		res.ench = this.ench;
		res.damage = this.damage;
		return res;
	}
}
