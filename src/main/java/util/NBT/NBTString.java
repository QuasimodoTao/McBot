package util.NBT;

import util.StreamArray;

public class NBTString extends NBT{
	public String a;
	  
	public NBTString(String par1Str) {
	    super(8, par1Str);
	}
	public void read(StreamArray par1DataInput) throws Exception {
	    this.a = par1DataInput.readShortString();
	}
	public String toString() {
			return "NBT_String:" + name + ":" + a;
	}
}
