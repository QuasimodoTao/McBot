package util.NBT;

import util.StreamArray;

public class NBTIntArray extends NBT{
	public int[] a;
	  
	public NBTIntArray(String par1Str) {
	    super(11, par1Str);
	}
	public void read(StreamArray par1DataInput) throws Exception {
	    int var3 = par1DataInput.readInt();
	    this.a = new int[var3];
	    for (int var4 = 0; var4 < var3; var4++)
	    	this.a[var4] = par1DataInput.readInt(); 
	}
	public String toString() {
		return "NBT_IntArray:" + name + ":" + a;
	}
}
