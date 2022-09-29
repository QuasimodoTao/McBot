package util.NBT;

import util.StreamArray;

public class NBTByteArray extends NBT{
	public byte[] a;
	  
	public NBTByteArray(String par1Str) {
		super(7, par1Str);
	}
	public void read(StreamArray par1DataInput) throws Exception {
		int var3 = par1DataInput.readInt();
		this.a = new byte[var3];
		par1DataInput.readFully(this.a);
	}
	public String toString() {
		return "NBT_ByteArray:" + name + ":" + a;
	}
}
