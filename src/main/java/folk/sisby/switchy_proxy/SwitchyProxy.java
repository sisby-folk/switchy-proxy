package folk.sisby.switchy_proxy;

import eu.pb4.styledchat.StyledChatEvents;
import eu.pb4.styledchat.ducks.ExtSignedMessage;
import folk.sisby.switchy.api.SwitchyEvents;
import folk.sisby.switchy.api.SwitchyPlayer;
import folk.sisby.switchy.api.presets.SwitchyPreset;
import folk.sisby.switchy.api.presets.SwitchyPresets;
import folk.sisby.switchy.modules.DrogtorModule;
import folk.sisby.switchy.modules.StyledNicknamesModule;
import folk.sisby.switchy.util.Feedback;
import folk.sisby.switchy_proxy.modules.ProxyModule;
import folk.sisby.switchy_proxy.modules.ProxyModuleConfig;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.MessageType;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.unmapped.C_zzdolisx;
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
	public static final String ARG_CONTENT = "proxy_content";
	public static final String ARG_DISPLAY_NAME = "proxy_display_name";

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
							SwitchyProxy.LOGGER.info("[Switchy Proxy] Original | <{}> {}", player.getName(), content);
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

	@SuppressWarnings("ConstantValue")
	private static void onMessageArgs(C_zzdolisx message, @Nullable ServerPlayerEntity sender, MessageType.C_iocvgdxe params) {
		if (sender instanceof SwitchyProxyPlayer spp && (Object) message instanceof ExtSignedMessage esm) {
			esm.styledChat_setArg(ARG_DISPLAY_NAME, proxyDisplayName(spp));
			if (spp.switchy_proxy$getProxiedContent() != null) {
				esm.styledChat_setArg(ARG_CONTENT, Feedback.literal(spp.switchy_proxy$getProxiedContent()));
			}
		}
	}

	private static void onMessageClear(C_zzdolisx message, @Nullable ServerPlayerEntity sender, MessageType.C_iocvgdxe params) {
		if (sender instanceof SwitchyProxyPlayer spp) {
			spp.switchy_proxy$setMatchedPreset(null);
			spp.switchy_proxy$setProxiedContent(null);
		}
	}

	@Override
	public void onInitialize() {
		SwitchyEvents.COMMAND_INIT.register(SwitchyProxyCommands::registerCommands);

		StyledChatEvents.PRE_MESSAGE_CONTENT.register((content, placeholderContext) -> proxyContent(content, placeholderContext.player()));

		ServerMessageEvents.CHAT_MESSAGE.register(PHASE_ARGS, SwitchyProxy::onMessageArgs);
		ServerMessageEvents.CHAT_MESSAGE.register(PHASE_CLEAR, SwitchyProxy::onMessageClear);
		ServerMessageEvents.CHAT_MESSAGE.addPhaseOrdering(PHASE_ARGS, PHASE_CLEAR);

		ServerMessageEvents.COMMAND_MESSAGE.register(PHASE_ARGS, (m, s, p) -> onMessageArgs(m, s.method_44023(), p));
		ServerMessageEvents.COMMAND_MESSAGE.register(PHASE_CLEAR, (m, s, p) -> onMessageClear(m, s.method_44023(), p));
		ServerMessageEvents.COMMAND_MESSAGE.addPhaseOrdering(PHASE_ARGS, PHASE_CLEAR);
	}
}
