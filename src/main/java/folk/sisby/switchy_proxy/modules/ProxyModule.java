package folk.sisby.switchy_proxy.modules;

import folk.sisby.switchy.api.SwitchyEvents;
import folk.sisby.switchy.api.module.*;
import folk.sisby.switchy_proxy.SwitchyProxyCommands;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class ProxyModule extends ProxyModuleData implements SwitchyModule, SwitchyModuleTransferable, SwitchyEvents.Init {
	public void updateFromPlayer(ServerPlayerEntity player, @Nullable String nextPreset) {
	}

	@Override
	public void applyToPlayer(ServerPlayerEntity player) {
	}

	@Override
	public void onInitialize() {
		SwitchyModuleRegistry.registerModule(ID, ProxyModule::new, new SwitchyModuleInfo(true, SwitchyModuleEditable.ALWAYS_ALLOWED, Text.translatable("switchy.switchy_proxy.module.proxies.description"))
				.withDescriptionWhenEnabled(Text.translatable("switchy.switchy_proxy.module.proxies.enabled"))
				.withDescriptionWhenDisabled(Text.translatable("switchy.switchy_proxy.module.proxies.disabled"))
				.withDeletionWarning(Text.translatable("switchy.switchy_proxy.module.proxies.warning"))
				.withModuleConfig(ProxyModuleConfig::new)
				.withConfigCommands(root -> {
					root.then(SwitchyProxyCommands.COMMAND_ADD);
					root.then(SwitchyProxyCommands.COMMAND_REMOVE);
					root.then(SwitchyProxyCommands.COMMAND_LATCH);
				})
		);
	}
}
