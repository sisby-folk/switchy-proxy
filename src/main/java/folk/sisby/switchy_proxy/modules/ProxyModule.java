package folk.sisby.switchy_proxy.modules;

import folk.sisby.switchy.api.SwitchyEvents;
import folk.sisby.switchy.api.module.SwitchyModule;
import folk.sisby.switchy.api.module.SwitchyModuleEditable;
import folk.sisby.switchy.api.module.SwitchyModuleInfo;
import folk.sisby.switchy.api.module.SwitchyModuleRegistry;
import folk.sisby.switchy_proxy.ProxyTag;
import folk.sisby.switchy_proxy.SwitchyProxy;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ProxyModule implements SwitchyModule, SwitchyEvents.Init {
	public static final Identifier ID = new Identifier(SwitchyProxy.ID, "proxies");

	public static final String KEY_TAG_LIST = "tags";

	private final Map<String, ProxyTag> tags = new HashMap<>();

	public void addTag(String pattern, ProxyTag tag) {
		tags.put(pattern, tag);
	}

	public void removeTag(String pattern) {
		tags.remove(pattern);
	}

	public Collection<ProxyTag> getTags() {
		return tags.values();
	}

	@Override
	public void updateFromPlayer(ServerPlayerEntity player, @Nullable String nextPreset) {

	}

	@Override
	public void applyToPlayer(ServerPlayerEntity player) {

	}

	@Override
	public NbtCompound toNbt() {
		NbtCompound outNbt = new NbtCompound();
		NbtList patternList = new NbtList();
		patternList.addAll(tags.values().stream().map(ProxyTag::toNbt).toList());
		outNbt.put(KEY_TAG_LIST, patternList);
		return outNbt;
	}

	@Override
	public void fillFromNbt(NbtCompound nbt) {
		this.tags.clear();
		nbt.getList(KEY_TAG_LIST, NbtElement.COMPOUND_TYPE).forEach((e) -> {
			if (e instanceof NbtCompound c) tags.put(ProxyTag.fromNbt(c).toString(), ProxyTag.fromNbt(c));
		});
	}

	@Override
	public void onInitialize() {
		SwitchyModuleRegistry.registerModule(ID, ProxyModule::new, new SwitchyModuleInfo(true, SwitchyModuleEditable.ALWAYS_ALLOWED, Text.translatable("commands.switchy_proxy.module.proxy.description")));
	}
}
