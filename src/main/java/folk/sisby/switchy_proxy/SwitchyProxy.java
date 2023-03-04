package folk.sisby.switchy_proxy;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.styledchat.StyledChatEvents;
import folk.sisby.switchy.api.SwitchyEvents;
import folk.sisby.switchy.api.SwitchyPlayer;
import folk.sisby.switchy.api.presets.SwitchyPresets;
import folk.sisby.switchy_proxy.modules.ProxyModule;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SwitchyProxy implements SwitchyEvents.Init, StyledChatEvents.PreMessageEvent {
	public static final String ID = "switchy_proxy";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {

	}

	@Override
	public String onPreMessage(String content, PlaceholderContext placeholderContext) {
		ServerPlayerEntity player = placeholderContext.player();
		if (player != null) {
			SwitchyPresets presets = ((SwitchyPlayer) player).switchy$getPresets();
			for (Map.Entry<String, ProxyModule> entry : presets.getAllOfModule(ProxyModule.ID, ProxyModule.class).entrySet()) {
				String name = entry.getKey();
				ProxyModule module = entry.getValue();
				ProxyTag match = module.getTags().stream().filter(tag -> tag.matches(content)).findFirst().orElse(null);
				if (match != null) {
					if (player instanceof SwitchyProxyPlayer sp) {
						sp.switchy_proxy$setMatchedPreset(name);
					}
					return match.strip(content);
				}
			}
		}
		return content;
	}
}
