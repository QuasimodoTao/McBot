package util.NBT;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import util.StreamArray;

public class NBTList extends NBT{
	private List<NBT> a = new ArrayList();
	  
	private byte c;

	public NBTList(String par1Str) {
	    super(9, par1Str);
	}
	public String toString() {
		return "NBT_List:" + "name" + ":" + a;
	}
	public void read(StreamArray par1DataInput) throws Exception {
	    this.c = (byte) par1DataInput.readByte();
	    int var3 = par1DataInput.readInt();
	    this.a = new ArrayList<NBT>();
	    for (int var4 = 0; var4 < var3; var4++) {
	    	NBT var5 = NBT.generalNBT(this.c, (String)null);
	    	var5.read(par1DataInput);
	    	this.a.add(var5);
	    } 
	}
	public void put(NBT par1NBTBase) {
	    this.c = par1NBTBase.getType();
	    this.a.add(par1NBTBase);
	}
	public NBT get(int par1) {
	    return this.a.get(par1);
	}
	public int size() {
	    return this.a.size();
	}
	public int getElementType() {
		return c;
	}
}
