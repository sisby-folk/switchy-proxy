package folk.sisby.switchy_proxy;

import eu.pb4.styledchat.StyledChatEvents;
import folk.sisby.switchy.api.SwitchyEvents;
import folk.sisby.switchy.api.SwitchyPlayer;
import folk.sisby.switchy.api.presets.SwitchyPreset;
import folk.sisby.switchy.api.presets.SwitchyPresets;
import folk.sisby.switchy_proxy.compat.DrogtorCompat;
import folk.sisby.switchy_proxy.compat.StyledNicknamesCompat;
import folk.sisby.switchy_proxy.modules.ProxyModule;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SwitchyProxy implements SwitchyEvents.Init {
	public static final String ID = "switchy_proxy";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	private static final Identifier DROGTOR_ID = new Identifier("switchy", "drogtor");
	private static final Identifier STYLED_ID = new Identifier("switchy", "styled_nicknames");

	@Override
	public void onInitialize() {
		StyledChatEvents.PRE_MESSAGE_CONTENT.register((content, placeholderContext) -> {
			ServerPlayerEntity player = placeholderContext.player();
			if (player instanceof SwitchyPlayer sp) {
				SwitchyPresets presets = sp.switchy$getPresets();
				for (Map.Entry<String, ProxyModule> entry : presets.getAllOfModule(ProxyModule.ID, ProxyModule.class).entrySet()) {
					String name = entry.getKey();
					ProxyModule module = entry.getValue();
					ProxyTag match = module.getTags().stream().filter(tag -> tag.matches(content)).findFirst().orElse(null);
					if (match != null) {
						SwitchyPreset preset = presets.getPreset(name);
						if (presets.containsModule(DROGTOR_ID) && presets.isModuleEnabled(DROGTOR_ID)) {
							DrogtorCompat.update(player, presets.getCurrentPreset());
							DrogtorCompat.apply(player, preset);
						}
						if (presets.containsModule(STYLED_ID) && presets.isModuleEnabled(STYLED_ID)) {
							StyledNicknamesCompat.update(player, presets.getCurrentPreset());
							StyledNicknamesCompat.apply(player, preset);
						}
						SwitchyProxy.LOGGER.info("[Switchy Proxy] Original | <{}> {}", player.getGameProfile().getName(), content);
						return match.strip(content);
					}
				}
			}
			return content;
		});

		ServerMessageEvents.CHAT_MESSAGE.register(((message, sender, params) -> {
			if (sender instanceof SwitchyPlayer sp) {
				SwitchyPresets presets = sp.switchy$getPresets();
				if (presets.containsModule(DROGTOR_ID) && presets.isModuleEnabled(DROGTOR_ID)) {
					DrogtorCompat.apply(sender, presets.getCurrentPreset());
				}
				if (presets.containsModule(STYLED_ID) && presets.isModuleEnabled(STYLED_ID)) {
					StyledNicknamesCompat.apply(sender, presets.getCurrentPreset());
				}
			}
		}));
	}
}
