package util.NBT;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import util.StreamArray;

public class NBTComp extends NBT{
	private Map<String, NBT> a = new HashMap<String, NBT>();
	public NBTComp() {
	    super(10, "");
	}
	public NBTComp(String par1Str) {
	    super(10, par1Str);
	}
	public void read(StreamArray par1DataInput) throws Exception {
	    this.a.clear();
	    NBT var3;
	    while(true) {
	    	var3 = NBT.b(par1DataInput);
	    	if(var3.getType() == 0) break;
	    	this.a.put(var3.getName(), var3); 
	    }	
	}
	public String toString() {
		String res0 = "NBT_Comp:" + name + ":{";
		Iterator<NBT> nc = a.values().iterator();
		if(nc.hasNext()) {
			NBT v = nc.next();
	    	res0 += v;
			
		}
		while (nc.hasNext()) {
	    	NBT v = nc.next();
	    	res0 += "," + v;
	    } 
		return res0 + "}";
	}
	public Collection<NBT> valus() {
		return this.a.values();
	}
	public void put(String par1Str, NBT par2NBTBase) {
		this.a.put(par1Str, par2NBTBase.setName(par1Str));
	}
	public NBT a(String par1Str) {
	    return (NBT)this.a.get(par1Str);
	}
	public boolean exist(String par1Str) {
	    return this.a.containsKey(par1Str);
	}
	public byte getByte(String par1Str) {
	    try {
	    	return !this.a.containsKey(par1Str) ? 0 : ((NBTByte)this.a.get(par1Str)).val;
	    } catch (ClassCastException var3) {
	    	return 0;
	    } 
	}
	public short getShort(String par1Str) {
	    try {
	    	return !this.a.containsKey(par1Str) ? 0 : ((NBTShort)this.a.get(par1Str)).val;
	    } catch (ClassCastException var3) {
	    	return  0;
	    } 
	}
	public int getInt(String par1Str) {
	    try {
	    	return !this.a.containsKey(par1Str) ? 0 : ((NBTInt)this.a.get(par1Str)).val;
	    } catch (ClassCastException var3) {
	    	return 0;
	    } 
	}
	public int getIntegerWithDefault(String key, int default_value) {
		return exist(key) ? getInt(key) : default_value;
	}
	  
	public long getLong(String par1Str) {
	    try {
	    	return !this.a.containsKey(par1Str) ? 0L : ((NBTLong)this.a.get(par1Str)).a;
	    } catch (ClassCastException var3) {
	    	return 0;
	    } 
	}
	public float getFloat(String par1Str) {
	    try {
	    	return !this.a.containsKey(par1Str) ? 0.0F : ((NBTFloat)this.a.get(par1Str)).a;
	    } catch (ClassCastException var3) {
	    	return 0.0F;
	    } 
	}
	public double getDouble(String par1Str) {
	    try {
	    	return !this.a.containsKey(par1Str) ? 0.0D : ((NBTDouble)this.a.get(par1Str)).a;
	    } catch (ClassCastException var3) {
	    	return 0.0D;
	    } 
	}
	public String getString(String par1Str) {
	    try {
	    	return !this.a.containsKey(par1Str) ? "" : ((NBTString)this.a.get(par1Str)).a;
	    } catch (ClassCastException var3) {
	    	return"";
	    } 
	}
	public byte[] getByteArray(String par1Str) {
	    try {
	    	return !this.a.containsKey(par1Str) ? new byte[0] : ((NBTByteArray)this.a.get(par1Str)).a;
	    } catch (ClassCastException var3) {
	    	return new byte[0];
	    } 
	}
	public int[] getIntArray(String par1Str) {
	    try {
	    	return !this.a.containsKey(par1Str) ? new int[0] : ((NBTIntArray)this.a.get(par1Str)).a;
	    } catch (ClassCastException var3) {
	    	return new int[0];
	    } 
	}
	public NBTComp getComp(String par1Str) {
	    try {
	    	return !this.a.containsKey(par1Str) ? new NBTComp(par1Str) : (NBTComp)this.a.get(par1Str);
	    } catch (ClassCastException var3) {
	    	return new NBTComp(par1Str);
	    } 
	}
	public NBTList getList(String par1Str) {
	    try {
	    	return !this.a.containsKey(par1Str) ? new NBTList(par1Str) : (NBTList)this.a.get(par1Str);
	    } catch (ClassCastException var3) {
	    	return new NBTList(par1Str);
	    } 
	}
	public boolean getBoolean(String par1Str) {
	    return (getByte(par1Str) != 0);
	}
	public boolean isEmpty() {
	    return this.a.isEmpty();
	}
}
