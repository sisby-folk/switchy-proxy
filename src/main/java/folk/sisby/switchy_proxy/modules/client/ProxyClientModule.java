package folk.sisby.switchy_proxy.modules.client;

import com.mojang.datafixers.util.Pair;
import folk.sisby.switchy.client.api.SwitchyClientEvents;
import folk.sisby.switchy.client.api.module.SwitchyClientModule;
import folk.sisby.switchy.client.api.module.SwitchyClientModuleRegistry;
import folk.sisby.switchy.ui.api.SwitchyUIPosition;
import folk.sisby.switchy.ui.api.module.SwitchyUIModule;
import folk.sisby.switchy.util.Feedback;
import folk.sisby.switchy_proxy.ProxyTag;
import folk.sisby.switchy_proxy.modules.ProxyModuleData;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.core.Component;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;

public class ProxyClientModule extends ProxyModuleData implements SwitchyClientModule, SwitchyUIModule, SwitchyClientEvents.Init {
	@Override
	public @Nullable Pair<Component, SwitchyUIPosition> getPreviewComponent(String presetName) {
		if (getTags().isEmpty()) return null;
		return Pair.of(Components.label(Feedback.literal(getTags().stream().map(ProxyTag::toString).collect(Collectors.joining(" | "))).setStyle(Style.EMPTY.withItalic(true).withColor(Formatting.DARK_GRAY))), SwitchyUIPosition.LEFT);
	}

	@Override
	public void onInitialize() {
		SwitchyClientModuleRegistry.registerModule(ID, ProxyClientModule::new);
	}
}
