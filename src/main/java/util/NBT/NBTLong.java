package util.NBT;

import util.StreamArray;

public class NBTLong extends NBT{
	public long a;
	public NBTLong(String par1Str) {
	    super(5, par1Str);
	}
	public void read(StreamArray par1DataInput) throws Exception {
	    this.a = par1DataInput.readLong();
	}
	public String toString() {
		return "NBT_Long:" + name + ":" + a;
	}
}
