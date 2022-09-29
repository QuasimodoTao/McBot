package event;

import mc.Entity;
import mcbot.Bot;

public interface OnBotEvent {
	void onChatMessage(int pos,String msg);
	void onDisconnect(String reason);
	void onSpawnEntity(Entity entity);
	void onTeleport(double ox,double oy,double oz,double cx,double cy,double cz);
	void onUpdateBlock(int x,int y,int z,int org,int cur);
	void onUpdateHealth(float health,int food,float saturation);
}
