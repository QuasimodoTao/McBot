package mc;

public class World {
	private volatile Chunk [] chunk;
	private MinecraftWorld wid = MinecraftWorld.mwUndef;
	
	private int getIndex(int x,int z) {return ((x << 5) | (z & 0x1f)) & 0x03ff;}
	public World() {
		chunk = new Chunk[32 * 32];
		for(int i = 0;i < 32 * 32;i++) chunk[i] = null;
	}
	public void respawn(MinecraftWorld wid) {
		if(this.wid == wid) return;
		for(int i = 0;i < 32*32;i++) chunk[i] = null;
		this.wid = wid;
	}
	public int getTotalHigh() {
		if(wid == MinecraftWorld.mwOverworld) return 24;
		else return 16;
	}
	public void load(Chunk c) {
		if(c == null) return;
		if(wid != MinecraftWorld.mwOverworld) {
			for(int i = 19;i >= 0;i--) {
				c.block[i + 4] = c.block[i];
				c.defBlock[i + 4] = c.defBlock[i];
			}
			for(int i = 0;i < 4;i++) c.block[i] = null;
		}
		int index = getIndex(c.x,c.z);
		chunk[index] = c;
	}
	public void unlock(int x,int z) {
		int index = getIndex(x,z);
		if(chunk[index] == null) return;
		if(chunk[index].x == x && chunk[index].z == z) chunk[index] = null;	
	}
	public int getBlock(int x, int y, int z) {
		int cx = x >> 4;
		int cz = z >> 4;
		int index = getIndex(cx, cz);
		if (chunk[index] != null) {
			Chunk c = chunk[index];
			if (c.x == cx && c.z == cz) {
				return c.getBlock(x, y, z);
			}
		}
		return -1;
	}
	public void setBlock(int x, int y, int z, int val) {
		int cx = x >> 4;
		int cz = z >> 4;
		int index = getIndex(cx, cz);
		if (chunk[index] != null) {
			Chunk c = chunk[index];
			if (c.x == cx && c.z == cz) 
				c.setBlock(x, y, z, val);
		}
	}
	public boolean isLoaded(int x, int y, int z) {
		int cx = x >> 4;
		int cz = z >> 4;
		int index = getIndex(cx, cz);
		if (chunk[index] != null) {
			Chunk c = chunk[index];
			if (c.x == cx && c.z == cz) return true;
		}
		return false;
	}
	
	
	
}
