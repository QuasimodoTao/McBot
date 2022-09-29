package mc;

public class Chunk {
	public int x = 0;
	public int z = 0;
	public volatile int defBlock[];
	public volatile short[][] block;
	public Chunk() {
		defBlock = new int[24];
		block = new short[24][];
		for(int i = 0;i < 24;i++) {
			defBlock[i] = 0;
			block[i] = null;
		}
	}
	public int getBlock(int _x, int _y, int _z) {
		int l = _y >> 4;
		if (l >= 20 || l < -4) return 0;
		l += 4;
		if (block[l] == null) return defBlock[l];
		_y &= 0x0f;
		_x &= 0x0f;
		_z &= 0x0f;
		return block[l][_y * 256 + _z * 16 + _x];
	}
	public void setBlock(int _x, int _y, int _z, int val) {
		int l = _y >> 4;
		if (l >= 20 || l < -4) return;
		l += 4;
		if (block[l] == null) {
			block[l] = new short[16 * 16 * 16];
			for (int i = 0; i < 16 * 16 * 16; i++) block[l][i] = (short)defBlock[l];
		}
		_y &= 0x0f;
		_x &= 0x0f;
		_z &= 0x0f;
		block[l][_y * 256 + _z * 16 + _x] = (short)val;
	}
}
