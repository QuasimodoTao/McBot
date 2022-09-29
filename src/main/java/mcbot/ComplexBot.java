package mcbot;

import java.util.List;

import mc.MinecraftItem;
import mc.Slot;

public class ComplexBot extends Thread{
	private Bot bot;
	
	public ComplexBot(Bot bot) {
		this.bot = bot;
	}
	protected ComplexBot() {}
	protected final void setBot(Bot bot) {
		this.bot = bot;
	}
	public final void onlineCommand(String user, String cmd) throws InterruptedException, Exception {
		if(user == null || user.length() == 0 || cmd == null || cmd.length() == 0) return;
		if(cmd.length() > 0 && cmd.charAt(0) == '/') {
			bot.chat.sendChat(cmd);
			return;
		}
		List<String> cmds = Chat.parseCommandLine(cmd);
		if(cmds.size() == 0) return;
		String cm = cmds.get(0);
		cm.toLowerCase();
		if(cm.compareTo("tp") == 0) {
			if(cmds.size() <= 1) {
				bot.chat.sendChat("/msg " + user + " bad argument, should use as tp res.");
				return;
			}
			tpCommand();
			Thread.sleep(500);
			bot.chat.sendChat("/res tp " + cmds.get(1));
			sleep(200);
			bot.chat.sendChat("/msg " + user + " try tp " + cmds.get(1));
		}
		else if(cm.compareTo("tpa") == 0) {
			tpCommand();
			Thread.sleep(500);
			String dire;
			if(cmds.size() <= 1) dire = user;
			else dire = cmds.get(1);
			bot.chat.sendChat("/tpa " + dire);
			sleep(200);
			bot.chat.sendChat("/msg " + user + " try tp " + dire);
		}
		else if(cm.compareTo("drop") == 0) {
			if(cmds.size() <= 1) {
				bot.chat.sendChat("/msg " + user + " bad argument, should use as drop slot or drop all.");
				return;
			}
			String arg = cmds.get(1);
			arg.toLowerCase();
			if(arg.compareTo("all") == 0) {
				dropItemCommand();
				Thread.sleep(500);
				for(int i = 0;i < 36;i++) {
					if(bot.item.inventorys[i].present) {
						bot.item.dropInventoryItemStack(i);
						sleep(250);
					}
				}
				bot.item.dropAlterHandStack();
				bot.chat.sendChat("/msg " + user + " try drop item stack in all slots in inventory and alter hand.");
			}
			else {
				int s;
				try {
					s = Integer.parseInt(cmds.get(1));
				} catch(NumberFormatException e) {
					bot.chat.sendChat("/msg " + user + " bad argument, should use as show [slot], shot must be a number.");
					return;
				}
				if(s > 36) 
					bot.chat.sendChat("/msg " + user + " bad argument, should use as show [slot], shot must less than 37.");
				else {
					dropItemCommand();
					Thread.sleep(500);
					if(s == 36){
						bot.item.dropAlterHandStack();
						bot.chat.sendChat("/msg " + user + " try drop item stack in alter hand.");
					}
					else {
						bot.item.dropInventoryItemStack(s);
						bot.chat.sendChat("/msg " + user + " try drop item stack in slot " + s + ".");
					}
				}
			}
		}
		else if(cm.compareTo("show") == 0){
			if(cmds.size() <= 1) {
				bot.chat.sendChat("/msg " + user + " bad argument, should use as show [slot].");
				return;
			}
			int s;
			try {
				s = Integer.parseInt(cmds.get(1));
			} catch(NumberFormatException e) {
				bot.chat.sendChat("/msg " + user + " bad argument, should use as show [slot], slot must be a number.");
				return;
			}
			if(s > 36) {
				bot.chat.sendChat("/msg " + user + " bad argument, should use as show [slot], slot must less than 37.");
				return;
			}
			String send = "/msg " + user + " Slot " + s;
			Slot st = s < 36 ? bot.item.inventorys[s] : bot.item.alterHand[0];
			if(st.present == false) send += " is empty.";
			else send += " " + MinecraftItem.item[st.id].name.substring(10) + ",count " + st.count + ".";
			bot.chat.sendChat(send);
		}
		else onlineCommand(user,cmds,cmd);
	}
	public final void unknowCommand(String user, List<String> cmd,String org) throws Exception {
		bot.chat.sendChat(org);
	}
	public void onlineCommand(String user, List<String> cmd, String org) throws InterruptedException, Exception {
		unknowCommand(user,cmd,org);
	}
	public void tpCommand() {}
	public void dropItemCommand() {}
}
