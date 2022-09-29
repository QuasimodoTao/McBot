package util.NBT;

import util.StreamArray;

public class NBTFloat extends NBT{
	public float a;
	  
	public NBTFloat(String par1Str) {
	    super(5, par1Str);
	}
	public void read(StreamArray par1DataInput) throws Exception {
	    this.a = par1DataInput.readFloat();
	}
	public String toString() {
		return "NBT_Float:" + name + ":" + a;
	}
}
