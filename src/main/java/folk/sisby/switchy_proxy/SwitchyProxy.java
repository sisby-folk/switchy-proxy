package folk.sisby.switchy_proxy;

import eu.pb4.styledchat.StyledChatEvents;
import folk.sisby.switchy.api.SwitchyEvents;
import folk.sisby.switchy.api.SwitchyPlayer;
import folk.sisby.switchy.api.presets.SwitchyPresets;
import folk.sisby.switchy_proxy.modules.ProxyModule;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.quiltmc.loader.api.QuiltLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SwitchyProxy implements SwitchyEvents.Init {
	public static final String ID = "switchy_proxy";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static boolean StyledCompat = false;
	public static boolean DrogtorCompat = false;

	@Override
	public void onInitialize() {
		if (QuiltLoader.isModLoaded("drogtor") && !QuiltLoader.isModLoaded("drogstyle") ) DrogtorCompat = true;
		if (QuiltLoader.isModLoaded("styled-nicknames")) StyledCompat = true;

		StyledChatEvents.PRE_MESSAGE_CONTENT.register((content, placeholderContext) -> {
			ServerPlayerEntity player = placeholderContext.player();
			if (player instanceof SwitchyPlayer sp) {
				SwitchyPresets presets = sp.switchy$getPresets();
				for (Map.Entry<String, ProxyModule> entry : presets.getAllOfModule(ProxyModule.ID, ProxyModule.class).entrySet()) {
					String name = entry.getKey();
					ProxyModule module = entry.getValue();
					ProxyTag match = module.getTags().stream().filter(tag -> tag.matches(content)).findFirst().orElse(null);
					if (match != null) {
						if (player instanceof SwitchyProxyPlayer spp) {
							spp.switchy_proxy$setMatchedPreset(name);
						}
						SwitchyProxy.LOGGER.info("[Switchy Proxy] Original | <{}> {}", player.getGameProfile().getName(), content);
						return match.strip(content);
					} else {
						if (player instanceof SwitchyProxyPlayer spp) {
							spp.switchy_proxy$setMatchedPreset("");
						}
					}
				}
			}
			return content;
		});

		ServerMessageEvents.CHAT_MESSAGE.register(((message, sender, params) -> {
			if (sender instanceof SwitchyProxyPlayer spp) {
				spp.switchy_proxy$setMatchedPreset(null);
			}
		}));
	}
}
