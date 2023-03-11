package folk.sisby.switchy_proxy.modules;

import folk.sisby.switchy.api.SwitchyEvents;
import folk.sisby.switchy.api.module.*;
import net.minecraft.nbt.NbtCompound;
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
	public NbtCompound toClientNbt() {
		return toNbt();
	}

	@Override
	public void onInitialize() {
		SwitchyModuleRegistry.registerModule(ID, ProxyModule::new, new SwitchyModuleInfo(true, SwitchyModuleEditable.ALWAYS_ALLOWED, Text.translatable("switchy.switchy_proxy.module.proxies.description"))
				.withDescriptionWhenEnabled(Text.translatable("switchy.switchy_proxy.module.proxies.enabled"))
				.withDescriptionWhenDisabled(Text.translatable("switchy.switchy_proxy.module.proxies.disabled"))
				.withDeletionWarning(Text.translatable("switchy.switchy_proxy.module.proxies.warning"))
		);
	}
}
