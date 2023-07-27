package folk.sisby.switchy_proxy;

import eu.pb4.styledchat.StyledChatEvents;
import eu.pb4.styledchat.ducks.ExtSignedMessage;
import folk.sisby.switchy.api.SwitchyEvents;
import folk.sisby.switchy.api.SwitchyPlayer;
import folk.sisby.switchy.api.presets.SwitchyPreset;
import folk.sisby.switchy.api.presets.SwitchyPresets;
import folk.sisby.switchy.modules.StyledNicknamesModule;
import folk.sisby.switchy_proxy.modules.ProxyModule;
import folk.sisby.switchy_proxy.modules.ProxyModuleConfig;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

public class SwitchyProxy implements SwitchyEvents.Init {
	public static final String ID = "switchy_proxy";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);
	public static final Identifier PHASE_ARGS = new Identifier(ID, "set_args");
	public static final Identifier PHASE_CLEAR = new Identifier(ID, "clear");
	public static final String ARG_CONTENT = "proxy_content";
	public static final String ARG_DISPLAY_NAME = "proxy_display_name";



	public static @Nullable Text proxyDisplayName(SwitchyProxyProfile spp, boolean clear) {
		SwitchyPreset preset = spp.switchy_proxy$getMatchedPreset();
		if (preset != null) {
			if (clear) spp.switchy_proxy$setMatchedPreset(null);
			if (preset.containsModule(StyledNicknamesModule.ID)) {
				return preset.getModule(StyledNicknamesModule.ID, StyledNicknamesModule.class).getText();
			}
		}
		return null;
	}

	public static @Nullable String proxyContent(String content, ServerPlayerEntity player) {
		if (player instanceof SwitchyPlayer sp && player.getGameProfile() instanceof SwitchyProxyProfile spp) {
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
					String proxiedContent = match.strip(content);
					spp.switchy_proxy$setProxiedContent(proxiedContent);
					return proxiedContent;
				}
			}
			if (presets.getModuleConfig(ProxyModule.ID, ProxyModuleConfig.class).isLatchEnabled() && spp.switchy_proxy$getLatchedPreset() != null && presets.getPresets().containsValue(spp.switchy_proxy$getLatchedPreset())) {
				spp.switchy_proxy$setMatchedPreset(spp.switchy_proxy$getLatchedPreset());
				return content;
			}
		}
		return content;
	}

	@Override
	public void onInitialize() {
		StyledChatEvents.PRE_MESSAGE_CONTENT.register((content, placeholderContext) -> proxyContent(content, placeholderContext.player()));

		ServerMessageEvents.CHAT_MESSAGE.register(PHASE_ARGS, (message, sender, params) -> {
			if (sender.getGameProfile() instanceof SwitchyProxyProfile spp) {
				((ExtSignedMessage) (Object) message).styledChat_setArg(ARG_DISPLAY_NAME, Objects.requireNonNullElse(proxyDisplayName(spp, false), Text.empty()));
				if (spp.switchy_proxy$getProxiedContent() != null) {
					((ExtSignedMessage) (Object) message).styledChat_setArg(ARG_CONTENT, Text.literal(spp.switchy_proxy$getProxiedContent()));
				}
			}
		});

		ServerMessageEvents.CHAT_MESSAGE.register(PHASE_CLEAR, (message, sender, params) -> {
			if (sender.getGameProfile() instanceof SwitchyProxyProfile spp) {
				spp.switchy_proxy$setMatchedPreset(null);
				spp.switchy_proxy$setProxiedContent(null);
			}
		});

		ServerMessageEvents.CHAT_MESSAGE.addPhaseOrdering(PHASE_ARGS, PHASE_CLEAR);

		ServerMessageEvents.COMMAND_MESSAGE.register(((message, source, params) -> {
			ServerPlayerEntity player = source.getPlayer();
			if (player != null && player.getGameProfile() instanceof SwitchyProxyProfile spp) {
				spp.switchy_proxy$setMatchedPreset(null);
				spp.switchy_proxy$setProxiedContent(null);
			}
		}));
	}
}
