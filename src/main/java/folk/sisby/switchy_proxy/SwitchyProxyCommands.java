package folk.sisby.switchy_proxy;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import folk.sisby.switchy.api.SwitchyEvents;
import folk.sisby.switchy.api.SwitchyPlayer;
import folk.sisby.switchy.api.presets.SwitchyPreset;
import folk.sisby.switchy.api.presets.SwitchyPresets;
import folk.sisby.switchy_proxy.modules.ProxyModule;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static folk.sisby.switchy.util.Command.*;
import static folk.sisby.switchy.util.Feedback.*;
import static folk.sisby.switchy.util.Feedback.literal;

public class SwitchyProxyCommands implements SwitchyEvents.CommandInit {
	@Override
	public void registerCommands(LiteralArgumentBuilder<ServerCommandSource> switchyArgument, Consumer<Text> helpTextRegistry) {
		switchyArgument
				.then(CommandManager.literal("proxy")
						.then(CommandManager.argument("preset", StringArgumentType.word())
								.suggests((c, b) -> suggestPresets(c, b, true))
								.then(CommandManager.literal("add")
										.then(CommandManager.argument("pattern", StringArgumentType.greedyString())
												.executes(c -> execute(c, (player, presets) -> addProxy(player, presets, c.getArgument("preset", String.class), c.getArgument("pattern", String.class)))))
								)
								.then(CommandManager.literal("remove")
										.then(CommandManager.argument("pattern", StringArgumentType.greedyString())
												.suggests(SwitchyProxyCommands::suggestPatterns)
												.executes(c -> execute(c, (player, presets) -> removeProxy(player, presets, c.getArgument("preset", String.class), c.getArgument("pattern", String.class)))))
								)
						)
				);
	}

	public static CompletableFuture<Suggestions> suggestPatterns(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
		SwitchyPresets presets = ((SwitchyPlayer) context.getSource().getPlayer()).switchy$getPresets();
		try {
			String name = context.getArgument("preset", String.class);
			ProxyModule module = presets.getPreset(name).getModule(ProxyModule.ID, ProxyModule.class);
			CommandSource.suggestMatching(module.getTags().stream().map(ProxyTag::toString), builder);
		} catch (IllegalArgumentException ignored) {

		}
		return builder.buildFuture();
	}

	public void addProxy(ServerPlayerEntity player, SwitchyPresets presets, String name, String pattern) {
		try {
			SwitchyPreset preset = presets.getPreset(name);
			try {
				ProxyModule module = preset.getModule(ProxyModule.ID, ProxyModule.class);
				try {
					module.addTag(ProxyTag.parse(pattern));
					tellSuccess(player, "commands.switchy_proxy.add.success", literal(pattern), literal(name));
				} catch (IllegalArgumentException ignored) {
					tellInvalid(player, "commands.switchy_proxy.add.fail.invalid", literal("text"));
				}
			} catch (IllegalArgumentException ignored) {
				tellInvalidTry(player, "commands.switchy_proxy.add.fail.module", "commands.switchy.module.enable.command", literal(ProxyModule.ID.toString()));
			}
		} catch (IllegalArgumentException ignored) {
			tellInvalidTry(player, "commands.switchy_proxy.add.fail.preset", "commands.switchy.list.command");
		}
	}

	public void removeProxy(ServerPlayerEntity player, SwitchyPresets presets, String name, String pattern) {
		try {
			SwitchyPreset preset = presets.getPreset(name);
			try {
				ProxyModule module = preset.getModule(ProxyModule.ID, ProxyModule.class);
				try {
					module.removeTag(pattern);
					tellSuccess(player, "commands.switchy_proxy.remove.success", literal(pattern), literal(name));
				} catch (IllegalArgumentException ignored) {
					tellInvalid(player, "commands.switchy_proxy.remove.fail.pattern");
				}
			} catch (IllegalArgumentException ignored) {
				tellInvalidTry(player, "commands.switchy_proxy.remove.fail.module", "commands.switchy.module.enable.command", literal(ProxyModule.ID.toString()));
			}
		} catch (IllegalArgumentException ignored) {
			tellInvalidTry(player, "commands.switchy_proxy.remove.fail.preset", "commands.switchy.list.command");
		}
	}
}
