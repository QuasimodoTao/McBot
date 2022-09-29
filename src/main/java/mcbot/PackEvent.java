package mcbot;

import util.Pack;

public interface PackEvent {
	void event(Bot bot,Pack p) throws Exception;
}
