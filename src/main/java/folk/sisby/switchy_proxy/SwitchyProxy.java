package folk.sisby.switchy_proxy;

import eu.pb4.styledchat.StyledChatEvents;
import folk.sisby.switchy.api.SwitchyEvents;
import folk.sisby.switchy.api.SwitchyPlayer;
import folk.sisby.switchy.api.presets.SwitchyPreset;
import folk.sisby.switchy.api.presets.SwitchyPresets;
import folk.sisby.switchy.modules.DrogtorModule;
import folk.sisby.switchy_proxy.modules.ProxyModule;
import folk.sisby.switchy_proxy.modules.ProxyModuleConfig;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SwitchyProxy implements SwitchyEvents.Init {
	public static final String ID = "switchy_proxy";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
		StyledChatEvents.PRE_MESSAGE_CONTENT.register((content, placeholderContext) -> {
			ServerPlayerEntity player = placeholderContext.player();
			if (player instanceof SwitchyPlayer sp && player instanceof SwitchyProxyPlayer spp) {
				SwitchyPresets presets = sp.switchy$getPresets();
				for (Map.Entry<String, ProxyModule> entry : presets.getAllOfModule(ProxyModule.ID, ProxyModule.class).entrySet()) {
					String name = entry.getKey();
					ProxyModule module = entry.getValue();
					ProxyTag match = module.getTags().stream().filter(tag -> tag.matches(content)).findFirst().orElse(null);
					if (match != null) {
						SwitchyPreset preset = presets.getPreset(name);
						if (spp.switchy_proxy$getMatchedPreset() == null) {
							SwitchyProxy.LOGGER.info("[Switchy Proxy] Original | <{}> {}", player.getGameProfile().getName(), content);
						}
						spp.switchy_proxy$setMatchedPreset(preset);
						return match.strip(content);
					}
				}
				if (presets.getModuleConfig(ProxyModule.ID, ProxyModuleConfig.class).isLatchEnabled() && spp.switchy_proxy$getLatchedPreset() != null && presets.getPresets().containsValue(spp.switchy_proxy$getLatchedPreset())) {
					spp.switchy_proxy$setMatchedPreset(spp.switchy_proxy$getLatchedPreset());
					return content;
				}
				// Fix styled chat breaking drogtor coloured nicknames by forcing the mixin route
				if (presets.containsModule(DrogtorModule.ID) && presets.isModuleEnabled(DrogtorModule.ID)) {
					presets.getCurrentPreset().getModule(DrogtorModule.ID, DrogtorModule.class).updateFromPlayer(player, null);
					spp.switchy_proxy$setMatchedPreset(presets.getCurrentPreset());
				}
			}
			return content;
		});

		ServerMessageEvents.CHAT_MESSAGE.register(((message, sender, params) -> {
			if (sender instanceof SwitchyProxyPlayer spp) {
				spp.switchy_proxy$setMatchedPreset(null);
			}
		}));

		ServerMessageEvents.COMMAND_MESSAGE.register(((message, source, params) -> {
			if (source.method_44023() instanceof SwitchyProxyPlayer spp) {
				spp.switchy_proxy$setMatchedPreset(null);
			}
		}));
	}
}
