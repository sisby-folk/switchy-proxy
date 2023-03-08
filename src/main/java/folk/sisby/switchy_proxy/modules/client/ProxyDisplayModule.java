package folk.sisby.switchy_proxy.modules.client;

import com.mojang.datafixers.util.Pair;
import folk.sisby.switchy.client.api.SwitchyClientEvents;
import folk.sisby.switchy.client.api.SwitchySwitchScreenPosition;
import folk.sisby.switchy.client.api.module.SwitchyDisplayModule;
import folk.sisby.switchy.client.api.module.SwitchyDisplayModuleRegistry;
import folk.sisby.switchy_proxy.ProxyTag;
import folk.sisby.switchy_proxy.modules.ProxyModuleData;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.core.Component;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;

public class ProxyDisplayModule extends ProxyModuleData implements SwitchyDisplayModule, SwitchyClientEvents.Init {
	@Override
	public @Nullable Pair<Component, SwitchySwitchScreenPosition> getDisplayComponent() {
		if (getTags().isEmpty()) return null;
		return Pair.of(Components.label(Text.literal(getTags().stream().map(ProxyTag::toString).collect(Collectors.joining(" | "))).setStyle(Style.EMPTY.withItalic(true).withColor(Formatting.DARK_GRAY))), SwitchySwitchScreenPosition.LEFT);
	}

	@Override
	public void onInitialize() {
		SwitchyDisplayModuleRegistry.registerModule(ID, ProxyDisplayModule::new);
	}
}
