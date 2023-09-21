package folk.sisby.switchy_proxy;

import eu.pb4.styledchat.StyledChatEvents;
import folk.sisby.switchy.api.SwitchyEvents;
import folk.sisby.switchy.api.SwitchyPlayer;
import folk.sisby.switchy.api.presets.SwitchyPreset;
import folk.sisby.switchy.api.presets.SwitchyPresets;
import folk.sisby.switchy.modules.DrogtorModule;
import folk.sisby.switchy.modules.StyledNicknamesModule;
import folk.sisby.switchy_proxy.modules.ProxyModule;
import folk.sisby.switchy_proxy.modules.ProxyModuleConfig;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SwitchyProxy implements SwitchyEvents.Init {
	public static final String ID = "switchy_proxy";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);
	public static final Identifier PHASE_ARGS = new Identifier(ID, "set_args");
	public static final Identifier PHASE_CLEAR = new Identifier(ID, "clear");

	public static @Nullable Text decorateDisplayName(MutableText text, SwitchyProxyPlayer spp) {
		if (text != null && spp instanceof ServerPlayerEntity spe) {
			return spe.addTellClickEvent(Team.decorateName(spe.getScoreboardTeam(), text));
		}
		return null;
	}

	public static @Nullable Text proxyDisplayName(SwitchyProxyPlayer spp) {
		SwitchyPreset preset = spp.switchy_proxy$getMatchedPreset();
		if (preset != null) {
			if (preset.containsModule(StyledNicknamesModule.ID)) {
				Text nickname = preset.getModule(StyledNicknamesModule.ID, StyledNicknamesModule.class).getOutput();
				if (nickname != null) {
					return decorateDisplayName((MutableText) nickname, spp);
				}
			}
			if (preset.containsModule(DrogtorModule.ID)) {
				Text nickname = preset.getModule(DrogtorModule.ID, DrogtorModule.class).getText();
				if (nickname != null) {
					return decorateDisplayName((MutableText) nickname, spp);
				}
			}
		}
		return null;
	}

	public static @Nullable String proxyContent(String content, ServerPlayerEntity player) {
		onMessageClear(null, player, false);
		if (player instanceof SwitchyPlayer sp && player instanceof SwitchyProxyPlayer spp) {
			SwitchyPresets presets = sp.switchy$getPresets();
			if (presets.isModuleEnabled(ProxyModule.ID)) {
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
				// Fix styled chat breaking drogtor coloured nicknames by forcing the mixin route
				if (presets.containsModule(DrogtorModule.ID) && presets.isModuleEnabled(DrogtorModule.ID)) {
					presets.getCurrentPreset().getModule(DrogtorModule.ID, DrogtorModule.class).updateFromPlayer(player, null);
					spp.switchy_proxy$setMatchedPreset(presets.getCurrentPreset());
				}
			}
		}
		return content;
	}

	private static Text onMessageClear(Text message, @Nullable ServerPlayerEntity sender, boolean filtered) {
		if (sender instanceof SwitchyProxyPlayer spp) {
			spp.switchy_proxy$setMatchedPreset(null);
			spp.switchy_proxy$setProxiedContent(null);
		}
		return message;
	}

	@Override
	public void onInitialize() {
		SwitchyEvents.COMMAND_INIT.register(SwitchyProxyCommands::registerCommands);

		StyledChatEvents.PRE_MESSAGE_CONTENT_SEND.register((content, player, filtered) -> proxyContent(content, player));

		// StyledChatEvents.MESSAGE_CONTENT_SEND.register(SwitchyProxy::onMessageClear);
	}
}
