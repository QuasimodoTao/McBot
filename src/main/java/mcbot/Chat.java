package mcbot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import util.Event;

public class Chat {
	
	private AtomicBoolean chatBusy = new AtomicBoolean(false);
	private Bot bot = null;
	//public Event loginEvent = new Event(false);
	public Event chestShopEvent = new Event();

	
	public Chat(Bot bot) {
		this.bot = bot;
	}

	public String getStringFromJson(JSONObject js) {
		String showStr = "";
		if(js.containsKey("translate")) {
			String chatType = js.getString("translate");
			if(chatType.compareTo("chat.type.text") == 0) {
				JSONArray with = js.getJSONArray("with");
				if(with == null) {
					System.out.println(chatType);
					return "";
				}
				if(with.size() <= 1) {
					System.out.println(chatType);
					return "";
				}
				showStr += "<";
				showStr += with.getJSONObject(0).getString("text");
				showStr += "> ";
				for(int i = 1;i < with.size();i++) {
					showStr += with.getJSONObject(i).getString("text");
				}
			}
			else {
				if(chatType.compareTo("sleep.players_sleeping") == 0) return "";
				if(chatType.compareTo("chat.type.advancement.task") == 0) return "";
				if(chatType.contains("death")) return "";
				if(chatType.charAt(0) == '%') return "";
				System.out.println(chatType);
				return "";
			}
		}
		if(js.containsKey("extra")) {
			JSONArray extra = js.getJSONArray("extra");
			for(int i = 0;i < extra.size();i++) {
				String text = extra.getJSONObject(i).getString("text");
				StringBuffer bf = new StringBuffer();
				for(int j = 0;j < text.length();j++) {
					if(text.charAt(j) == '\\') {
						j++;
						if(j >= text.length()) break;
						char a = text.charAt(j);
						switch(a) {
						case 'n':
							bf.append('\n');
							break;
						case '\\':
							bf.append('\\');
							break;
						case 'u':
							String nu = text.substring(j+1,j+4);
							if(nu == null) break;
							int v = Integer.parseInt(nu);
							j += 4;
							a = (char)v;
							bf.append(a);
							break;
						case '\"':
							bf.append('\"');
							break;
						case '\'':
							bf.append('\'');
							break;
						case 'b':
							bf.append('\b');
							break;
						case 'f':
							bf.append('\f');
							break;
						case 't':
							bf.append('\t');
							break;
						default:
							bf.append(a);
							break;
						}
					}
					else {
						bf.append(text.charAt(j));
					}
				}
				showStr += bf;
			}
			
		}
		if(js.containsKey("text")) showStr += js.getString("text");
		return showStr;
	}
	public void onChatMessage(String json,int pos) {
		JSONObject js = JSONObject.parseObject(json);
		String showStr = getStringFromJson(js);
		if(showStr == null || showStr.length() == 0) return;
		if(showStr.charAt(0) == '%') return;
		bot.showMessage(showStr);
		
		String clan = null;
		String user = null;
		String cont = null;
		
		if(showStr.charAt(0) == '[') {
			int indexs = showStr.indexOf(']');
			clan = showStr.substring(1,indexs);
			if(indexs + 1 < showStr.length()) {
				if(showStr.charAt(indexs + 1) == '<') {
					int indext = showStr.indexOf('>');
					user = showStr.substring(indexs + 2,indext);
					cont = showStr.substring(indext + 1);
				}
				else cont = showStr.substring(indexs + 1);
			}
			if(clan.contains("->")) {
				//msg
				int ind = 0;
				while(true) {
					char ch = cont.charAt(ind);
					if(ch != '\n' && ch != ' ' && ch != '\t') break;
					ind++;
				}
				if(ind != 0) cont = cont.substring(ind);
				user = clan.split(" ")[0];
				if(bot.admin == null) {
					if(bot.user.maskUser == true) {
						try {
							if(allocChat() == true) bot.net.packOutChat("/ignore " + user);
						} catch (Exception e) {
							return;
						}
					}
				}
				else {
					int i;
					for(i = 0;i < bot.admin.length;i++) {
						if(bot.admin[i].compareTo(user) == 0) break;
					}
					if(i >= bot.admin.length) {
						if(bot.user.maskUser == true) {
							try {
								if(allocChat() == true) bot.net.packOutChat("/ignore " + user);
							} catch (Exception e) {
								return;
							}
						}
					}
					else {
						String c = user;
						String d = cont;
						new Thread(()->{
							if(bot.mainThread instanceof ComplexBot) {
								try {
									((ComplexBot) bot.mainThread).onlineCommand(c,d);
								} catch (Exception e) {}
							} else bot.onlineCommand(c,d);
						}).start();
					}
				}
			}
		}
		else if(showStr.charAt(0) == '<') {
			int indext = showStr.indexOf('>');
			user = showStr.substring(1,indext);
			cont = showStr.substring(indext + 1);
		}
		if(user == null) {
			//if(showStr.contains("/login <密码>")) {
			//	if(bot.user.inputPassword == true) {
			//		new Thread(()->{
			//			try {
			//			Thread.sleep(500);
			//			bot.net.packOutChat("/login " + bot.user.password);
			//		} catch (Exception e) {
			//			return;
			//		}
			//		}).start();
			//	} 
			//}
			//else if(showStr.contains("已成功登录！")) {
			//	loginEvent.setEvent();
			//}
			//else if(showStr.contains("已成功登录！若要退出登录请输入“/logout")) {
			//	loginEvent.setEvent();
			//}
			//else 
			if(showStr.contains("+---------------------------------------------------+")) {
				chestShopEvent.setEvent();
			}
		}
		else {
			if(bot.admin == null) {
				try {
					if(allocChat() == true) bot.net.packOutChat("/ignore " + user);
				} catch (Exception e) {
					return;
				}
			}
			else {
				int i;
				for(i = 0;i < bot.admin.length;i++) {
					if(bot.admin[i].compareTo(user) == 0) break;
				}
				if(i >= bot.admin.length) {
					if(bot.user.maskUser == true) {
						try {
							if(allocChat() == true) bot.net.packOutChat("/ignore " + user);
						} catch (Exception e) {
							return;
						}
					}
				}
			}
		}
	}

	public void lockChat() throws InterruptedException {
		while(chatBusy.compareAndExchange(false, true) == false) Thread.sleep(10);
	}
	public boolean allocChat() {
		if(chatBusy.compareAndExchange(false, true) == false) return false;
		new Thread(()->{
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			chatBusy.set(false);
		}).start();
		return true;
	}
	public void unlockChat() {
		new Thread(()->{
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			chatBusy.set(false);
		}).start();
	}
	public boolean buyItemFromChestShop(int x, int y, int z, int send) throws Exception,InterruptedException {
		return buyItemFromChestShop(x,y,z,send,10000,10000,true);
	}
	public boolean buyItemFromChestShop(int x, int y, int z, int send,int outOfTimeShop, 
			int outOfTimeBuy,boolean retry) throws Exception,InterruptedException {
		String _send = new String("");
		
		if (send >= 0) _send += send;
		else _send += "all";

		while (true) {
			lockChat();
			while (true) {
				chestShopEvent.clearEvent();;
				bot.click(x, y, z);
				if (outOfTimeShop <= 0) chestShopEvent.waitEvent();
				else if(chestShopEvent.waitEvent(outOfTimeShop) == true) break;
				if (!retry) {
					unlockChat();
					return false;
				}
			}
			Thread.sleep(500);
			chestShopEvent.clearEvent();;
			sendChat(_send);
			unlockChat();
			if (outOfTimeBuy <= 0) chestShopEvent.waitEvent();
			else if(chestShopEvent.waitEvent(outOfTimeBuy) == true) return true;
			if (!retry) return false;
		}
	}
	public boolean delayableCommand(String cmd,int outOfTime) throws Exception,InterruptedException {
		bot.posUpdate.clearEvent();;
		if(outOfTime <= 0) {
			sendChat(cmd);
			bot.posUpdate.waitEvent();
			return true;
		}
		else {
			while (true) {
				sendChat(cmd);
				if(true == bot.posUpdate.waitEvent(6000)) return true;
				outOfTime -= 6000;
				if(outOfTime <= 0) return false;
			}
		}
	}
	public void sendChat(String msg) throws Exception {
		bot.net.packOutChat(msg);
	}
	public void tpCommand(String point,int outOfTime) throws Exception {
		String cmd = "/res tp ";
		cmd += point;
		delayableCommand(cmd,outOfTime);
	}
	public void homeCommand(int outOfTime) throws Exception {
		delayableCommand("/home",outOfTime);
	}
	public void warpCommand(String point,int outOfTime) throws Exception {
		String cmd = "/warp ";
		cmd += point;
		delayableCommand(cmd,outOfTime);
	}
	public static List<String> parseCommandLine(String cmd) {
		List<String> cmds = new ArrayList<String>();
		int first,last;
		first = 0;
		int len = cmd.length();
		while(true) {
			while(first < len) {
				char ch = cmd.charAt(first);
				if(ch != ' ' && ch != '\n' && ch != '\t') break;
				first++;
			}
			if(first >= len) break;
			last = first;
			while(last < len) {
				char ch = cmd.charAt(last);
				if(ch == ' ' || ch == '\n' || ch == '\t') break;
				last++;
			}
			if(last >= len) {
				cmds.add(cmd.substring(first));
				break;
			}
			cmds.add(cmd.substring(first,last));
			first = last;
		}
		return cmds;
	}
	
}
