package mcbot;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import util.StreamArray;
import util.NBT.*;

public class de {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			FileInputStream in = new FileInputStream("nbt.bin");
			byte[] data = in.readAllBytes();
			in.close();
			StreamArray b = new StreamArray(data);
			NBT c = NBT.parseNBT(b);
			if(c instanceof NBTComp) {
				NBTComp nc = (NBTComp)c;
				FileOutputStream out = new FileOutputStream("nbt.txt");
				out.write(nc.toString().getBytes());
				out.close();
			}
			else if(c instanceof NBTEnd) {
				
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
