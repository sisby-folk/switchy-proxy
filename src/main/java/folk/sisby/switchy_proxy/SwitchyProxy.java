package folk.sisby.switchy_proxy;

import folk.sisby.switchy.api.SwitchyEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchyProxy implements SwitchyEvents.Init {
	public static final String ID = "switchy_proxy";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {

	}
}
