package folk.sisby.switchy_proxy.modules;

import folk.sisby.switchy.api.SwitchySerializable;
import folk.sisby.switchy_proxy.ProxyTag;
import folk.sisby.switchy_proxy.SwitchyProxy;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ProxyModuleData implements SwitchySerializable {
	public static final Identifier ID = new Identifier(SwitchyProxy.ID, "proxies");

	public static final String KEY_TAG_LIST = "tags";

	private final Map<String, ProxyTag> tags = new HashMap<>();

	public void addTag(ProxyTag tag) {
		tags.put(tag.toString(), tag);
	}

	public void removeTag(String pattern) throws IllegalArgumentException {
		if (!tags.containsKey(pattern)) throw new IllegalArgumentException("The specified pattern does not exist");
		tags.remove(pattern);
	}

	public Collection<ProxyTag> getTags() {
		return tags.values();
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
}
